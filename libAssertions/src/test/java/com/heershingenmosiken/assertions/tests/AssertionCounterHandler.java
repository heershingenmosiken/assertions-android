package com.heershingenmosiken.assertions.tests;

import com.heershingenmosiken.assertions.AssertionData;
import com.heershingenmosiken.assertions.AssertionHandler;

import java.util.concurrent.atomic.AtomicInteger;

class AssertionCounterHandler implements AssertionHandler {

    private final AtomicInteger counter;
    private final Runnable action;

    AssertionCounterHandler(AtomicInteger counter) {
        this(counter, () -> {});
    }

    AssertionCounterHandler(AtomicInteger counter, Runnable action) {
        this.counter = counter;
        this.action = action;
    }

    @Override
    public void handle(AssertionData assertionData) {
        counter.incrementAndGet();
        action.run();
    }
}
