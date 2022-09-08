package com.heershingenmosiken.assertions

fun Throwable.assert() = Assertions.fail(this)
fun Throwable.assertSilently() = Assertions.failSilently(this)