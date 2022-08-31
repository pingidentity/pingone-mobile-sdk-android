# PingOne for Customers Mobile SDK

## Overview

PingOne for Customers Mobile SDK is a set of components and services targeted at enabling organizations to include multifactor authentication (MFA) into native applications.
This solution leverages Ping Identity’s expertise in MFA technology, as a component that can be embedded easily and quickly into a new or existing application. The PingOne for Customers Mobile SDK package comprises of the following components:

* The PingOne for Customers Mobile SDK library for Android applications.
* A sample app example source code for Android.
* Mobile Authentication Framework for Android developers integrated inside a sample app.

The Sample Code directory contains the Android project that is ready to be built after all the prerequisites are satisfied. To open the Sample Code as a Project in Android Studio, clone the whole directory and go to: File -> New -> Import Project.
Choose the Sample Code folder as the project's root folder.

Release notes can be found [here](./release-notes.md).

### Documentation

Reference documentation is available for PingOne for Customers Mobile SDK, describing its capabilities, features, installation and setup, integration with mobile apps, deployment and more:

* [PingOne for Customers Mobile SDK release notes and admin related documentation](https://docs.pingidentity.com/csh?Product=p1&context=p1mfa_c_introduction)
* [PingOne for Customers Mobile SDK developer documentation](https://apidocs.pingidentity.com/pingone/native-sdks/v1/api/#pingone-mfa-native-sdks)


## Set up a mobile app using the PingOne SDK sample code

Note: PingOne for Customers Mobile SDK supports Android 8.0 (API level 26) and up, Gradle 7.2 and up, Java 11 and up.


### Prerequisites

Prepare the FCM push messaging mandatory data from Firebase developers console:

* Server key
* Package name
* google-services.json

Refer to: [Add Firebase to your Android project](https://firebase.google.com/docs/android/setup).

### Configure FCM push messaging on the PingOne Portal

#### FCM Push Notification:

Add the google-services.json retrieved from the Firebase developers console to your project.

When configuring your PingOne SDK application in the PingOne admin web console (**Connections > Applications > {NATIVE application} > Edit > Authenticator**), you should fill in the Package Name and the Server Key. See [Edit an application](https://documentation.pingidentity.com/pingone/p14cAdminGuide/index.shtml#p1_t_editApplication.html) in the administration guide.



#### Add the PingOne SDK component into your existing project

1. Download the [PingOne.aar](SDK/PingOne.aar) library file.

2. Make the following changes to your `build.gradle` files in order to add the PingOne SDK component dependency:
	* Open your top-level `build.gradle` file, to add the PingOne SDK component as a dependency. Add the following lines to the `allprojects` node inside the `repositories` node:

	    ```
	    flatDir {
	        dirs 'libs'
	    }
	    ```

	* Create a `libs` folder inside your module’s folder. Copy the PingOne SDK component file `PingOne.aar` into the `libs` folder. Add the following dependency to the modules that use the PingOne SDK component:

	    ```
	    {
	        implementation fileTree(include: ['*.aar'], dir: 'libs')
	    }
	    ```

	*  As the PingOne SDK component is loaded locally, you’ll have to add the PingOne SDK component’s dependencies manually in order to be able to compile and run it. Add these dependencies under the PingOne SDK component dependency:

	   ```java
       implementation 'org.slf4j:slf4j-api:1.7.30'
       implementation 'com.github.tony19:logback-android:2.0.0'

       implementation 'com.madgag.spongycastle:core:1.58.0.0'
       implementation 'com.madgag.spongycastle:bcpkix-jdk15on:1.58.0.0'

       //Import the Firebase BoM (Bill of Materials)
       implementation platform('com.google.firebase:firebase-bom:26.3.0')

       //FireCloud Messaging Services
       implementation 'com.google.firebase:firebase-core'
       implementation 'com.google.firebase:firebase-messaging'
       
       //Google's gson library to build and parse JSON format
       implementation 'com.google.code.gson:gson:2.8.9'

       /*
        * The jose.4.j library is an open source (Apache 2.0) implementation
        * of JWT and the JOSE specification suite
        */
         implementation 'org.bitbucket.b_c:jose4j:0.7.9'
       
       
       // Google's SafetyNet library that allows validating device integrity
       implementation 'com.google.android.gms:play-services-safetynet:18.0.1'


       implementation "androidx.lifecycle:lifecycle-extensions:2.2.0"

       /*
       * The library that provides ability to use Certificate Transparency
       * https://www.certificate-transparency.org
       */
       implementation("com.appmattus.certificatetransparency:certificatetransparency-android:1.0.0")
       ```						


Starting Android 13 (API level 33) the application needs to request the 'Post Notifications' permission from the user to be able to show notifications. For more information see [Notification Runtime Permission Documentation](https://developer.android.com/guide/topics/ui/notifiers/notification-permission)

### Pairing

To manually pair the device, call the following method with your pairing key:

```java
PingOne.pair(context, pairingKey, new PingOne.PingOneSDKPairingCallback())
```

To automatically pair the device using OpenID Connect:

1. call this function to get the PingOne SDK mobile payload:
```java
public static String generateMobilePayload(Context context);
```
2. pass the received mobile payload on the OIDC request as the value of query param: `mobilePayload`
3. call this function with the ID token after the OIDC authentication completes:
```java
public static void processIdToken(String idToken, PingOnePairingCallback callback);
```

### Working with push messages in Android

PingOne SDK utilizes push messaging in order to authenticate end users. PingOne SDK can work side by side within an app that uses push messaging. This page details the steps needed in order to work with push messages in Android. Your application may receive push messages from the PingOne SDK server, and also from other sources. As a result, your implementation of the FirebaseMessagingService will have to differentiate between push messages sent from the PingOne SDK server and other messages, and pass them to the PingOne SDK component for processing.
In your app, add the appropriate section in your androidmanifest.xml file (FCM messaging service), and add the appropriate class.


#### Register device token on PingOne server

Retrieve the FCM Registration Token Id from the FCM and set it in the PingOne Library by calling
 ```java
PingOne.setDeviceToken(context, token, new PingOne.PingOneSDKCallback())
```
Make sure you set the device’s FCM registration token before you call `PingOne.pair`, and make sure you update the PingOne SDK Library with the new device token each time it changes.

### Handling Push Notifications

Implement the PingOne library’s push handling by passing the RemoteMessage received from FCM to the PingOne Library.
PingOne SDK will only handle push notifications which were issued by the PingOne SDK server. For other push notifications, `PingOneSDKError` with the code `10002, unrecognizedRemoteNotification` will be returned.

```java
@Override
public void onMessageReceived(final RemoteMessage remoteMessage) {
    PingOne.processRemoteNotification(context, remoteMessage, new PingOne.PingOneNotificationCallback() {
        @Override
	public void onComplete(@Nullable NotificationObject notificationObject, PingOneSDKError error) {
	    if (notificationObject == null){
	        //the push is not from PingOne - apply your customized application logic
	    }else{
	       //the object contains two options - approve and deny - present them to the user             
	    }
	}
    });
}
```


### Mobile Authentication Framework

The sample code contains two modules: `PingAuthenticationUI` and `PingAuthenticationCore`.
The following method starts an authentication process when the user taps "Authentication API" on the main screen. The authentication process is completed by the PingFederate Authentication API.
**Note:** Before calling this method, you need to update your `OIDC_ISSUER` and `CLIENT_ID` in the `gradle.properties` class at `PingAuthenticationCore` module. See [Authentication API for Android Developers](https://github.com/pingidentity/mobile-authentication-framework-android)
 ```java
 public void authenticate(@NonNull Activity context, @NonNull String mobilePayload, @Nullable String dynamicData)
```
This is public method of PingAuthenticationUI module, which should be instantiated first as follows:
```java
    PingAuthenticationUI authenticationUI = new PingAuthenticationUI();
    authenticationUI.authenticate(context, mobilePayload, dynamicData);

```

### PingOne Mobile SDK sample app

The PingOne Mobile SDK bundle provides a sample app that includes all the basic flows in order to help you get started.


### Send Logs

The PingOne Mobile SDK bundle writes fixed size, encrypted log messages to memory. To send these logs to our server for support, call the ```public static void sendLogs(Context context, PingOneSendLogsCallback callback)``` method.
For example:
 ```java
PingOne.sendLogs(context, new PingOne.PingOneSendLogsCallback() {
    @Override
    public void onComplete(@Nullable final String supportId, @Nullable PingOneSDKError pingOneSDKError) {
        if(supportId!=null){
            // pass this supportId value to PingOne support team
        }
     }
});
```

### Get One Time Passcode

Requests the SDK to provide One Time Passcode.

Signature:
```java
public static void getOneTimePassCode(Context context, PingOneOneTimePasscodeCallback callback)
```

For example:
 ```java
PingOne.getOneTimePassCode(context, new PingOne.PingOneOneTimePasscodeCallback() {
                @Override
                public void onComplete(@Nullable OneTimePasscodeInfo otpData, @Nullable PingOneSDKError error) {
					//handle response
				}
};
```

### Device Integrity Validation

PingOne uses Google's SafetyNet to perform device integrity validation for threat protection.
To use this feature, you should obtain a SafetyNet API Key. Refer to [Obtain a SafetyNet API Key](https://developer.android.com/training/safetynet/attestation#obtain-api-key).
The retrieved API key should be passed to the PingOne SDK using the following new API method:

```java
PingOne.setSafetyNetApiKey(context, apiKey);
```

### Authentication via QR code scanning

PingOne SDK provides an ability to authenticate via scanning the QR code (or typing the code manually). The code should
be passed to the PingOne SDK using the following API method:

```java
PingOne.authenticate(context, authCode, new PingOne.PingOneAuthenticationCallback() {
    @Override
	public void onComplete(@Nullable AuthenticationObject authObject, @Nullable PingOneSDKError error){
        if (authObject != null){
            //parse authObject (see below)
		}
    }
});
```

authCode should be passed as is or inside a URI. For example: "7F45HGf5", "https://myapp.com/pingonesdk?authentication_code=7F45HGf5", "pingonesdk?authentication_code=7F45HGf5"

AuthenticationObject is implemented as Parcelable to provide the developers an ability to
pass it between activities and/or fragments and contains the following fields and methods:
```java
	public class AuthenticationObject{
    	//for inner use
    	String requestId;
    	//for inner use
		String authCode;
		/*
		 * a JsonArray of users. See UserModel below for further understanding
		 * what it contains. 
		 */
		JsonArray users;
		//for passing any data from server to end-user
		String clientContext;
		/*
		 * a status String value returned from a server when user calls an authenticate API method
		 * Possible values at this step:
		 * CLAIMED
		 * EXPIRED
		 * COMPLETED	
		 */
		String status;
		/*
		 * String that determines if user approval is required to complete an authentication			
		 * Possible values:
		 * REQUIRED
		 * NOT_REQUIRED
		 */
		String needsApproval;
		
		/*
		 * if userApproval is "REQUIRED" the approve or deny method should be called with a userId
		 * of the user, who triggered the method. The application should register for a callback
		 * that will return Status with one of the following values:
		 * COMPLETED
		 * EXPIRED
		 * DENIED 
		 */
		public void approve(Context context, String userId, PingOneAuthenticationStatusCallback callback);
		public void deny(Context context, String userId, PingOneAuthenticationStatusCallback callback);
}
```
The JsonArray of users may be parsed to the array of following model:
```java
public class UserModel{
    String userId;
	String email;
	String given;
	String family;
	String username;
}
```

## Disclaimer

THE SAMPLE CODE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SAMPLE CODE OR THE USE OR OTHER DEALINGS IN
THE SAMPLE CODE.  FURTHERMORE, THIS SAMPLE CODE IS NOT COMMERCIALLY SUPPORTED BY PING IDENTITY BUT QUESTIONS MAY BE ADDRESSED TO PING'S SUPPORT CENTER OR MAY BE OTHERWISE ADDRESSED IN THE RELATED DOCUMENTATION.

Any questions or issues should go to the support center, or may be discussed in the [Ping Identity developer communities](https://community.pingidentity.com/collaborate).
