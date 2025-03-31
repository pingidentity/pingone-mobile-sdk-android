# Release Notes

## v2.0.0 - April 1st, 2025
Features:

- The PingOne MFA SDK has been updated to allow the same device to be paired in multiple geographic regions.
- The SDK now has a method called `testRemoteNotification` that allows you to test and debug push notification functionality.
- A new mandatory method called `configure` has been added. This method is used to set up the SDK.
- The following methods have been deprecated: `getInfo`, `generateMobilePayload`.
- The method `setDeviceToken` was updated to return an array of errors.
- Cancel Authentication feature added: lets PingOne server send a new notification with `authCanceled` notification type, to the user’s device if they switch to a different device during the authentication process. This notification type tells the app that the authentication was canceled, so the app can remove any pop-up without waiting for a timeout, ensuring a smoother user experience.
- Number Matching feature added: This is an enhanced authentication method where a number is displayed on the web interface, and the user must input the same number on their mobile device after receiving a push notification, with parameter `numberPicked`, in the `NotificationObject.approve()` method. This ensures that the user controls both devices, adding an extra layer of security to prevent accidental or unauthorized approvals.

Known issue:
- If your app enables obfuscation, you need to add specific ProGuard rules to prevent re-obfuscation of the PingOne MFA SDK. Failure to do so may cause unexpected behavior or malfunctions in certain library features.
  To avoid issues, add the following rules to your ProGuard rules file:
```gradle
# Prevent re-obfuscation of the PingOne MFA SDK classes.
-keep class com.pingidentity.pingidsdkv2.** {*;}
-keep class com.pingidentity.pingonemfa.** {*;}
```
Ensure your app's ProGuard configuration includes these rules before building a release.


## v1.11.0 - December 24th, 2023
Bug fixes:

- Relocated the SDK obfuscated classes to unique names to prevent conflicts with other dependencies using default settings.
- Enhanced start-up flow to prevent blocking the main thread during application start.
- Removed `android:name` from the application tag in the library AndroidManifest.


## v1.10.0 - June 29th, 2023
Features:

- The PingOne MFA SDK has been updated to rotate encryption keys once a year. Key rotation events are written to the audit log.


## v1.9.0 - April 19th, 2023
Features:

- **Device integrity validation migrated from SafetyNet API to Play Integrity API.
  Note that this version of the SDK is not backward-compatible with previous versions in terms of integrity validation. If you want to use the new version of the SDK, you will need to configure Play Integrity to ensure the integrity of your data. If you have not done so, and integrity checking is enabled for the application, users may be blocked.**
- Performance improvements.


## v1.8.1 - Jan 9th, 2023
Features:

- Added support for different push message categories.
- Added support and sample code for Huawei Messaging Services
- Added support for migration from the PingID SDK. See README for detailed instructions.


## v1.7.2 - Aug 31st, 2022
Features:

- Added support for alphanumeric pairing key.
- Bug fixes and performance improvements.


## v1.7.1 - June 13th, 2022
Features:

- Improvements to device integrity checks in the SDK.


## v1.7.0 - April 25th, 2022
Features:

- Added support for authentication using QR Code scanning or manual typing of an authentication code
- Added Certificate Transparency mechanism to protect against mis-issued certificates
- The JWT signature validation updated to use more strong EC algorithm.

Compatibility:

- Minimal Android version is updated to 26 (Android 8)


## v1.6.0 - August 1st, 2021
Features:

- Added support for device integrity validation for threat protection.


## v1.5.0 - April 6th, 2021
Features:

- Added support for one time passcode and disable SDK push notifications.

Compatibility:

- Deprecated current pairing method and added support to new one with returned object of PairingInfo.
- Deprecated current processRemoteNotification method and added support to new one with Context as a parameter.


## v.1.4.1 - Feb 3rd, 2021
Bug fixes:

- Added a missing dependency in the project-level `build.gradle` file.
- Fixed a dead link from README


## v1.4.0 - Jan 5th, 2021
Features:

- Added support for Mobile Authentication Framework for Android Developers.


## v1.3.0 - June 18th, 2020
Features:

- Push notification data is now JWT-signed and verified
- Added `clientContext` to the push notification object. `clientContext` contains extra parameters that are passed to the client.
- Added support for background push notification (extra verification) during device authorization


Compatibility:

- Deprecated method: `PingOne.processRemoteNotification(RemoteMessage remoteMessage, PingOne.PingOneNotificationCallback callback)`
  Instead please use:
  `PingOne.processRemoteNotification(Context context, RemoteMessage remoteMessage, PingOne.PingOneNotificationCallback callback)`


## v1.2.0 - March 31st, 2020
Features:

- Support for sending logs (both by app and admin)
- Ability to track authentication expiration time


## v1.1.0 - December 9th, 2019
Features:

- Support for automatic pairing via OpenID Connect authentication.
- The `pair()` function now returns error code 10007 `PAIRING_KEY_DATA_CENTER_MISMATCH` when trying to pair a user in a different geographical datacenter than the users already paired on the device.

Compatibility:

- Deprecated functions: `NotificationObject.approve(Context context, PingOne.PingOneSDKCallback callback)`


## v1.0.2 - October 30th, 2019
Features:

- Support for APAC datacenter.


## v1.0.1 - October 10th, 2019
Bug fixes:
- Handle test push without producing an error.

Compatibility notes:
- replace NimbusDS with Jose4J.


## v1.0 - August 1st, 2019
Features:

- Provide MFA capability (using push notifications) for Android native apps.
