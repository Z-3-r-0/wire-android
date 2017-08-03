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
import com.waz.zclient.core.controllers.tracking.events.Event;
import com.waz.zclient.core.stores.api.ZMessagingApiStoreObserver;
import com.waz.zclient.core.stores.appentry.AppEntryStateCallback;
import com.waz.zclient.fragments.CountryDialogFragment;
import com.waz.zclient.newreg.fragments.EmailInvitationFragment;
import com.waz.zclient.newreg.fragments.EmailRegisterFragment;
import com.waz.zclient.newreg.fragments.EmailSignInFragment;
import com.waz.zclient.newreg.fragments.EmailVerifyEmailFragment;
import com.waz.zclient.newreg.fragments.PhoneAddEmailFragment;
import com.waz.zclient.newreg.fragments.PhoneInvitationFragment;
import com.waz.zclient.newreg.fragments.PhoneRegisterFragment;
import com.waz.zclient.newreg.fragments.PhoneSetNameFragment;
import com.waz.zclient.newreg.fragments.PhoneSignInFragment;
import com.waz.zclient.newreg.fragments.PhoneVerifyEmailFragment;
import com.waz.zclient.newreg.fragments.SignUpPhotoFragment;
import com.waz.zclient.newreg.fragments.VerifyPhoneFragment;
import com.waz.zclient.newreg.fragments.WelcomeEmailFragment;
import com.waz.zclient.newreg.fragments.country.CountryController;

public class AppEntryTestActivity extends TestActivity implements VerifyPhoneFragment.Container,
                                                                       PhoneRegisterFragment.Container,
                                                                       PhoneSignInFragment.Container,
                                                                       PhoneSetNameFragment.Container,
                                                                       PhoneAddEmailFragment.Container,
                                                                       PhoneVerifyEmailFragment.Container,
                                                                       SignUpPhotoFragment.Container,
                                                                       EmailRegisterFragment.Container,
                                                                       EmailSignInFragment.Container,
                                                                       EmailVerifyEmailFragment.Container,
                                                                       WelcomeEmailFragment.Container,
                                                                       EmailInvitationFragment.Container,
                                                                       PhoneInvitationFragment.Container,
                                                                       InAppWebViewFragment.Container,
                                                                       CountryDialogFragment.Container,
                                                                       AppEntryStateCallback,
                                                                       ZMessagingApiStoreObserver {

    @Override
    public void onShowPhoneInvitationPage() {

    }

    @Override
    public void onShowEmailInvitationPage() {

    }

    @Override
    public void onInvitationFailed() {

    }

    @Override
    public void onInvitationSuccess() {

    }

    @Override
    public void onShowPhoneRegistrationPage() {

    }

    @Override
    public void onShowPhoneSignInPage() {

    }

    @Override
    public void onShowPhoneCodePage() {

    }

    @Override
    public void onShowPhoneAddEmailPage() {

    }

    @Override
    public void onShowPhoneVerifyEmailPage() {

    }

    @Override
    public void onShowPhoneNamePage() {

    }

    @Override
    public void onEnterApplication(boolean openSettings) {

    }

    @Override
    public void onShowPhoneSetPicturePage() {

    }

    @Override
    public void onShowEmailWelcomePage() {

    }

    @Override
    public void onShowEmailRegistrationPage() {

    }

    @Override
    public void onShowEmailVerifyEmailPage() {

    }

    @Override
    public void onShowEmailSignInPage() {

    }

    @Override
    public void onShowEmailSetPicturePage() {

    }

    @Override
    public void onShowFirstLaunchPage() {

    }

    @Override
    public void tagAppEntryEvent(Event event) {

    }

    @Override
    public void onOpenUrl(String url) {

    }

    @Override
    public void onOpenUrlInApp(String url, boolean withCloseButton) {

    }

    @Override
    public int getAccentColor() {
        return 0;
    }

    @Override
    public void enableProgress(boolean enabled) {

    }

    @Override
    public void abortAddAccount() {

    }

    @Override
    public void openCountryBox() {

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
}


