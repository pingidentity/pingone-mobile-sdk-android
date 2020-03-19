# Release Notes

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

