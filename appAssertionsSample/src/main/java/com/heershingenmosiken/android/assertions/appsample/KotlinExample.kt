package com.heershingenmosiken.android.assertions.appsample

import com.heershingenmosiken.assertions.assertSilently

class KotlinExample {
    init {
        IllegalStateException("Please do not use failSilently to often.").assertSilently()
    }
}