package com.sakebook.android.sample.adobecamera;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.adobe.creativesdk.foundation.AdobeCSDKFoundation;
import com.adobe.creativesdk.foundation.auth.IAdobeAuthClientCredentials;

/**
 * Created by sakemotoshinya on 16/03/05.
 */
public class MainApplication extends Application implements IAdobeAuthClientCredentials {
    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }

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
    public String[] getAdditionalScopesList() {
        return new String[0];
    }

    @Override
    public String getRedirectURI() {
        return null;
    }
}
