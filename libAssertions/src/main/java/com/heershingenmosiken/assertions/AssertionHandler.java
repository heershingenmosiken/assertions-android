package com.heershingenmosiken.assertions;

/**
 * Handler for happened assertion.
 */

public interface AssertionHandler {

    public static AssertionHandler EMPTY_HANDLER = new AssertionHandler() {
        @Override
        public void handle(AssertionData assertionData) {
        }
    };

    void handle(AssertionData assertionData);
}