
# Application state

Each application has a state and responds to events and user actions.

But, while the application running we may face unexpected behavior, because of a bug in the code or other systems and libraries we rely on.
So how Application should behave in such cases?
 * Ignore - The bad, now we have application in an unknown state, with possibly broken user experience.
 * Crash - The ugly, no comments.
 * Assert - The normal, we will catch it, get informed, and fix it later.

This approach helps to catch issues that were missed during the development, PR review, and Testing phases.

## How assertions help 
   
For the Developer (in dev/debug builds):
 * Assertions will crash your Application, now you can fix the issue.
 
For the User (in production/release builds):
 * Assertions will NOT crash your Application, but report the occurred issue with the StackTrace to the Firebase or any other provided monitoring tool.


## What should be covered with assertions 

When conditions seem impossible or unreachable, but technically is still possible or reachable and may side effect with undesired user experience. 
 
Like:
1. In `else` clause of all `when/if` checks
2. In `catch` blocks, where we do not want to catch an exception.
3. In checks for other components results (like Backend), that may have a mistake or other issues.   

See examples below:

#### Example 1:
You have if/else or switch by enum with some predefined amount of values.
And you have else case, that **never**(haha) should be called.
```kotlin
private fun logAppRateClick(it: Int) {
    when (it) {
        Dialog.BUTTON_POSITIVE -> BI.getLogger().logAppRate(BILogger.AppRateType.Positive)
        Dialog.BUTTON_NEGATIVE -> BI.getLogger().logAppRate(BILogger.AppRateType.Negative)
        else -> ??? // you know tht it is impossible, but what if someone will add one more button without handling?
    }
}
```

Someone may later add neutral button and forget to add it's handling, how we should implement else case here?
```kotlin
else -> Assertions.fail(IllegalStateException("logAppRateClick($it) $it is unknown"))
```
^ with assertion we will get the exception in debug build, but in the production, we will see new Non-Fatal issues in Firebase.

#### Example 2:

We are drawing QR codes in UI on Android.

```kotlin
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val codeWriter = MultiFormatWriter()
    try {
        val bitMatrix = codeWriter.encode(text, BarcodeFormat.QR_CODE, width, height)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }
    } catch (e: WriterException) {
        // what should we do here?
    }
    return bitmap
```

Should we throw an exception, catch it on the above layer and display an error instead of QR code? <br />
Do we have enough development and design time to design and implement it? <br />
How QA team will test that if all our text that we send into MultiFormatWriter() are working without exception?


Ignoring not looks good as well.

***Solution:*** 
```
Assertions.fail(IllegalStateException("Failed to encode QR code for text = $text width = $width height = $height"), e)
```
Now we will see the issue in Firebase if it occurs or will never see if that functionality works fine. And sure we saved some bucks on development ;)


#### Example 3:
Assertion added to this project, not from its start and it helps us to catch some bugs that exist for years.

PushNotification handling, there was an agreement with the team backend about a list of id's for notification types
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
appeared that some of the ids were not handled. WOW!

#### Example 4
If you working on a component that works with multiple threads, and you would like to force your client to call a specific API in the UI or non UI threads, the next API would be helpful:
```java
AndroidAssertions.assertUIThread(() -> new IllegalStateException("Should be called in UI thread"));
```

