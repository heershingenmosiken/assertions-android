package com.heershingenmosiken.assertions.android

import android.os.Handler
import android.os.Looper
import com.heershingenmosiken.assertions.*

object AndroidAssertions : DefaultAssertions() {

    /**
     * Application would crash if assertion happens (if it is not silent assertion).
     */
    fun shouldCrashOnAssertion(crashOnAssertions: Boolean) {
        if (crashOnAssertions) {
            Assertions.addAssertionHandler(AndroidAssertionHandler, Int.MIN_VALUE)
        } else {
            Assertions.removeAssertionHandler(AndroidAssertionHandler)
        }
    }

    /**
     * Ensures that method called on UI thread, else raises exception provided by ThrowableFactory.
     */
    fun assertUIThread(throwableFactory: ThrowableFactory) {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            Assertions.fail(throwableFactory)
        }
    }

    /**
     * Ensures that method called on UI thread, else raises exception provided by ThrowableFactory.
     */
    fun assertNotUIThread(throwableFactory: ThrowableFactory) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            Assertions.fail(throwableFactory)
        }
    }
}

object AndroidAssertionHandler : AssertionHandler {

    private val UI_HANDLER = Handler(Looper.getMainLooper())

    override fun handle(assertionData: AssertionData) {
        if (!assertionData.silent) {
            UI_HANDLER.post(ThrowDelegateRunnable(assertionData.throwable))
        }
    }

}

private class ThrowDelegateRunnable constructor(throwable: Throwable) : Runnable {

    private val runtimeException: RuntimeException = RuntimeException(throwable)

    override fun run() {
        throw runtimeException
    }
}