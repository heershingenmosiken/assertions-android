package com.heershingenmosiken.assertions.tests;

import com.heershingenmosiken.assertions.AssertionHandler;
import com.heershingenmosiken.assertions.Assertions;
import com.heershingenmosiken.assertions.ThrowableFactory;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;


public class AssertionsTests {

    private static Throwable EXCEPTION = new Exception();
    private static ThrowableFactory EXCEPTION_FACTORY = () -> EXCEPTION;

    private static void withAssertions(int expectedAssertionsCount, Runnable runnable) {
        AtomicInteger counter = new AtomicInteger();
        AssertionCounterHandler handler = new AssertionCounterHandler(counter);
        Assertions.addAssertionHandler(handler);

        runnable.run();

        Assertions.removeAssertionHandler(handler);

        org.junit.jupiter.api.Assertions.assertEquals(expectedAssertionsCount, counter.intValue());
    }

    @Test
    void failTests() {

        withAssertions(1, () -> Assertions.fail(EXCEPTION));

        withAssertions(1, () -> Assertions.fail(EXCEPTION));
        withAssertions(1, () -> Assertions.fail(EXCEPTION_FACTORY));
        withAssertions(1, () -> Assertions.failSilently(EXCEPTION));
    }

    @Test
    void assertNullTests() {
        withAssertions(0, () -> Assertions.assertNotNull(new Object(), EXCEPTION_FACTORY));
        withAssertions(0, () -> Assertions.assertNull(null, EXCEPTION_FACTORY));
        withAssertions(1, () -> Assertions.assertNotNull(null, EXCEPTION_FACTORY));
        withAssertions(1, () -> Assertions.assertNull(new Object(), EXCEPTION_FACTORY));
    }

    @Test
    void assertTrueFalseTest() {
        withAssertions(0, () -> Assertions.assertFalse(false, EXCEPTION_FACTORY));
        withAssertions(0, () -> Assertions.assertTrue(true, EXCEPTION_FACTORY));
        withAssertions(1, () -> Assertions.assertFalse(true, EXCEPTION_FACTORY));
        withAssertions(1, () -> Assertions.assertTrue(false, EXCEPTION_FACTORY));
    }

    @Test
    void assertEmptyTests() {
        withAssertions(0, () -> Assertions.assertEmpty(Collections.emptyList(), EXCEPTION_FACTORY));
        withAssertions(0, () -> Assertions.assertNotEmpty(Collections.singleton(new Object()), EXCEPTION_FACTORY));
        withAssertions(1, () -> Assertions.assertEmpty(Collections.singleton(new Object()), EXCEPTION_FACTORY));
        withAssertions(1, () -> Assertions.assertNotEmpty(Collections.emptyList(), EXCEPTION_FACTORY));
    }
}
