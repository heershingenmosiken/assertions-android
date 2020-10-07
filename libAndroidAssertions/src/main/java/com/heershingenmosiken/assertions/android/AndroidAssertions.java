package com.heershingenmosiken.assertions.android;

import android.os.Handler;
import android.os.Looper;

import com.heershingenmosiken.assertions.AssertionData;
import com.heershingenmosiken.assertions.AssertionHandler;
import com.heershingenmosiken.assertions.Assertions;
import com.heershingenmosiken.assertions.ThrowableFactory;

public class AndroidAssertions extends Assertions {

    /**
     * Application would crash if assertion happens (if it is not silent assertion).
     */
    public static void shouldCrashOnAssertion(boolean crashOnAssertions) {
        if (crashOnAssertions) {
            addAssertionHandler(ANDROID_ASSERTION_HANDLER, Integer.MIN_VALUE);
        } else {
            removeAssertionHandler(ANDROID_ASSERTION_HANDLER);
        }
    }

    /**
     * Ensures that method called on UI thread, else raises exception provided by ThrowableFactory.
     */
    public static void assertUIThread(ThrowableFactory throwableFactory) {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            internalRaiseAssertion(throwableFactory, "AssertUIThread", false);
        }
    }

    /**
     * Ensures that method called on UI thread, else raises exception provided by ThrowableFactory.
     */
    public static void assertNotUIThread(ThrowableFactory throwableFactory) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            internalRaiseAssertion(throwableFactory, "AssertNotUIThread", false);
        }
    }

    private static final AssertionHandler ANDROID_ASSERTION_HANDLER = new AssertionHandler() {

        private Handler UI_HANDLER = new Handler(Looper.getMainLooper());

        @Override
        public void log(String message) {
            // ignore
        }

        @Override
        public void handle(AssertionData assertionData) {
            if (!assertionData.silent) {
                UI_HANDLER.post(new ThrowDelegateRunnable(assertionData.throwable));
            }
        }
    };

    private static class ThrowDelegateRunnable implements Runnable {

        private final RuntimeException runtimeException;

        private ThrowDelegateRunnable(Throwable throwable) {
            this.runtimeException = new RuntimeException(throwable);
        }

        @Override
        public void run() {
            throw runtimeException;
        }
    }
}
