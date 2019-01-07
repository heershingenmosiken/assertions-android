package com.heershingenmosiken.assertions.android

import android.os.Handler
import android.os.Looper
import com.heershingenmosiken.assertions.AssertionData
import com.heershingenmosiken.assertions.AssertionHandler

object AndroidAssertionHandler : AssertionHandler {

    private val UI_HANDLER = Handler(Looper.getMainLooper())

    override fun handle(assertionData: AssertionData) {
        if (!assertionData.silent) {
            UI_HANDLER.post(ThrowDelegateRunnable(assertionData.throwable))
        }
    }

}