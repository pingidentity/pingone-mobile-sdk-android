package com.pingidentity.pingone;

import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.huawei.hms.push.HmsMessageService;
import com.huawei.hms.push.RemoteMessage;
import com.pingidentity.pingidsdkv2.NotificationObject;
import com.pingidentity.pingidsdkv2.PingOne;
import com.pingidentity.pingidsdkv2.types.NotificationProvider;
import com.pingidentity.pingone.notification.SampleNotificationsManager;

import org.json.JSONException;
import org.json.JSONObject;

public class SampleHmsMessagingService extends HmsMessageService {
    private static final String TAG = "SampleHmsMessagingService";
    private static final String TITLE = "title";
    private static final String BODY = "body";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.i(TAG, "onMessageReceived is called");
        // Check whether the message is empty.
        if (remoteMessage == null) {
            Log.e(TAG, "Received message entity is null!");
            return;
        } else {
            Log.d(TAG, "Received message entity: " + remoteMessage.getData());
        }

        PingOne.processRemoteNotification(SampleHmsMessagingService.this, remoteMessage.getData(),
                (pingOneNotificationObject, error) -> {
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
                            Log.i(TAG, "Test push received");
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
                        SampleNotificationsManager sampleNotificationsManager = new SampleNotificationsManager(SampleHmsMessagingService.this);
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
                            handleNotificationObjectIntent.putExtra("category", parseCategoryFromRemoteMessage(remoteMessage));
                            sampleNotificationsManager.buildAndSendNotificationAccordingToCategory(handleNotificationObjectIntent);
                        }
                    }

                }
        );
    }

    /*
     * save HMS registration token to the shared preferences
     */
    private void saveHmsRegistrationToken(@NonNull String token) {
        SharedPreferences sharedPreferences = getSharedPreferences("InternalPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("pushToken", token);
        editor.apply();
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        saveHmsRegistrationToken(token);
        PingOne.setDeviceToken(this, token, NotificationProvider.HMS, pingOneSDKError -> {
            if (pingOneSDKError != null) {
                //re-schedule
            }
        });
    }

    private Intent createPingOneNotificationIntent(RemoteMessage remoteMessage, NotificationObject notificationObject){
        Intent handleNotificationObjectIntent = new Intent(SampleHmsMessagingService.this, SampleActivity.class);
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
     * Parse the HMS RemoteMessage to get notifications title and body
     */
    private void parseTitleAndBody(@NonNull RemoteMessage remoteMessage, Intent intent) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(remoteMessage.getData());
            // set the title
            if (remoteMessage.getData().contains(TITLE)) {
                intent.putExtra(TITLE, jsonObject.getString(TITLE));
            }
            // set the body
            if (remoteMessage.getData().contains(BODY)) {
                intent.putExtra(BODY, jsonObject.getString(BODY));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    private String parseCategoryFromRemoteMessage(@NonNull RemoteMessage remoteMessage){
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(remoteMessage.getData());
            if (jsonObject.has("category")){
                return jsonObject.getString("category");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


}

