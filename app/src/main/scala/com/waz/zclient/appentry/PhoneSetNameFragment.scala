/**
  * Wire
  * Copyright (C) 2017 Wire Swiss GmbH
  *
  * This program is free software: you can redistribute it and/or modify
  * it under the terms of the GNU General Public License as published by
  * the Free Software Foundation, either version 3 of the License, or
  * (at your option) any later version.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU General Public License for more details.
  *
  * You should have received a copy of the GNU General Public License
  * along with this program.  If not, see <http://www.gnu.org/licenses/>.
  */
package com.waz.zclient.appentry

import android.os.Bundle
import android.text.{Editable, TextWatcher}
import android.view.{KeyEvent, LayoutInflater, View, ViewGroup}
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.waz.threading.Threading
import com.waz.zclient._
import com.waz.zclient.controllers.navigation.Page
import com.waz.zclient.newreg.views.PhoneConfirmationButton
import com.waz.zclient.pages.BaseFragment
import com.waz.zclient.ui.text.TypefaceEditText
import com.waz.zclient.ui.utils.KeyboardUtils
import com.waz.zclient.utils.StringUtils

object PhoneSetNameFragment {
  val TAG = classOf[PhoneSetNameFragment].getName

  def newInstance: PhoneSetNameFragment = new PhoneSetNameFragment

  trait Container {
    def enableProgress(enable: Boolean): Unit
    def getAccentColor: Int
    def showError(entryError: EntryError, okCallback: => Unit = {}): Unit
  }

}

class PhoneSetNameFragment extends BaseFragment[PhoneSetNameFragment.Container] with FragmentHelper with TextWatcher with View.OnClickListener {

  implicit val executionContext = Threading.Ui

  private lazy val appEntryController = inject[AppEntryController]

  private lazy val editTextName = findById[TypefaceEditText](getView, R.id.et__reg__name)
  private lazy val nameConfirmationButton = findById[PhoneConfirmationButton](getView, R.id.pcb__signup)

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    inflater.inflate(R.layout.fragment_phone__name, container, false)
  }

  override def onStart(): Unit = {
    super.onStart()
    editTextName.requestFocus
    nameConfirmationButton.setAccentColor(getContainer.getAccentColor)
  }

  override def onResume(): Unit = {
    super.onResume()
    editTextName.addTextChangedListener(this)
    nameConfirmationButton.setOnClickListener(this)
    editTextName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      def onEditorAction(v: TextView, actionId: Int, event: KeyEvent): Boolean = {
        if (actionId == EditorInfo.IME_ACTION_DONE && isNameValid(editTextName.getText.toString)) {
          confirmName()
          true
        } else {
          false
        }
      }
    })
    getControllerFactory.getGlobalLayoutController.setSoftInputModeForPage(Page.PHONE_REGISTRATION)
    KeyboardUtils.showKeyboard(getActivity)
  }

  override def onPause(): Unit = {
    editTextName.removeTextChangedListener(this)
    nameConfirmationButton.setOnClickListener(null)
    super.onPause()
  }

  override def onStop(): Unit = {
    KeyboardUtils.hideKeyboard(getActivity)
    super.onStop()
  }

  private def confirmName(): Unit = {
    getContainer.enableProgress(true)
    KeyboardUtils.hideKeyboard(getActivity)
    val name: String = editTextName.getText.toString
    appEntryController.registerName(name).map {
      case Left(error) =>
        getContainer.enableProgress(false)
        getContainer.showError(error, {
          if (getActivity == null) return
          KeyboardUtils.showKeyboard(getActivity)
          editTextName.requestFocus
          nameConfirmationButton.setState(PhoneConfirmationButton.State.INVALID)
        })
      case _ =>
    }
  }

  def onClick(view: View): Unit = {
    view.getId match {
      case R.id.pcb__signup =>
        confirmName()
    }
  }

  def beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int): Unit = {}

  def onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int): Unit = {
    nameConfirmationButton.setState(validateName(charSequence.toString))
  }

  def afterTextChanged(s: Editable): Unit = {}

  private def validateName(name: String): PhoneConfirmationButton.State = {
    if (isNameValid(name))
      PhoneConfirmationButton.State.CONFIRM
    else
      PhoneConfirmationButton.State.NONE
  }

  private def isNameValid(name: String): Boolean = !StringUtils.isBlank(name)

}
