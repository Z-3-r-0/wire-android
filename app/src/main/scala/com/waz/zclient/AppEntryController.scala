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

import com.waz.ZLog
import com.waz.ZLog.ImplicitTag._
import com.waz.api.{ClientRegistrationState, KindOfAccess}
import com.waz.api.impl._
import com.waz.client.RegistrationClient
import com.waz.client.RegistrationClient.ActivateResult.{Failure, PasswordExists}
import com.waz.model._
import com.waz.service.ZMessaging
import com.waz.threading.Threading
import com.waz.utils.events.{EventContext, Signal}
import com.waz.zclient.AppEntryController._
import com.waz.zclient.controllers.SignInController._

import scala.concurrent.Future

//TODO: Invitation token!
class AppEntryController(implicit inj: Injector, eventContext: EventContext) extends Injectable {

  implicit val ec = Threading.Background

  lazy val optZms = inject[Signal[Option[ZMessaging]]]

  val currentAccount = for {
    accountData <- ZMessaging.currentAccounts.activeAccount
    optZms <- optZms
    userData <- optZms.fold(Signal.const(Option.empty[UserData]))(z => z.usersStorage.optSignal(z.selfUserId))
  } yield (accountData, userData)

  val entryStage = for {
    (account, user) <- currentAccount
  } yield stateForAccountAndUser(account, user)

  currentAccount{ acc => ZLog.debug(s"Current account: $acc")}
  entryStage{ stage => ZLog.debug(s"Current stage: $stage")}

  def stateForAccountAndUser(account: Option[AccountData], user: Option[UserData]): AppEntryStage = {
    account.fold[AppEntryStage] {
      LoginStage
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
        return LoginStage //Unverified account with no pending stuff
      }
      if (accountData.regWaiting) {
        return AddNameStage
      }
      user.fold[AppEntryStage] {
        Unknown //Has account but has no user, should be temporary
      } { userData =>
        if (userData.picture.isEmpty) {
          return AddPictureStage
        }
        EnterAppStage
      }
    }
  }

  def loginPhone(phone: String): Future[Either[EntryError, Unit]] = {
    ZMessaging.currentAccounts.loginPhone(PhoneNumber(phone)).map {
      case Left(error) => Left(EntryError(error.code, error.label, SignInMethod(Login, Phone)))
      case Right(_) => Right(())
    }
  }

  def loginEmail(email: String, password: String): Future[Either[EntryError, Unit]] = {
    ZMessaging.currentAccounts.loginEmail(EmailAddress(email), password).map {
      case Left(error) => Left(EntryError(error.code, error.label, SignInMethod(Login, Email)))
      case _ => Right(())
    }
  }

  def registerEmail(name: String, email: String, password: String): Future[Either[EntryError, Unit]] = {
    ZMessaging.currentAccounts.registerEmail(EmailAddress(email), password, name).map {
      case Left(error) => Left(EntryError(error.code, error.label, SignInMethod(Register, Email)))
      case _ => Right(())
    }
  }

  def registerPhone(phone: String): Future[Either[EntryError, Unit]] = {
    ZMessaging.currentAccounts.register(PhoneCredentials(PhoneNumber(phone), None), None, AccentColor()).map {
      case Left(error) => Left(EntryError(error.code, error.label, SignInMethod(Register, Phone)))
      case _ => Right(())
    }
  }

  def verifyPhone(code: String): Future[Either[EntryError, Unit]] = {
    ZMessaging.currentAccounts.activeAccount.head.flatMap {
      case Some(accountData) if accountData.pendingPhone.isDefined && accountData.regWaiting =>
        ZMessaging.currentAccounts.activatePhoneOnRegister(accountData.pendingPhone.get, ConfirmationCode(code)).map {
          case Left(error) => Left(EntryError(error.code, error.label, SignInMethod(Register, Phone)))
          case _ => Right(())
        }
      case Some(accountData) if accountData.pendingPhone.isDefined =>
        ZMessaging.currentAccounts.loginPhone(accountData.pendingPhone.get, ConfirmationCode(code)).map {
          case Left(error) => Left(EntryError(error.code, error.label, SignInMethod(Login, Phone)))
          case _ => Right(())
        }
      case _ => Future.successful(Left(GenericRegisterPhoneError))
    }
  }

  def registerName(name: String): Future[Either[EntryError, Unit]] = {
    ZMessaging.currentAccounts.activeAccount.head.flatMap {
      case Some(accountData) if accountData.phone.isDefined && accountData.regWaiting && accountData.code.isDefined =>
        ZMessaging.currentAccounts.registerNameOnPhone(accountData.phone.get, ConfirmationCode(accountData.code.get), name).map {
          case Left(error) => Left(EntryError(error.code, error.label, SignInMethod(Register, Phone)))
          case _ => Right(())
        }
      case _ => Future.successful(Left(GenericRegisterPhoneError))
    }
  }

  def resendActivationEmail(): Unit = {
    //TODO: do.
  }

  //TODO: register and login
  def resendActivationPhoneCode(shouldCall: Boolean = false): Future[Either[EntryError, Unit]] = {

    def requestCode(accountData: AccountData, kindOfAccess: KindOfAccess): Future[RegistrationClient.ActivateResult] = {
      if (shouldCall)
        ZMessaging.currentAccounts.requestPhoneConfirmationCall(accountData.pendingPhone.get, kindOfAccess).future
      else
        ZMessaging.currentAccounts.requestPhoneConfirmationCode(accountData.pendingPhone.get, kindOfAccess).future
    }

    ZMessaging.currentAccounts.getActiveAccount.flatMap {
      case Some(account) if account.pendingPhone.isDefined =>
        val kindOfAccess = if (account.regWaiting) KindOfAccess.REGISTRATION else KindOfAccess.LOGIN_IF_NO_PASSWD
        requestCode(account, kindOfAccess).map {
          case Failure(error) =>
            Left(EntryError(error.code, error.label, SignInMethod(Register, Phone)))
          case PasswordExists =>
            val validationType = if (account.regWaiting) Register else Login
            Left(EntryError(ErrorResponse.PasswordExists.code, ErrorResponse.PasswordExists.label, SignInMethod(validationType, Phone)))
          case _ =>
            Right(())
        }
      case _ => Future.successful(Left(GenericRegisterPhoneError))
    }
  }

  def cancelVerification(): Unit = ZMessaging.currentAccounts.logout(true)

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
  object LoginStage       extends AppEntryStage
}
