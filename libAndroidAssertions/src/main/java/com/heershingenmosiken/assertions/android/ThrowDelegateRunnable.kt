package com.heershingenmosiken.assertions.android

internal class ThrowDelegateRunnable constructor(throwable: Throwable) : Runnable {

    private val runtimeException: RuntimeException = RuntimeException(throwable)

    override fun run() {
        throw runtimeException
    }
}