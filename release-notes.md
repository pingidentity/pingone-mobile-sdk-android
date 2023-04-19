# Release Notes


## v1.9.0 - April 19th, 2023
Features:

- Updated SDK to version 1.9.0
- **Device integrity validation migrated from SafetyNet API to Play Integrity API.
    Note that this version of the SDK is not backward-compatible with previous versions in terms of integrity validation. If you want to use the new version of the SDK, you will need to configure Play Integrity to ensure the integrity of your data. If you have not done so, and integrity checking is enabled for the application, users may be blocked.**
- Performance improvements.

Compatibility:

- Dependencies updated to their latest versions:
    * In the 'build.gradle' file at the **app** level:
        * 'com.pingidentity.pingonemfa:android-sdk:**1.9.0**'
- Dependencies added:
    * In the 'build.gradle' file at the **app** level:
        *  'com.google.android.play:integrity:**1.1.0**'


## v1.8.1 - Jan 9th, 2023
Features:

- Updated SDK to version 1.8.1
- Added support for different push message categories.
- Added support and sample code for Huawei Messaging Services
- Added support for migration from the PingID SDK. See README for detailed instructions.

Compatibility:

- Dependencies updated to their latest versions:
    * In the 'build.gradle' file at the **project** level:
        * 'com.google.gms:google-services:**4.3.14**'
        * 'androidx.navigation:navigation-safe-args-gradle-plugin:**2.5.3**'
    * In the 'build.gradle' file at the **app** level:
        * 'com.google.code.gson:gson:**2.9.0**'
- Dependencies added:
    * In the 'build.gradle' file at the **app** level:
        * 'com.pingidentity.pingonemfa:android-sdk:1.8.1'
- Dependencies removed from the project:
    * In the 'build.gradle' file at the **app** level:
        * 'org.slf4j:slf4j-api:1.7.30'
        * 'com.github.tony19:logback-android:2.0.0'
        * 'com.madgag.spongycastle:core:1.58.0.0'
        * 'com.madgag.spongycastle:bcpkix-jdk15on:1.58.0.0'
        * 'com.google.android.gms:play-services-vision:20.1.3'
        * 'com.google.android.gms:play-services-safetynet:18.0.1'
        * 'org.bitbucket.b_c:jose4j:0.7.9'
        * 'com.appmattus.certificatetransparency:certificatetransparency-android:1.0.0'


## v1.7.2 - Aug 31st, 2022
Features:

- Updated SDK to version 1.7.2.
- Added support for alphanumeric pairing key.
- Bug fixes and performance improvements.

Compatibility:

- Android **target** SDK version is updated to API level 33 (Android 13)
- Dependencies updated to their latest versions:
    * In the 'build.gradle' file at the **project** level:
        * 'com.android.tools.build:gradle:**7.2.2**'
        * 'com.google.gms:google-services:**4.3.13**'
        * 'androidx.navigation:navigation-safe-args-gradle-plugin:**2.5.1**'
    * In the 'build.gradle' file at the **app** level:
        * 'com.google.code.gson:gson:**2.9.0**'

## v1.7.1 - June 13th, 2022
Features:

- Updated SDK to version 1.7.1.

Bug fixes:

- Improvements to device integrity checks in the SDK.

Compatibility:

- Dependencies updated to their latest versions:
    * In the 'build.gradle' file at the **project** level:
        * 'com.android.tools.build:gradle:**7.1.2**'
        * 'androidx.navigation:navigation-safe-args-gradle-plugin:**2.4.1**'


## v1.7.0 - April 25th, 2022
Features:

- Updated SDK to version 1.7.0
- Added support for authentication using QR Code scanning or manual typing of an authentication code
- Added Certificate Transparency mechanism to protect against mis-issued certificates
- The JWT signature validation updated to use more strong EC algorithm.


Compatibility:
- Minimal Android version is updated to 26 (Android 8)
- Dependencies updated to their latest versions:
    * In the 'build.gradle' file at the **project** level:
        * 'com.android.tools.build:gradle:**7.1.1**'
        * 'com.google.gms:google-services:**4.3.10**'
        * 'androidx.navigation:navigation-safe-args-gradle-plugin:**2.3.5**'
    * In the 'build.gradle' file at the **app** level:
        * 'org.bitbucket.b_c:jose4j:**0.7.9**'
        * 'com.google.android.gms:play-services-safetynet:**18.0.1**'
        * 'com.google.code.gson:gson:**2.8.9**'
- Dependencies added:
    * In the 'build.gradle' file at the **app** level:
        * 'com.appmattus.certificatetransparency:certificatetransparency-android:1.0.0'


## v1.6.0 - August 1st, 2021
Features:

- Updated SDK to version 1.6.0.
- Added support for device integrity validation for threat protection.


Compatibility:
- Dependencies updated to their latest versions:
    * In the 'build.gradle' file at the **project** level:
        * 'com.android.tools.build:gradle:**4.2.1**'
        * 'com.google.gms:google-services:**4.3.8**'
        * 'androidx.navigation:navigation-safe-args-gradle-plugin:**2.3.5**'
    * In the 'build.gradle' file at the **app** level:
        * Migrated to FireBase BOM:
            * implementation platform('com.google.firebase:firebase-bom:26.3.0')
            * implementation 'com.google.firebase:firebase-core'
            * implementation 'com.google.firebase:firebase-messaging'
        * 'androidx.appcompat:appcompat:**1.3.0**'


## v1.5.0 - April 6th, 2021
Features:
- Updated SDK to version 1.4.0.
- Added support for one time passcode and disable SDK push notifications.

Compatibility notes:
- Deprecated current pairing method and added support to new one with returned object of PairingInfo.
- Deprecated current processRemoteNotification method and added support to new one with Context as a parameter.

Dependencies updated:
- com.android.tools.build:gradle:4.0.1
- com.google.gms:google-services:4.3.4

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
