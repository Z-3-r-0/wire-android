/**
 * Wire
 * Copyright (C) 2016 Wire Swiss GmbH
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
package com.waz.zclient.newreg.utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.annotation.Nullable;

import com.waz.zclient.EntryError;
import com.waz.zclient.R;
import com.waz.zclient.utils.ViewUtils;

public final class AppEntryUtil {

    private AppEntryUtil() { }

    public static void showErrorDialog(Activity activity,
                                       EntryError appEntryError,
                                       @Nullable final ErrorDialogCallback errorDialogCallback) {
        ViewUtils.showAlertDialog(activity,
                                  appEntryError.headerResource(),
                                  appEntryError.bodyResource(),
                                  R.string.reg__phone_alert__button,
                                  new DialogInterface.OnClickListener() {
                                      @Override
                                      public void onClick(DialogInterface dialog, int which) {
                                          dialog.dismiss();
                                          if (errorDialogCallback != null) {
                                              errorDialogCallback.onOk();
                                          }

                                      }
                                  },
                                  false);

    }

    public interface ErrorDialogCallback {
        void onOk();
    }
}
