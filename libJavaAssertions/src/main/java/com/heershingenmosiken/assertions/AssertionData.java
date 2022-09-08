package com.heershingenmosiken.assertions;

public class AssertionData {

    public final Throwable throwable;
    public final String message;
    public final boolean silent;

    AssertionData(Throwable throwable, String message, boolean silent) {
        this.throwable = throwable;
        this.message = message;
        this.silent = silent;
    }
}