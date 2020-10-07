package com.heershingenmosiken.assertions;

/**
 * Handler for assertions.
 */
public interface AssertionHandler {

    /**
     * Report message that may be related to next message.
     */
    void log(String message);

    /**
     * Handle reported assertion.
     */
    void handle(AssertionData assertionData);
}