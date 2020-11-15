## ExceptionFactory concept

***Issue:*** Throwable that we send to Firebase or any other tool should contain information about the place where exception occurs, and it should be the last lines of StackTrace.

We have observed that if we will not provide an Exception object to the assertion, Firebase will think that issue is inside the assertion function, not calling code, and groups them by assertion function, so in assertTrue(...) issue would fall all places this function was called from. ***This is not what we want.***

So we should provide an Exception to the assertion function to log it correctly.

***BUT!*** Exception creation is an expensive operation as the system needs to collect StackTrace, and we don't want to create an exception if assertion does not trigger exception generation, like in conditional assertions.

Here we came to the ExceptionFactory idea. We pass the exception factory to the assertion function, so if assertion triggers then the factory will create an exception and the last line in its stack trace would be code that is called an assertion.

So we solved our issue, with no significant trade-offs:
 * Exception will be generated only if assertion happens.
 * StackTrace of generated exception has last lines pointing to the exact location in your code, so Crash reporting tools would easily distinguish them.
 