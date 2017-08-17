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
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.waz.service.ZMessaging
import com.waz.threading.Threading
import com.waz.utils.returning
import com.waz.zclient.{AppEntryController, FragmentHelper, R}
import com.waz.zclient.pages.BaseFragment
import com.waz.zclient.ui.views.ZetaButton

object FirstLaunchAfterLoginFragment {
  val TAG: String = classOf[FirstLaunchAfterLoginFragment].getName

  def newInstance: Fragment = new FirstLaunchAfterLoginFragment

  trait Container {}
}

class FirstLaunchAfterLoginFragment extends BaseFragment[FirstLaunchAfterLoginFragment.Container] with FragmentHelper with View.OnClickListener {

  lazy val appEntryController = inject[AppEntryController]

  private lazy val registerButton = returning(findById[ZetaButton](getView, R.id.zb__first_launch__confirm)){ v =>
    v.setIsFilled(true)
    v.setAccentColor(ContextCompat.getColor(getContext, R.color.text__primary_dark))
  }


  override def onViewCreated(view: View, savedInstanceState: Bundle) = {
    registerButton
  }

  override def onCreateView(inflater: LayoutInflater, viewGroup: ViewGroup, savedInstanceState: Bundle): View =
    inflater.inflate(R.layout.fragment_login_first_launch, viewGroup, false)

  override def onResume() {
    super.onResume()
    registerButton.setOnClickListener(this)
  }

  override def onPause() {
    registerButton.setOnClickListener(null)
    super.onPause()
  }

  def onClick(view: View) {
    view.getId match {
      case R.id.zb__first_launch__confirm =>
        onConfirmClicked()
    }
  }

  private def onConfirmClicked() {
    appEntryController.currentAccount.head.map {
      case (Some(acc), _) => ZMessaging.currentAccounts.setLoggedIn(acc.id)
      case _ =>
    } (Threading.Ui)
  }
}
