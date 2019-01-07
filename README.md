[![Download](https://api.bintray.com/packages/dekalo-stanislav/heershingenmosiken/assertions-android/images/download.svg)](https://bintray.com/dekalo-stanislav/heershingenmosiken/assertions-android/_latestVersion)
 [![Build Status](https://travis-ci.com/heershingenmosiken/android-assertions.svg?branch=master)](https://travis-ci.com/heershingenmosiken/android-assertions) [![codecov](https://codecov.io/gh/heershingenmosiken/android-assertions/branch/master/graph/badge.svg)](https://codecov.io/gh/heershingenmosiken/android-assertions)


# android-assertions

Provides Assertion functionality for android application.

## Handle unhadled application state

While application running it is often possible to get into state when application do not know what to do, here you have 2 options:
 * Crash - not looks good for user
 * Ignore - now we have application in unknown state, with possibly broken user experience
 
Both variants looks not good, see examples below:

#### Example 1:
You have if/else or switch by enum or some predefined amount of values.
And you have else case, that **never**(haha) should be called.
```kotlin
private fun logAppRateClick(it: Byte) {
        when (it.toInt()) {
            Dialog.BUTTON_POSITIVE -> BI.getLogger().logAppRate(BILogger.AppRateType.Positive)
            Dialog.BUTTON_NEGATIVE -> BI.getLogger().logAppRate(BILogger.AppRateType.Negative)
            else -> ???
    }
```
In this case we have also **Dialog.BUTTON_NEUTRAL** that is we should not display right now.

Someone may later add neutral button and forget to add it's handling, how we should implement else case here?
```kotlin
else -> Assertions.fail(IllegalStateException("logAppRateClick($it) $it is unknown"))
```
^ with assertion we will get exception in debug build and in production we will see new Non-Fatal issues in Crashlytics.

#### Example 2:
Assertion added to this project not from it's start and it helps us to catch some bugs that exists for years.

PushNotification handling, we agreed with backend about long list of id's for notification types 
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
You can guess that `StubNotificationUseCase` actually do nothing, and you would be right.
After adding assertion here:
```java
        default:
            Assertions.fail(new IllegalStateException("Notification type with Id = " + type.getId() + " was not handled."));
            return new StubNotificationUseCase(context, pushNotification);
```
appeared that some of id's was not handled. WOW!

#### Example 3

If you working on component that work with multiple threads, and you would like to force your client to call specific API UI / non UI threads, next API would be helpfull:
```java
AndroidAssertions.assertUIThread(() -> new IllegalStateException("Should be called in UI thread"));
``` 


#### Long story short

In general this is try to catch issues that was missed by Developer, Reviewer and QA team.

Sure if you have really big team with lots of QA, automated tests and you NEVER release application without full regression, you may avoid this technique, but: 

also there are situation that happens time to time(hard to catch) OR are not very obvious, and happens under conditions that is hard to obtain. 

In this cases such approach will help a lot.

## Approach

 * In any situation where you have else clause that should not happens you should add assertion
 * In production build assertion will generate Non-Fatal error in Crash reporting tool
 * In development build assertion will crash application and force developer to fix happened assertion.

## Integration

Add dependecy from jcenter:
```gradle
implementation 'com.heershingenmosiken:assertions-android:1.+'
```

Initiaize in your Application::onCreate(...) method.

```java
// Application should crash on assertion only in debug mode
AndroidAssertions.shouldCrashOnAssertion(BuildConfig.DEBUG);
// In any case we would like to report raised assertion to crashlytics as Non-Fatal exception
AndroidAssertions.addAssertionHandler(assertionData -> Crashlytics.logException(assertion.throwable));
```

We have also gradle library for pure Java modules, see below.

## Usage


In case of unreachable code like described above, reasonable to add next line.
```java
AndroidAssertions.fail(new IllegalStateException("Unreachable code"));
```

If you want to check some variables that possibly is ok, you should follow next API, here we use ExceptionFactory concept, see explanation below.
```java
AndroidAssertions.assertTrue(shouldBeTrue, () -> new IllegalStateException("Value is not true"));
AndroidAssertions.assertEmpty(collection, () -> new IllegalStateException("Collection is not empty"));
AndroidAssertions.assertNotNull(object, () -> new IllegalStateException("Collection is not empty"));
```

Hint 1: Meaningful messages is important and helps faster understand what happens without opening editor.
Hint 2: You can add information application state to Exceptions message. 

## ExceptionFactory concept

***Issue:*** Throwable that we send to Crashlytics or any other tool should contain information about place where exception occurs, and it should be last lines of StackTrace.

We have observed that if we will not provide Exception object to assertion, Crashlytics and Firebase will think that issue is inside assertion function, not calling code, and  groups them by assertion function, so in assertTrue(...) issue would fall all places this function was called from, this is not what we want.

So we should provide Exception to assertion function to log it correctly.

BUT! Exception creation is expesive operation as system need to collect StackTrace, and we don't want to createException if assertion not triggers exception generation.

Here we came to ExceptionFactory idea. We pass exception factory to assertion function, so if assertion triggers factory will create exception and last line in it's stack trace would be code that called assertion.

So we solved our issue, with no significant trade offs:
 * Exception will be generated only if assertion happens.
 * StackTrace of generated exception has last lines pointing to exact location in your code, so Crash reporting tools would easily distinct them.

## For pure java modules

If you want to add assertion to pure java module and do not want to bring android dependency there, we have pure `assertions-java` java library.

It is core part of assertion library and `assertions-android` depends on it, so you may add it as follows:

```gradle
implementation 'com.heershingenmosiken:assertions-java:1.+'
```

It will share same AssertionHandlers with `assertions-android` module.

## It is lightweight

We have no any thirdapty dependencies.
If you need you may just reuse it as source code.

## Silent trick

There are situation when Assertion happens because of dependency that is out of your control and it is not possible to fix it right now. In this case we have tricky silent fail method.

```java
AndroidAssertions.failSilently(new IllegalStateException("Please do not use failSilently to often."));
```

It will trigger AssertionHandler, but will not crash application.

## Similar libraries

 * [Java assertions](https://docs.oracle.com/javase/7/docs/technotes/guides/language/assert.html)
 * [Guava preconditions](https://github.com/google/guava/blob/master/guava/src/com/google/common/base/Preconditions.java)
 * [Dart assertions](https://www.dartlang.org/guides/language/language-tour#assert)
 * Lots of others
 
## Contribution / Issues

Feel free to make Pull Request and Raise issues or Feature Requests if you will have any.