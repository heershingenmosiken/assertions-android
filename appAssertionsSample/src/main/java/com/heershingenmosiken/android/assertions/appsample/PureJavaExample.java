package com.heershingenmosiken.android.assertions.appsample;

import com.heershingenmosiken.assertions.Assertions;

public class PureJavaExample {
    {
        Assertions.failSilently(new IllegalStateException("Please do not use failSilently to often."));
    }
}
