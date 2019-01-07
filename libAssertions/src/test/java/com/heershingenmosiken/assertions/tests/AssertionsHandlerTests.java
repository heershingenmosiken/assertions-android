package com.heershingenmosiken.assertions.tests;

import com.heershingenmosiken.assertions.AssertionData;
import com.heershingenmosiken.assertions.AssertionHandler;
import com.heershingenmosiken.assertions.Assertions;
import com.heershingenmosiken.assertions.ThrowableFactory;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

public class AssertionsHandlerTests {

    private static Throwable EXCEPTION = new Exception();
    private static ThrowableFactory EXCEPTION_FACTORY = () -> EXCEPTION;

    private static final AssertionHandler RETHROW_HANDLER = assertionData -> {
        throw new AssertionHappensException(assertionData.throwable);
    };

    @Test
    void noHandlersTest() {
        Assertions.fail(EXCEPTION);
    }

    @Test
    void singleHandlerTest() {
        Assertions.addAssertionHandler(RETHROW_HANDLER);
        org.junit.jupiter.api.Assertions.assertThrows(AssertionHappensException.class, () -> Assertions.fail(EXCEPTION));
        Assertions.removeAssertionHandler(RETHROW_HANDLER);
    }

    @Test
    void multipleAssertionsTest() {
        AtomicInteger count = new AtomicInteger();

        AssertionHandler handlers[] = new AssertionHandler[]{
                assertionData -> count.incrementAndGet(),
                assertionData -> count.incrementAndGet(),
                assertionData -> count.incrementAndGet()
        };

        for (AssertionHandler handler : handlers) Assertions.addAssertionHandler(handler);

        Assertions.fail(EXCEPTION);
        org.junit.jupiter.api.Assertions.assertEquals(3, count.intValue());

        Assertions.removeAssertionHandler(handlers[0]);

        Assertions.fail(EXCEPTION);
        org.junit.jupiter.api.Assertions.assertEquals(5, count.intValue());

        for (AssertionHandler handler : handlers) Assertions.removeAssertionHandler(handler);
    }

    private static class AssertionCounterHandler implements AssertionHandler {

        private final AtomicInteger counter;
        private final Runnable action;

        private AssertionCounterHandler(AtomicInteger counter, Runnable action) {
            this.counter = counter;
            this.action = action;
        }

        @Override
        public void handle(AssertionData assertionData) {
            counter.incrementAndGet();
            action.run();
        }
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
}
