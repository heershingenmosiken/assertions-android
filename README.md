# assertions-android

Assertions functionality for Android Applications.

Early discovery of application issues.
See the full reasoning [here](docs/reasoning.md).

## Integration

Add dependency from `jcenter`:
```gradle
implementation 'com.heershingenmosiken:assertions-android:1.+'
```

Initialize in your Application::onCreate(...) method.

```java
// Application should crash on assertion only in debug mode
AndroidAssertions.shouldCrashOnAssertion(BuildConfig.DEBUG);
// In any case we would like to report raised assertion to crashlytics as Non-Fatal exception
AndroidAssertions.addAssertionHandler(assertionData -> Crashlytics.logException(assertion.throwable));
```

We have also gradle library for pure Java modules, see [below](https://github.com/heershingenmosiken/assertions-android#for-pure-java-modules).

## Usage

API when assertion state occurred.
```java
AndroidAssertions.fail(new IllegalStateException("Unreachable code"));
```

API for variables state validation, see [ExceptionFactory](docs/exception_factory.md) concept, [explanation](docs/exception_factory.md).
```java
AndroidAssertions.assertTrue(shouldBeTrue, () -> new IllegalStateException("Value is not true"));
AndroidAssertions.assertEmpty(collection, () -> new IllegalStateException("Collection is not empty"));
AndroidAssertions.assertNotNull(object, () -> new IllegalStateException("Collection is not empty"));
```

***Hint 1:*** Meaningful messages are important and help faster understand what happens without opening the editor.
***Hint 2:*** You can add information application state to Exceptions message.

## For pure java modules

If you want to add an assertion to a pure java module(no android) and do not want to bring android dependency there, we have a pure `assertions-java` java library.

It is a core part of the assertion library and `assertions-android` depends on it, so you may add it as follows:

```gradle
implementation 'com.heershingenmosiken:assertions-java:1.+'
```

It will share the same AssertionHandlers with `assertions-android` module.

## It is lightweight

We have no third-party dependencies.
You may reuse it as source code.

## Silent trick

There are situations when Assertion happens because of dependency that is out of your control and it is not possible to fix it right now. 

In this case, you may use `failSilently` method.
```java
AndroidAssertions.failSilently(new IllegalStateException("Please do not use failSilently to often."));
```

It will trigger AssertionHandler, but will not crash the application.

## Similar libraries

 * [Java assertions](https://docs.oracle.com/javase/7/docs/technotes/guides/language/assert.html)
 * [Guava preconditions](https://github.com/google/guava/blob/master/guava/src/com/google/common/base/Preconditions.java)
 * [Dart assertions](https://www.dartlang.org/guides/language/language-tour#assert)
 * Lots of others
 
## Contribution / Issues

Feel free to make PRs and Raise issues or Feature Requests if you will have any.

## Author

Stanislav Dekalo @dekalo-stanislav 