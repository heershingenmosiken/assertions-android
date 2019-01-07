package com.heershingenmosiken.assertions.tests;

import com.heershingenmosiken.assertions.AssertionData;
import com.heershingenmosiken.assertions.AssertionHandler;
import com.heershingenmosiken.assertions.Assertions;
import com.heershingenmosiken.assertions.ThrowableFactory;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.Collections;


public class AssertionsTests {

    private static Throwable EXCEPTION = new Exception();
    private static ThrowableFactory EXCEPTION_FACTORY = () -> EXCEPTION;

    private static final AssertionHandler handler = assertionData -> {
        throw new AssertionHappensException(assertionData.throwable);
    };

    @BeforeAll
    static void setup() {
        Assertions.addAssertionHandler(handler);
    }

    @AfterAll
    static void shutDown() {
        Assertions.removeAssertionHandler(handler);
    }

    @Test
    void failTests() {
        org.junit.jupiter.api.Assertions.assertThrows(AssertionHappensException.class, () -> Assertions.fail(EXCEPTION));
        org.junit.jupiter.api.Assertions.assertThrows(AssertionHappensException.class, () -> Assertions.fail(EXCEPTION_FACTORY));
        org.junit.jupiter.api.Assertions.assertThrows(AssertionHappensException.class, () -> Assertions.failSilently(EXCEPTION));
    }

    @Test
    void assertNullTests() {
        Assertions.assertNotNull(new Object(), EXCEPTION_FACTORY);
        Assertions.assertNull(null, EXCEPTION_FACTORY);
        org.junit.jupiter.api.Assertions.assertThrows(AssertionHappensException.class, () -> Assertions.assertNotNull(null, EXCEPTION_FACTORY));
        org.junit.jupiter.api.Assertions.assertThrows(AssertionHappensException.class, () -> Assertions.assertNull(new Object(), EXCEPTION_FACTORY));
    }

    @Test
    void assertTrueFalseTest() {
        Assertions.assertFalse(false, EXCEPTION_FACTORY);
        Assertions.assertTrue(true, EXCEPTION_FACTORY);
        org.junit.jupiter.api.Assertions.assertThrows(AssertionHappensException.class, () -> Assertions.assertFalse(true, EXCEPTION_FACTORY));
        org.junit.jupiter.api.Assertions.assertThrows(AssertionHappensException.class, () -> Assertions.assertTrue(false, EXCEPTION_FACTORY));
    }

    @Test
    void assertEmptyTests() {
        Assertions.assertEmpty(Collections.emptyList(), EXCEPTION_FACTORY);
        Assertions.assertNotEmpty(Collections.singleton(new Object()), EXCEPTION_FACTORY);
        org.junit.jupiter.api.Assertions.assertThrows(AssertionHappensException.class, () -> Assertions.assertEmpty(Collections.singleton(new Object()), EXCEPTION_FACTORY));
        org.junit.jupiter.api.Assertions.assertThrows(AssertionHappensException.class, () -> Assertions.assertNotEmpty(Collections.emptyList(), EXCEPTION_FACTORY));
    }
}
