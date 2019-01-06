package com.heershingenmosiken.assertions

import java.util.*

open class DefaultAssertions {

    companion object {
        private const val DEFAULT_PRIOTIY = 0;
        private val handlers = TreeSet<PriorityHandler>()
    }

    /**
     * Add AssertionHandler to handler list.
     */
    fun addAssertionHandler(assertionHandler: AssertionHandler) {
        handlers.add(PriorityHandler(assertionHandler))
    }

    /**
     * Add AssertionHandler to handler list, with DEFAULT_PRIORITY = 0.
     */
    fun addAssertionHandler(assertionHandler: AssertionHandler, priority: Int = DEFAULT_PRIOTIY) {
        handlers.add(PriorityHandler(assertionHandler, priority))
    }

    /**
     * Remove AssertionHandler from handlers list.
     */
    fun removeAssertionHandler(assertionHandler: AssertionHandler) {
        Utils.filter(handlers) { it.handler == assertionHandler }
    }

    private fun hasHandlers(): Boolean = !handlers.isEmpty()

    private fun internalRaiseAssertion(throwableFactory: ThrowableFactory, message: String, silent: Boolean = false) {
        if (hasHandlers()) {
            val assertion = AssertionData(message, throwableFactory.create(), silent)
            handlers.forEach { it.handler.handle(assertion) }
        }
    }

    /**
     * Assertion failed with throwable.
     */
    fun fail(throwable: Throwable) {
        internalRaiseAssertion( ThrowableFactory { throwable }, "Fail")
    }

    /**
     * Assertion failed with throwable.
     *
     * @param silently will be passed to AssertionHandler, crash will not happen in any case if it is silent assertion. It is reasonable if it is possible but undesired situation that you would like to log in your crash reporting tool.
     */
    fun failSilently(throwable: Throwable) {
        internalRaiseAssertion(ThrowableFactory { throwable }, "FailSilently", true)
    }

    /**
     * Assertion failed with throwable factory.
     */
    fun fail(throwableFactory: ThrowableFactory) {
        internalRaiseAssertion(ThrowableFactory { throwableFactory.create() }, "Fail")
    }

    /**
     * Checks that shouldBeTrue condition is true, else raises exception provided by ThrowableFactory.
     */
    fun assertTrue(shouldBeTrue: Boolean, throwableFactory: ThrowableFactory) {
        if (!shouldBeTrue) {
            internalRaiseAssertion(ThrowableFactory { throwableFactory.create() }, "AssertTrue")
        }
    }

    /**
     * Checks that shouldBeFalse condition is false, else raises exception provided by ThrowableFactory.
     */
    fun assertFalse(shouldBeFalse: Boolean, throwableFactory: ThrowableFactory) {
        if (shouldBeFalse) {
            internalRaiseAssertion(ThrowableFactory { throwableFactory.create() }, "AssertFalse")
        }
    }

    fun assertNull(shouldBeNull: Any?, throwableFactory: ThrowableFactory) {
        if (shouldBeNull != null) {
            internalRaiseAssertion(ThrowableFactory { throwableFactory.create() }, "AssertNull but was $shouldBeNull")
        }
    }

    fun assertNotNull(shouldNotBeNull: Any?, throwableFactory: ThrowableFactory) {
        if (shouldNotBeNull == null) {
            internalRaiseAssertion(ThrowableFactory { throwableFactory.create() }, "AssertNotNull")
        }
    }

    fun <T> assertEmpty(shouldBeEmpty: Collection<T>, throwableFactory: ThrowableFactory) {
        if (!shouldBeEmpty.isEmpty()) {
            internalRaiseAssertion(ThrowableFactory { throwableFactory.create() }, "AssertEmpty but size = ${shouldBeEmpty.size}")
        }
    }

    fun <T> assertNotEmpty(shouldNotBeEmpty: Collection<T>, throwableFactory: ThrowableFactory) {
        if (shouldNotBeEmpty.isEmpty()) {
            internalRaiseAssertion(ThrowableFactory { throwableFactory.create() }, "AssertNotEmpty but size = ${shouldNotBeEmpty.size}")
        }
    }

    private data class PriorityHandler(val handler: AssertionHandler, val priority: Int = DEFAULT_PRIOTIY) : Comparable<PriorityHandler> {
        override fun compareTo(other: PriorityHandler) = other.priority - priority
    }
}