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
package com.waz.zclient.controllers

import android.content.Context
import com.waz.threading.Threading
import com.waz.utils.events.{EventContext, Signal}
import com.waz.zclient.controllers.SignInController._
import com.waz.zclient.newreg.fragments.country.{Country, CountryController}
import com.waz.zclient.pages.main.profile.validator.{EmailValidator, NameValidator, PasswordValidator}
import com.waz.zclient.{AppEntryController, EntryError, Injectable, Injector}

import scala.concurrent.Future

class SignInController(implicit inj: Injector, eventContext: EventContext, context: Context) extends Injectable with CountryController.Observer {

  private lazy val appEntryController = inject[AppEntryController]

  val uiSignInState = Signal[SignInMethod](SignInMethod(Login, Email))

  val email = Signal("")
  val password = Signal("")
  val name = Signal("")
  val phone = Signal("")

  lazy val countryController = new CountryController(context)
  countryController.addObserver(this)
  lazy val phoneCountry = Signal[Country]()

  lazy val nameValidator = new NameValidator()
  lazy val emailValidator = EmailValidator.newInstance()
  lazy val passwordValidator = PasswordValidator.instance(context)
  lazy val legacyPasswordValidator = PasswordValidator.instanceLegacy(context)

  lazy val isValid: Signal[Boolean] = uiSignInState.flatMap {
    case SignInMethod(Login, Email) =>
      for {
        email <- email
        password <- password
      } yield emailValidator.validate(email) && legacyPasswordValidator.validate(password)
    case SignInMethod(Register, Email) =>
      for {
        name <- name
        email <- email
        password <- password
      } yield nameValidator.validate(name) && emailValidator.validate(email) && passwordValidator.validate(password)
    case SignInMethod(_, Phone) =>
      phone.map(_.nonEmpty)
    case _ => Signal.empty[Boolean]
  }

  def attemptSignIn(): Future[Either[EntryError, Unit]] = {
    implicit val ec = Threading.Ui
    uiSignInState.head.flatMap{
      case SignInMethod(Login, Email) =>
        for{
          email <- email.head
          password <- password.head
          response <- appEntryController.loginEmail(email, password)
        } yield response
      case SignInMethod(Login, Phone) =>
        for{
          phone <- phone.head
          code <- phoneCountry.head.map(_.getCountryCode)
          response <- appEntryController.loginPhone(s"+$code$phone")
        } yield response
      case SignInMethod(Register, Email) =>
        for{
          name <- name.head
          email <- email.head
          password <- password.head
          response <- appEntryController.registerEmail(name, email, password)
        } yield response
      case SignInMethod(Register, Phone) =>
        for{
          phone <- phone.head
          code <- phoneCountry.head.map(_.getCountryCode)
          response <- appEntryController.registerPhone(s"+$code$phone")
        } yield response
      case _ => Future.successful(Right(()))
    }
  }

  override def onCountryHasChanged(country: Country): Unit = phoneCountry ! country
}

object SignInController {

  trait SignType
  object Login extends SignType
  object Register extends SignType

  trait InputType
  object Email extends InputType
  object Phone extends InputType

  case class SignInMethod(signType: SignType, inputType: InputType)
}
