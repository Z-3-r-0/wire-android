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
package com.waz.zclient;

import com.waz.api.ImageAsset;
import com.waz.api.Self;
import com.waz.zclient.appentry.PhoneSetNameFragment;
import com.waz.zclient.appentry.VerifyPhoneFragment;
import com.waz.zclient.core.stores.api.ZMessagingApiStoreObserver;
import com.waz.zclient.fragments.CountryDialogFragment;
import com.waz.zclient.newreg.fragments.PhoneAddEmailFragment;
import com.waz.zclient.newreg.fragments.SignUpPhotoFragment;
import com.waz.zclient.newreg.fragments.country.CountryController;

import scala.Function0;
import scala.runtime.BoxedUnit;

public class AppEntryTestActivity extends TestActivity implements VerifyPhoneFragment.Container,
                                                                       PhoneSetNameFragment.Container,
                                                                       PhoneAddEmailFragment.Container,
                                                                       SignUpPhotoFragment.Container,
                                                                       InAppWebViewFragment.Container,
                                                                       CountryDialogFragment.Container,
                                                                       ZMessagingApiStoreObserver {
    @Override
    public void onOpenUrl(String url) {

    }

    @Override
    public int getAccentColor() {
        return 0;
    }

    @Override
    public void enableProgress(boolean enabled) {

    }

    @Override
    public CountryController getCountryController() {
        return null;
    }


    @Override
    public void onInitialized(Self self) {

    }

    @Override
    public void onLogout() {

    }

    @Override
    public void onForceClientUpdate() {

    }

    @Override
    public void dismissInAppWebView() {

    }

    @Override
    public ImageAsset getUnsplashImageAsset() {
        return null;
    }

    @Override
    public void showError(EntryError entryError, Function0<BoxedUnit> okCallback) {

    }

    @Override
    public void showError$default$2() {

    }
}


