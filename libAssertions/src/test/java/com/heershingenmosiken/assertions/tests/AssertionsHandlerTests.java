package com.heershingenmosiken.assertions.tests;

import com.heershingenmosiken.assertions.AssertionData;
import com.heershingenmosiken.assertions.AssertionHandler;
import com.heershingenmosiken.assertions.Assertions;
import com.heershingenmosiken.assertions.ThrowableFactory;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class AssertionsHandlerTests {

    private static Throwable EXCEPTION = new Exception();
    private static ThrowableFactory EXCEPTION_FACTORY = () -> EXCEPTION;

    private static final AssertionHandler RETHROW_HANDLER = new AssertionHandler() {
        @Override
        public void log(String message) {
            throw new IllegalStateException("log(" + message + ") called unexpectedly.");
        }

        @Override
        public void handle(AssertionData assertionData) {
            throw new AssertionHappensException(assertionData.throwable);
        }
    };

    @BeforeEach
    void setup() {
        cleanupHandlers();
    }

    @AfterEach
    void tearDown() {
        cleanupHandlers();
    }

    static void cleanupHandlers() {
        try {
            Field field = Assertions.class.getDeclaredField("handlers");
            field.setAccessible(true);
            ((PriorityQueue) field.get(null)).clear();
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new IllegalStateException(e);
        }
    }

    @Test
    void noHandlersTest() {
        Assertions.fail(EXCEPTION);
        Assertions.fail(EXCEPTION_FACTORY);
        Assertions.failSilently(EXCEPTION);
    }

    @Test
    void silentFlagTest() {

        AssertionHandler rethrowIfNotSilentHandler = new AssertionHandler() {
            @Override
            public void log(String message) {
                throw new IllegalStateException("log(" + message + ") called unexpectedly.");
            }

            @Override
            public void handle(AssertionData assertionData) {
                if (!assertionData.silent) {
                    throw new AssertionHappensException(assertionData.throwable);
                }
            }
        };

        Assertions.addAssertionHandler(rethrowIfNotSilentHandler);

        Assertions.failSilently(EXCEPTION);
        org.junit.jupiter.api.Assertions.assertThrows(AssertionHappensException.class, () -> Assertions.fail(EXCEPTION));

        Assertions.removeAssertionHandler(rethrowIfNotSilentHandler);
    }

    @Test
    void singleHandlerTest() {
        Assertions.addAssertionHandler(RETHROW_HANDLER);
        org.junit.jupiter.api.Assertions.assertThrows(AssertionHappensException.class, () -> Assertions.fail(EXCEPTION));
        Assertions.removeAssertionHandler(RETHROW_HANDLER);
    }

    @Test
    void multipleAssertionsTest() {
        AtomicInteger counter = new AtomicInteger();

        AssertionHandler[] handlers = new AssertionHandler[]{
                new AssertionCounterHandler(counter), new AssertionCounterHandler(counter), new AssertionCounterHandler(counter)
        };

        for (AssertionHandler handler : handlers) Assertions.addAssertionHandler(handler);

        Assertions.fail(EXCEPTION);
        org.junit.jupiter.api.Assertions.assertEquals(3, counter.intValue());

        Assertions.removeAssertionHandler(handlers[0]);

        Assertions.fail(EXCEPTION);
        org.junit.jupiter.api.Assertions.assertEquals(5, counter.intValue());

        for (AssertionHandler handler : handlers) Assertions.removeAssertionHandler(handler);
    }

    @Test
    void assertionHandlersPriorityTest() {

        AtomicInteger second = new AtomicInteger();
        AtomicInteger first = new AtomicInteger();
        AtomicInteger third = new AtomicInteger();

        AssertionHandler[] handlers = new AssertionHandler[]{
                new AssertionCounterHandler(second, () -> {
                    org.junit.jupiter.api.Assertions.assertEquals(1, first.get());
                    org.junit.jupiter.api.Assertions.assertEquals(1, second.get());
                    org.junit.jupiter.api.Assertions.assertEquals(0, third.get());
                }),
                new AssertionCounterHandler(first, () -> {
                    org.junit.jupiter.api.Assertions.assertEquals(1, first.get());
                    org.junit.jupiter.api.Assertions.assertEquals(0, second.get());
                    org.junit.jupiter.api.Assertions.assertEquals(0, third.get());
                }),
                new AssertionCounterHandler(third, () -> {
                    org.junit.jupiter.api.Assertions.assertEquals(1, first.get());
                    org.junit.jupiter.api.Assertions.assertEquals(1, second.get());
                    org.junit.jupiter.api.Assertions.assertEquals(1, third.get());
                })
        };

        Assertions.addAssertionHandler(handlers[0]);
        Assertions.addAssertionHandler(handlers[1], 5);
        Assertions.addAssertionHandler(handlers[2], -1);

        Assertions.fail(EXCEPTION);

        for (AssertionHandler handler : handlers) Assertions.removeAssertionHandler(handler);
    }

    @Test
    void assertionsHandlerLogTest() {

        String testMessage = "test message";

        List<String> messages = new ArrayList<>();

        AssertionHandler logTestHandler = new AssertionHandler() {

            @Override
            public void log(String message) {
                messages.add(message);
            }

            @Override
            public void handle(AssertionData assertionData) {
                throw new AssertionHappensException(assertionData.throwable);
            }
        };

        Assertions.addAssertionHandler(logTestHandler);

        Assertions.log(testMessage);
        org.junit.jupiter.api.Assertions.assertEquals(1, messages.size());
        org.junit.jupiter.api.Assertions.assertEquals(testMessage, messages.get(0));

        Assertions.removeAssertionHandler(logTestHandler);
    }
}
