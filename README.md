# android-assertions
Provides Assertion functionality for android application.

## Handle unhadled application state

While application running is is offten possible to get into state when application do not know what to do, here you have 2 options:
 * Crash application - not looks good for user
 * Do nothing - now we have
 
Both looks not good, see examples below:

### Example 1: 
You receive enum value from backend that should be `mp4` or `gif`, so you parse it to enum or as string.
```kotlin
private fun logAppRateClick(it: Byte) {
        when (it.toInt()) {
            Dialog.BUTTON_POSITIVE -> BI.getLogger().logAppRate(BILogger.AppRateType.Positive)
            Dialog.BUTTON_NEGATIVE -> BI.getLogger().logAppRate(BILogger.AppRateType.Negative)
            else -> ???
    }
```
But we have also **Dialog.BUTTON_NEUTRAL** that is we should not display right now.
Someone may later adds neutral button and forgets to add it's handling, how we should implement else case here?
```kotlin
else -> Assertions.fail(IllegalStateException("logAppRateClick($it) $it is unknown"))
```
^ with assertion we will get exception in debug build and in production we will see new Non-Fatal issues in Crashlytics.

### Example 2:
Assertion added to this project not from it's start and it helps us to catch some bugs that exists for years.

PushNotification handling, we agreed with backend about long list of id's for notification, type 
```java
    switch (type) {
        ...
        case FACEBOOK_FRIEND:
            return new ProfileNotificationUseCase(context, pushNotification);
        ...
        case FIRMWARE_UPDATE:
            return new DefaultNotificationUseCase(context, pushNotification.getId(), mNotificationsRepository);
        ...
        default:
            return new StubNotificationUseCase(context, pushNotification);
    }
```
It is from real project and as you can gues `StubNotificationUseCase` actually do nothing.
After adding assertion here:
```java
        default:
            Assertions.fail(new IllegalStateException("Notification type with Id = " + type.getId() + " was not handled."));
            return new StubNotificationUseCase(context, pushNotification);
```
appeared that some of id's was not handled.

## Example 3

If you working on component that work with multiple threads, next API would be helpfull:
```java
AndroidAssertions.INSTANCE.checkUIThread(() -> new IllegalStateException("Should be called in UI thread"));
``` 


### Long story short

Mostly this is try to raise issues that was missed by Developer, Reviewer and QA team.
But also there are situation that happens time to time(hard to catch) OR are not very obvious, and happens under conditions that is hard to obtain. In this cases such approach will help a lot.

## Approach

 * In any condition condition where you have else that should not happens you should add assertion
 * In production build assertion will generate Non-Fatal error in Crash reporting tool
 * In development build assertion will crash application and force developer to fix happened assertion.

## Integration

Add dependecy from jcenter:
```gradle
implementation 'com.heershingenmosiken:assertions-android:1.0.1'
```

Initiaize in your Application::onCreate(...) method.

```kotlin
// Application should crash on assertion only in debug mode
AndroidAssertions.shouldCrashOnAssertion(BuildConfig.DEBUG);
// In any case we would like to report raised assertion to crashlytics as Non-Fatal exception
AndroidAssertions.addAssertionHandler { assertion -> Crashlytics.logException(it.throwable) }
```

## Usage

// TBD

## ExceptionFactory concept

Throwable that AsserionHandler receives should contain information about place where it occurs.
We have observed that if we will not provide Exception object to assertion, Crashlytics and Firebase will think that isseu is inside assertion function, not calling code, and will group all assertTrue(...) methods together, that is not that we want.

So we should provide Exception to assertion function to log it correctly.

BUT! Exception creation is expesive operation as system need to collect StackTrace, and we don't want to createException if ASsertion not raise.

Here we came to ExceptionFactory. We created simple interface ExceptionFactory, and provide it to assertion.

 * Exception will be generated only if assertion happens.
 * StackTrace of generated exception has last lines pointing to exact location in your code, so Crash reporting tools would easily distinct them.

## For pure java modules

If you want to add assertion to pure java module and do not want to bring android dependency, we have pure `assertions-java` assertions module.

It is core part of assertion library and `assertions-android` depends on it, so you may add it as follows:

```gradle
implementation 'com.heershingenmosiken:assertions-java:1.+'
```

It will share same AssertionHandlers with `assertions-android` module.

## It is lightweight

We have no any thirdapty dependencies.

## Silent trick

There are situation when Assertion happens because of dependency that you couldn't fix right now. In this case we have tricky method silent fail method.

```java
Assertions.INSTANCE.failSilently(new IllegalStateException("Please do not use failSilently to often."));
```

It will trigger AssertionHandler, but will not crash application.

## Similar libraries

 * [Java assertions](https://docs.oracle.com/javase/7/docs/technotes/guides/language/assert.html)
 * [Guava preconditions](https://github.com/google/guava/blob/master/guava/src/com/google/common/base/Preconditions.java)
 * [Dart assertions](https://www.dartlang.org/guides/language/language-tour#assert)
 * Lots of others
 
