package com.heershingenmosiken.assertions.android;

import android.os.Looper;

import com.heershingenmosiken.assertions.DefaultAssertions;
import com.heershingenmosiken.assertions.ThrowableFactory;

public class DefaultAndroidAssertion extends DefaultAssertions {
    /**
     * Application would crash if assertion happens (if it is not silent assertion).
     */
    public void shouldCrashOnAssertion(boolean crashOnAssertions) {
        if (crashOnAssertions) {
            addAssertionHandler(AndroidAssertionHandler.INSTANCE, Integer.MIN_VALUE);
        } else {
            removeAssertionHandler(AndroidAssertionHandler.INSTANCE);
        }
    }

    /**
     * Ensures that method called on UI thread, else raises exception provided by ThrowableFactory.
     */
    public void assertUIThread(ThrowableFactory throwableFactory) {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            internalRaiseAssertion(throwableFactory, "AssertUIThread", false);
        }
    }

    /**
     * Ensures that method called on UI thread, else raises exception provided by ThrowableFactory.
     */
    public void assertNotUIThread(ThrowableFactory throwableFactory) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            internalRaiseAssertion(throwableFactory, "AssertNotUIThread", false);
        }
    }
}
