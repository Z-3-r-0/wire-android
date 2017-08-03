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
package com.waz.zclient

import com.waz.api.impl._
import com.waz.api.{ClientRegistrationState, KindOfAccess}
import com.waz.client.RegistrationClient.ActivateResult.{Failure, PasswordExists}
import com.waz.model._
import com.waz.service.ZMessaging
import com.waz.threading.Threading
import com.waz.utils.events.{EventContext, Signal}
import com.waz.zclient.AppEntryController._
import com.waz.zclient.newreg.fragments.country.Country

import scala.concurrent.Future

//TODO: Invitation token!
class AppEntryController(implicit inj: Injector, eventContext: EventContext) extends Injectable {

  implicit val ec = Threading.Background

  val optZms = inject[Signal[Option[ZMessaging]]]

  val currentAccount = for {
    accountData <- ZMessaging.currentAccounts.activeAccount
    optZms <- optZms
    userData <- optZms.fold(Signal.const(Option.empty[UserData]))(z => z.usersStorage.optSignal(z.selfUserId))
  } yield (accountData, userData)

  val uiSignInState = Signal[UiSignInState](LoginEmail)

  val entryStage = for {
    uiSignInState <- uiSignInState
    (account, user) <- currentAccount
  } yield stateForAccountAndUser(account, user, uiSignInState)

  val phoneCountry = Signal[Country]()

  def stateForAccountAndUser(account: Option[AccountData], user: Option[UserData], uiSignInState: UiSignInState): AppEntryStage = {
    account.fold[AppEntryStage] {
      LoginStage(uiSignInState)
    } { accountData =>
      if (accountData.clientRegState == ClientRegistrationState.LIMIT_REACHED) {
        return DeviceLimitStage
      }
      if (!accountData.verified) {
        if (accountData.pendingEmail.isDefined && accountData.password.isDefined) {
          return VerifyEmailStage
        }
        if (accountData.pendingPhone.isDefined) {
          return VerifyPhoneStage
        }
        return LoginStage(uiSignInState) //Unverified account with no pending stuff
      }
      user.fold[AppEntryStage] {
        Unknown //Has account but has no user, should be temporary
      } { userData =>
        if (userData.name.isEmpty) {
          return AddNameStage
        }
        if (userData.picture.isEmpty) {
          return AddPictureStage
        }
        EnterAppStage
      }
    }
  }

  def loginPhone(phone: String): Future[Either[EntryError, Unit]] = {
    login(PhoneCredentials(PhoneNumber(phone), Option.empty[ConfirmationCode])) map {
      case Left(error) => Left(EntryError(error.code, error.label))
      case _ => Right(())
    }
  }

  def loginEmail(email: String, password: String): Future[Either[EntryError, Unit]] = {
    login(EmailCredentials(EmailAddress(email), Some(password))) map {
      case Left(error) => Left(EntryError(error.code, error.label))
      case _ => Right(())
    }
  }

  def registerEmail(name: String, email: String, password: String): Future[Either[EntryError, Unit]] = {
    register(EmailCredentials(EmailAddress(email), Some(password)), name) map {
      case Left(error) => Left(EntryError(error.code, error.label))
      case _ => Right(())
    }
  }

  def registerPhone(phone: String): Future[Either[EntryError, Unit]] = {
    ZMessaging.currentAccounts.requestPhoneConfirmationCode(PhoneNumber(phone), KindOfAccess.REGISTRATION) map {
      case PasswordExists => Left(PhoneExistsError)
      case Failure(error) => Left(EntryError(error.code, error.label))
      case _ => Right(())
    }
  }

  def login(credentials: Credentials): Future[Either[ErrorResponse, AccountData]] = {
    ZMessaging.currentAccounts.login(credentials)
  }

  def register(credentials: Credentials, name: String): Future[Either[ErrorResponse, AccountData]] = {
    ZMessaging.currentAccounts.register(credentials, name, AccentColors.defaultColor)
  }

  def resendActivationEmail(): Unit = {
    ZMessaging.currentAccounts.getActiveAccount.map {
      _.flatMap(_.pendingEmail).foreach(ZMessaging.currentAccounts.requestVerificationEmail)
    }

  }

  //For Java access
  def goToLoginEmail(): Unit = {
    uiSignInState ! LoginEmail
  }

  def goToLoginPhone(): Unit = {
    uiSignInState ! LoginPhone
  }

  def goToRegisterEmail(): Unit = {
    uiSignInState ! RegisterEmail
  }

  def cancelEmailVerification(): Unit = {
    ZMessaging.currentAccounts.logout(true)
    uiSignInState ! RegisterEmail
  }

  def cancelPhoneVerification(): Unit = {
    ZMessaging.currentAccounts.logout(true)
    uiSignInState ! RegisterPhone
  }

}

object AppEntryController {
  trait AppEntryStage
  object Unknown          extends AppEntryStage
  object EnterAppStage    extends AppEntryStage
  object DeviceLimitStage extends AppEntryStage
  object AddNameStage     extends AppEntryStage
  object AddPictureStage  extends AppEntryStage
  object VerifyEmailStage extends AppEntryStage
  object VerifyPhoneStage extends AppEntryStage

  case class LoginStage(uiSignInState: UiSignInState) extends AppEntryStage

  trait UiSignInState
  object LoginEmail    extends UiSignInState
  object LoginPhone    extends UiSignInState
  object RegisterEmail extends UiSignInState
  object RegisterPhone extends UiSignInState
}
