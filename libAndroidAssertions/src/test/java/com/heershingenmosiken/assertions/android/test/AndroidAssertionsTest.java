package com.heershingenmosiken.assertions.android.test;

import com.heershingenmosiken.assertions.AssertionData;
import com.heershingenmosiken.assertions.AssertionHandler;
import com.heershingenmosiken.assertions.ThrowableFactory;
import com.heershingenmosiken.assertions.android.AndroidAssertions;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

@RunWith(RobolectricTestRunner.class)
public class AndroidAssertionsTest {

    private static final Throwable EXCEPTION = new Exception();
    private static final ThrowableFactory EXCEPTION_FACTORY = () -> EXCEPTION;

    private static class Result {

        public final boolean success;
        public final String message;

        private Result(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public static Result fail(String message) {
            return new Result(false, message);
        }

        public static Result success() {
            return new Result(true, "");
        }
    }

    private static AssertionData runWithAssertionResult(Runnable runnable) {
        AtomicReference<AssertionData> assertionDataReference = new AtomicReference<>();
        AssertionHandler assertionHandler = assertionDataReference::set;
        AndroidAssertions.addAssertionHandler(assertionHandler);

        runnable.run();

        AndroidAssertions.removeAssertionHandler(assertionHandler);
        return assertionDataReference.get();
    }

    private static Result withAssertion(Runnable runnable, Predicate<AssertionData> checker) {

        AssertionData assertionData = runWithAssertionResult(runnable);

        if (assertionData == null) {
            return Result.fail("Asseriton not happened but expected");
        } else if (!checker.test(assertionData)) {
            return Result.fail("Assertion happens but did not pass validation");
        } else {
            return Result.success();
        }
    }

    private static Result withNoAssertion(Runnable runnable) {

        AssertionData assertionData = runWithAssertionResult(runnable);

        if (assertionData != null) {
            return Result.fail("Assertion happens but was not expected");
        } else {
            return Result.success();
        }
    }

    private static <T> T asyncAction(Callable<T> callable) {
        AtomicReference<T> result = new AtomicReference<>();
        AtomicReference<Throwable> internalError = new AtomicReference<>();

        Thread otherThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    result.set(callable.call());
                } catch (Exception e) {
                    internalError.set(e);
                }
            }
        });

        otherThread.start();

        try {
            otherThread.join();

            if (internalError.get() != null) {
                throw new RuntimeException(internalError.get());
            }

            return result.get();

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testAssertUiThread() {
        Result result = withNoAssertion(() -> AndroidAssertions.assertUIThread(EXCEPTION_FACTORY));
        Assert.assertTrue(result.message, result.success);

        result = asyncAction(() ->
                withAssertion(
                        () -> AndroidAssertions.assertUIThread(EXCEPTION_FACTORY),
                        assertionData -> assertionData.throwable == EXCEPTION && !assertionData.silent && assertionData.message.equals("AssertUIThread")));
        Assert.assertTrue(result.message, result.success);
    }

    @Test
    public void testAssertNotUiThread() {
        Result result = withAssertion(
                () -> AndroidAssertions.assertNotUIThread(EXCEPTION_FACTORY),
                assertionData -> assertionData.throwable == EXCEPTION && !assertionData.silent && assertionData.message.equals("AssertNotUIThread"));
        Assert.assertTrue(result.message, result.success);

        result = asyncAction(() ->
                withNoAssertion(() -> AndroidAssertions.assertNotUIThread(EXCEPTION_FACTORY)));
        Assert.assertTrue(result.message, result.success);
    }
}
