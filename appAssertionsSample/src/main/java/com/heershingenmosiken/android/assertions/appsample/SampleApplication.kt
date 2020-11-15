package com.heershingenmosiken.android.assertions.appsample

import android.app.Application
import android.util.Log
import com.heershingenmosiken.assertions.AssertionData
import com.heershingenmosiken.assertions.AssertionHandler
import com.heershingenmosiken.assertions.android.AndroidAssertions

class SampleApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidAssertions.shouldCrashOnAssertion(BuildConfig.DEBUG)
        AndroidAssertions.addAssertionHandler(object : AssertionHandler {

            override fun log(message: String?) {
                Log.i("Assertion", "logging of $message")
            }

            override fun handle(assertionData: AssertionData) {
                // here yoy should send event to Crashlytics or Firebase.
                // this callback will be invoked despite of shouldCrashOnAssertion call.
                Log.e("Assertion", "assertion happens silently = ${assertionData.silent}", assertionData.throwable)
            }
        })
    }
}
