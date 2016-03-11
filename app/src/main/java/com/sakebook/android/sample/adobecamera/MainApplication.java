package com.sakebook.android.sample.adobecamera;

import android.app.Application;

import com.adobe.creativesdk.aviary.IAviaryClientCredentials;
import com.adobe.creativesdk.foundation.AdobeCSDKFoundation;

/**
 * Created by sakemotoshinya on 16/03/05.
 */
public class MainApplication extends Application implements IAviaryClientCredentials {
    @Override
    public void onCreate() {
        super.onCreate();
        AdobeCSDKFoundation.initializeCSDKFoundation(getApplicationContext());
    }

    @Override
    public String getClientID() {
        return BuildConfig.CREATIVE_SDK_CLIENT_ID;
    }

    @Override
    public String getClientSecret() {
        return BuildConfig.CREATIVE_SDK_CLIENT_SECRET;
    }

    @Override
    public String getBillingKey() {
        return ""; // Leave this blank
    }
}
