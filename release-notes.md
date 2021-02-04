# Release Notes

## v.1.4.1 - Feb 3rd, 2021
Bug fixes:
 - Added a missing dependency in the project-level `build.gradle` file.
 - Fixed a dead link from README

## v1.4.0 - Jan 5th, 2021
Features:

- Added support for Mobile Authentication Framework for Android Developers. 
See the files in the `PingAuthenticationUI` and `PingAuthenticationCore` folders.


## v1.3.0 - June 18th, 2020
Features:

- Push notification data is now JWT-signed and verified
- Added `clientContext` to the push notification object. `clientContext` contains extra parameters that are passed to the client.
- Added support for background push notification (extra verification) during device authorization

Dependencies updated:

  * 'com.android.tools.build:gradle:3.5.3'
  * 'com.google.gms:google-services:4.3.3'
  * 'com.google.firebase:firebase-core:17.4.2'
  * 'com.google.firebase:firebase-messaging:20.2.0'


Compatibility notes:

- Deprecated method: `PingOne.processRemoteNotification(RemoteMessage remoteMessage, PingOne.PingOneNotificationCallback callback)`
  Instead please use:
 `PingOne.processRemoteNotification(Context context, RemoteMessage remoteMessage, PingOne.PingOneNotificationCallback callback)`


## v1.2.0 - March 31st, 2020
Features:

- Support for sending logs (both by app and admin)
- Ability to track authentication expiration time in the app

## v1.1.0 - December 9th, 2019
Features:

- Support for automatic pairing via OpenID Connect authentication.
- The `pair()` function now returns error code 10007 `PAIRING_KEY_DATA_CENTER_MISMATCH` when trying to pair a user in a different geographical datacenter than the users already paired on the device.

Compatibility notes:

- Deprecated functions: `NotificationObject.approve(Context context, PingOne.PingOneSDKCallback callback)`
- Sample app now using AppAuth 0.7.1

## v1.0.2 - October 30th, 2019
Features:
- Support for APAC datacenter.

## v1.0.1 - October 10th, 2019
Features:
- added a notification pop up in the sample app.

Bug fixes:
- Handle test push without producing an error.

Compatibility notes:
- replace NimbusDS with Jose4J.


## v1.0 - August 1st, 2019
Features:
- Provide MFA capability (using push notifications) for Android native apps.

