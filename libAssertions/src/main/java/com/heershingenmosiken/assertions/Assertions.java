package com.heershingenmosiken.assertions;

import java.util.Collection;
import java.util.PriorityQueue;

public class Assertions {

    private static final int DEFAULT_PRIORITY = 0;
    private static final PriorityQueue<PriorityHandler> handlers = new PriorityQueue<>(5, (l, r) -> r.priority - l.priority);

    /**
     * Add AssertionHandler to handlers set, with DEFAULT_PRIORITY = 0
     */
    public static void addAssertionHandler(AssertionHandler assertionHandler) {
        handlers.add(new PriorityHandler(assertionHandler, DEFAULT_PRIORITY));
    }

    /**
     * Add AssertionHandler to handler list, with provided priority.
     * Handlers with greater priority will be invoked earlier.
     */
    public static void addAssertionHandler(AssertionHandler assertionHandler, int priority) {
        handlers.add(new PriorityHandler(assertionHandler, priority));
    }

    /**
     * Remove AssertionHandler from handlers list.
     */
    public static void removeAssertionHandler(AssertionHandler assertionHandler) {
        Utils.filter(handlers, priorityHandler -> priorityHandler.handler == assertionHandler);
    }

    private static boolean hasHandlers() {
        return !handlers.isEmpty();
    }

    protected static void internalRaiseAssertion(ThrowableFactory throwableFactory, String message, boolean silent) {
        if (hasHandlers()) {
            AssertionData assertionData = new AssertionData(throwableFactory.create(), message, silent);
            Utils.forEach(handlers, priorityHandler -> priorityHandler.handler.handle(assertionData));
        }
    }

    public static void log(String message) {
        Utils.forEach(handlers, priorityHandler -> priorityHandler.handler.log(message));
    }

    /**
     * Assertion failed with throwable.
     */
    public static void fail(Throwable throwable) {
        internalRaiseAssertion(() -> throwable, "Fail", false);
    }

    /**
     * Assertion failed with throwable. This assertion marked as silent.
     */
    public static void failSilently(Throwable throwable) {
        internalRaiseAssertion(() -> throwable, "FailSilently", true);
    }

    /**
     * Assertion failed with throwable factory.
     */
    public static void fail(ThrowableFactory throwableFactory) {
        internalRaiseAssertion(throwableFactory, "Fail", false);
    }

    /**
     * Checks that shouldBeTrue condition is true, else raises exception provided by ThrowableFactory.
     */
    public static void assertTrue(boolean shouldBeTrue, ThrowableFactory throwableFactory) {
        if (!shouldBeTrue) {
            internalRaiseAssertion(throwableFactory, "AssertTrue", false);
        }
    }

    /**
     * Checks that shouldBeFalse condition is false, else raises exception provided by ThrowableFactory.
     */
    public static void assertFalse(boolean shouldBeFalse, ThrowableFactory throwableFactory) {
        if (shouldBeFalse) {
            internalRaiseAssertion(throwableFactory, "AssertFalse", false);
        }
    }

    public static void assertNull(Object shouldBeNull, ThrowableFactory throwableFactory) {
        if (shouldBeNull != null) {
            internalRaiseAssertion(throwableFactory, "AssertNull but was [" + shouldBeNull + "]", false);
        }
    }

    public static void assertNotNull(Object shouldNotBeNull, ThrowableFactory throwableFactory) {
        if (shouldNotBeNull == null) {
            internalRaiseAssertion(throwableFactory, "AssertNotNull", false);
        }
    }

    public static void assertEmpty(Collection shouldBeEmpty, ThrowableFactory throwableFactory) {
        if (!shouldBeEmpty.isEmpty()) {
            internalRaiseAssertion(throwableFactory, "AssertEmpty but size = " + shouldBeEmpty.size(), false);
        }
    }

    public static void assertNotEmpty(Collection shouldNotBeEmpty, ThrowableFactory throwableFactory) {
        if (shouldNotBeEmpty.isEmpty()) {
            internalRaiseAssertion(throwableFactory, "AssertNotEmpty but size = " + shouldNotBeEmpty.size(), false);
        }
    }

    /**
     * Test only.
     */
    private static void tearDown() {
        handlers.clear();
    }

    private static class PriorityHandler {

        private final AssertionHandler handler;
        private final int priority;

        private PriorityHandler(AssertionHandler handler, int priority) {
            this.handler = handler;
            this.priority = priority;
        }
    }
}