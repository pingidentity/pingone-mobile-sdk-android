package com.pingidentity.pingone;

import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.pingidentity.pingidsdkv2.NotificationObject;
import com.pingidentity.pingidsdkv2.PingOne;
import com.pingidentity.pingidsdkv2.PingOneSDKError;
import com.pingidentity.pingidsdkv2.types.NotificationProvider;
import com.pingidentity.pingone.notification.SampleNotificationsManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

/*
 * This is where you will receive FCM messages and new tokens, if previous token
 * was discarded by Google's security
 */
public class SampleMessagingService extends FirebaseMessagingService {

    private final String TAG = SampleMessagingService.class.getCanonicalName();
    @Override
    public void onMessageReceived(@NonNull final RemoteMessage remoteMessage) {
        Log.i(TAG, "Firebase RemoteMessage received");
        PingOne.processRemoteNotification(SampleMessagingService.this, remoteMessage, new PingOne.PingOneNotificationCallback() {
            @Override
            public void onComplete(@Nullable NotificationObject pingOneNotificationObject, @Nullable PingOneSDKError error) {

                if (pingOneNotificationObject == null) {
                    //the push is not from PingOne For Customer - handle it your way
                    Log.i(TAG, "Received push message is not from PingOne for Customer");
                    /*
                     * implement the logic to handle push that was received from the other service than
                     * PingOne for Customer
                     */
                    //...
                    return;
                }

                /*
                 * create intent to handle NotificationObject received from PingOne for Customer
                 */
                Intent handleNotificationObjectIntent = createPingOneNotificationIntent(remoteMessage, pingOneNotificationObject);
                /*
                 * handle state where application was open when push was received
                 */
                if(ProcessLifecycleOwner.get().getLifecycle().getCurrentState() == Lifecycle.State.RESUMED) {
                    if (pingOneNotificationObject.isTest()) {
                        /*
                         * received TEST push from PingOne service, you can choose to do nothing
                         */
                        Log.i("SampleMessagingService", "Test push received");
                    } else {
                        /*
                         * received push message from PingOne for Customer while app is in foreground.
                         */
                        startActivity(handleNotificationObjectIntent);
                    }
                    /*
                     * handle state where application was closed/background when push was received
                     */
                }else{
                    /*
                     * create notifications manager
                     */
                    SampleNotificationsManager sampleNotificationsManager = new SampleNotificationsManager(SampleMessagingService.this);
                    /*
                     * handle PingOne for Customers TEST push message
                     */
                    if (pingOneNotificationObject.isTest()){
                        sampleNotificationsManager.
                                buildAndSendPlainNotification(handleNotificationObjectIntent, false);
                        /*
                         * handle PingOne for Customers push message
                         */
                    }else {
                        /*
                         * Parse category from remoteMessage data to build a notification accordingly.
                         * May be null if no category set at the server side.
                         */
                        handleNotificationObjectIntent.putExtra("category", remoteMessage.getData().get("category"));
                        sampleNotificationsManager.buildAndSendNotificationAccordingToCategory(handleNotificationObjectIntent);
                    }
                }
            }
        });

    }

    /*
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(@NonNull String token) {
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        PingOne.setDeviceToken(this, token, NotificationProvider.FCM, new PingOne.PingOneSDKCallback() {
            @Override
            public void onComplete(@Nullable PingOneSDKError pingOneSDKError) {
                //check for an error and re-schedule service update
            }
        });
        saveFcmRegistrationToken(token);
    }

    private Intent createPingOneNotificationIntent(RemoteMessage remoteMessage, NotificationObject notificationObject){
        Intent handleNotificationObjectIntent = new Intent(SampleMessagingService.this, SampleActivity.class);
        handleNotificationObjectIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        handleNotificationObjectIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        /*
         * provide a NotificationObject received from PingOne for Customer SDK as a Parcelable
         * to the intent
         */
        handleNotificationObjectIntent.putExtra("PingOneNotification", notificationObject);
        /*
         * Optional: parse title and message body from RemoteMessage as well
         */
        parseTitleAndBody(remoteMessage, handleNotificationObjectIntent);

        return handleNotificationObjectIntent;
    }

    /*
     * Parse the "aps" part of the RemoteMessage to get notifications' title and body
     */
    private void parseTitleAndBody(@NonNull RemoteMessage remoteMessage, Intent intent){
        if(remoteMessage.getData().containsKey("aps")){
            try {
                JSONObject jsonObject = new JSONObject(Objects.requireNonNull(remoteMessage.getData().get("aps")));
                intent.putExtra("title", ((JSONObject)jsonObject.get("alert")).get("title").toString());
                intent.putExtra("body", ((JSONObject)jsonObject.get("alert")).get("body").toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveFcmRegistrationToken(@NonNull String token){
        SharedPreferences sharedPreferences = getSharedPreferences("InternalPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("pushToken", token);
        editor.apply();
    }

}
