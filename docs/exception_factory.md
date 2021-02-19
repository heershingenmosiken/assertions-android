## ExceptionFactory concept

***Issue:*** Throwable that we send to Crashlytics or any other tool should contain information about place where exception occurs, and it should be last lines of StackTrace.

We have observed that if we will not provide Exception object to assertion, Crashlytics and Firebase will think that issue is inside assertion function, not calling code, and  groups them by assertion function, so in assertTrue(...) issue would fall all places this function was called from, this is not what we want.

So we should provide Exception to assertion function to log it correctly.

BUT! Exception creation is expesive operation as system need to collect StackTrace, and we don't want to createException if assertion not triggers exception generation.

Here we came to ExceptionFactory idea. We pass exception factory to assertion function, so if assertion triggers factory will create exception and last line in it's stack trace would be code that called assertion.

So we solved our issue, with no significant trade offs:
 * Exception will be generated only if assertion happens.
 * StackTrace of generated exception has last lines pointing to exact location in your code, so Crash reporting tools would easily distinct them.