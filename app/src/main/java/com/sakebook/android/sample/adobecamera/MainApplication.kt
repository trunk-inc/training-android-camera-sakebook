package com.sakebook.android.sample.adobecamera

import android.app.Application
import android.content.Context
import android.support.multidex.MultiDex

import com.adobe.creativesdk.foundation.AdobeCSDKFoundation
import com.adobe.creativesdk.foundation.auth.IAdobeAuthClientCredentials

/**
 * Created by sakemotoshinya on 16/03/05.
 */
class MainApplication : Application(), IAdobeAuthClientCredentials {
    override fun attachBaseContext(context: Context) {
        super.attachBaseContext(context)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        AdobeCSDKFoundation.initializeCSDKFoundation(applicationContext)
    }

    override fun getClientID(): String {
        return BuildConfig.CREATIVE_SDK_CLIENT_ID
    }

    override fun getClientSecret(): String {
        return BuildConfig.CREATIVE_SDK_CLIENT_SECRET
    }

    override fun getAdditionalScopesList(): Array<String> {
        return arrayOf()
    }

    override fun getRedirectURI(): String? {
        return null
    }
}
