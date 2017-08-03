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



//TODO: Add the access type...
case class EntryError(code: Int, label: String) {
  def headerResource: Int = error.map(_._1).getOrElse(R.string.new_sign_in_generic_error_header)
  def bodyResource: Int = error.map(_._2).getOrElse(R.string.new_sign_in_generic_error_message)

  //TODO: why do some have the same error code?
  private val errorMap = Map[(Int, String), (Int, Int)](
    (409, "key-exists") ->             (R.string.new_reg_email_exists_header, R.string.new_reg_email_exists_message),
    (400, "invalid-email") ->          (R.string.new_reg_email_invalid_header, R.string.new_reg_email_invalid_message),
    (400, "invalid-request") ->        (R.string.new_reg_email_invalid_header, R.string.new_reg_email_invalid_message),
    (0,   "") ->                       (R.string.new_reg_email_generic_error_header, R.string.new_reg_email_generic_error_message),
    (0,   "") ->                       (R.string.new_reg_email_register_generic_error_header, R.string.new_reg_email_register_generic_error_message),
    (403, "invalid-credentials") ->    (R.string.new_reg_email_invalid_login_credentials_header, R.string.new_reg_email_invalid_login_credentials_message),
    (409, "key-exists") ->             (R.string.new_reg_phone_exists_header, R.string.new_reg_phone_exists_message),
    (400, "bad-request") ->            (R.string.new_reg_phone_invalid_format_header, R.string.new_reg_phone_invalid_format_message),
    (404, "invalid-code") ->           (R.string.new_reg_phone_invalid_registration_code_header, R.string.new_reg_phone_invalid_registration_code_message),
    (404, "invalid-code") ->           (R.string.new_reg_phone_invalid_add_code_header, R.string.new_reg_phone_invalid_add_code_message),
    (403, "") ->                       (R.string.new_reg_phone_invalid_login_code_header, R.string.new_reg_phone_invalid_login_code_message),
    (403, "pending-login") ->          (R.string.new_reg_phone_pending_login_header, R.string.new_reg_phone_pending_login_message),
    (403, "unauthorized") ->           (R.string.new_reg_email_generic_error_header, R.string.new_reg_email_generic_error_message),
    (0,   "") ->                       (R.string.new_reg_phone_add_password_header, R.string.new_reg_phone_add_password_message),
    (0,   "") ->                       (R.string.new_reg_phone_generic_error_header, R.string.new_reg_phone_generic_error_message),
    (0,   "") ->                       (R.string.profile_phone_generic_error_header, R.string.profile_phone_generic_error_message),
    (0,   "") ->                       (R.string.profile_generic_error_header, R.string.profile_generic_error_message),
    (429, "") ->                       (R.string.new_reg_phone_too_man_attempts_header, R.string.new_reg_phone_too_man_attempts_message),
    (600, "") ->                       (R.string.new_reg_server_connectivity_error_header, R.string.new_reg_server_connectivity_error_message),
    (0,   "") ->                       (R.string.new_sign_in_generic_error_header, R.string.new_sign_in_generic_error_message),
    (598, "") ->                       (R.string.new_reg_internet_connectivity_error_header, R.string.new_reg_internet_connectivity_error_message),
    (403, "phone-budget-exhausted") -> (R.string.new_reg_phone_budget_exhausted_title, R.string.new_reg_phone_budget_exhausted_message)
  )

  private def error = errorMap.get((code, label))
}

object UnknownError extends EntryError(-1, "")
object PhoneExistsError extends EntryError(409, "key-exists")
