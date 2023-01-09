package com.pingidentity.pingone.notification;

import static com.pingidentity.pingone.notification.SampleNotificationsActionsReceiver.ACTION_APPROVE;
import static com.pingidentity.pingone.notification.SampleNotificationsActionsReceiver.ACTION_DENY;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.pingidentity.pingidsdkv2.NotificationObject;
import com.pingidentity.pingone.MainActivity;
import com.pingidentity.pingone.R;
import com.pingidentity.pingone.SampleActivity;

public class SampleNotificationsManager {

    private final String SAMPLE_NOTIFICATION_CHANNEL_ID = "pingOne.sample.channel";
    public static final int NOTIFICATION_ID_SAMPLE_APP = 1003;

    private final Context context;
    public SampleNotificationsManager(Context context){
        this.context = context;
        createNotificationChannel(context);
    }

    /*
     * Create the notification channel before posting any notifications. It's safe to call this
     * repeatedly because creating an existing notification channel performs no operation.
     */
    private void createNotificationChannel(Context context) {

        CharSequence name = context.getString(R.string.channel_name);
        String description = context.getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel(SAMPLE_NOTIFICATION_CHANNEL_ID, name, importance);
        channel.setDescription(description);
        /*
         * Register the channel with the system; you can't change the importance
         * or other notification behaviors after this
         */
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        assert notificationManager != null;
        notificationManager.createNotificationChannel(channel);
    }

    /*
     * there are several possible types of categories in remoteMessage data, build a
     * notification accordingly:
     * 1. category is null: build a notification without action buttons in analogy with iOS
     * 2. category "auth": build a "usual" notification with approve/deny buttons
     * 3. category "auth_open": build a notification with approve/deny buttons where approve action
     * will also open the application
     */
    public void buildAndSendNotificationAccordingToCategory(@NonNull Intent notificationIntent){
        String category = notificationIntent.getStringExtra("category");
        Log.i(SampleNotificationsManager.class.getCanonicalName(),
                "build notification for category " + category);
        if (category==null){
            buildAndSendPlainNotification(notificationIntent, true);
            return;
        }
        switch (category){
            case ("auth"):
                buildAndSendActionsNotification(notificationIntent, false);
                break;
            case ("auth_open"):
                buildAndSendActionsNotification(notificationIntent, true);
                break;
            default:
                buildAndSendPlainNotification(notificationIntent, true);
                break;
        }
    }

    /*
     * Every notification should respond to a tap, usually to open an activity in your app that
     * corresponds to the notification. To do so, you must specify a content intent defined with
     * a PendingIntent object and pass it to setContentIntent().
     */
    public void buildAndSendPlainNotification(@NonNull Intent notificationIntent, boolean openApplicationOnTap){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, SAMPLE_NOTIFICATION_CHANNEL_ID);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        /*
         * show the notification over the lock screen
         */
        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        if(notificationIntent.hasExtra("title")) {
            builder.setContentTitle(notificationIntent.getStringExtra("title"));
        }
        if (notificationIntent.hasExtra("body")){
            builder.setContentText(notificationIntent.getStringExtra("body"));
        }
        if (openApplicationOnTap){
            builder.setContentIntent(createOnTapPendingIntent(notificationIntent));
        }
        //cancel this notification "on-click" as it doesn't have any action items
        builder.setAutoCancel(true);

        sendNotification(builder);
    }

    public void buildAndSendActionsNotification(@NonNull Intent notificationIntent, boolean openOnApprove){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, SAMPLE_NOTIFICATION_CHANNEL_ID);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        /*
         * show the notification over the lock screen
         */
        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        if(notificationIntent.hasExtra("title")) {
            builder.setContentTitle(notificationIntent.getStringExtra("title"));
        }
        if (notificationIntent.hasExtra("body")){
            builder.setContentText(notificationIntent.getStringExtra("body"));
        }

        NotificationObject notificationObject = notificationIntent.getParcelableExtra("PingOneNotification");
        /*
         * to pass a custom parcelable via pending intent it is needed to wrap it in Bundle
         * to avoid RuntimeException which may happen on old devices while decoding
         */
        Bundle extra = new Bundle();
        extra.putParcelable("NotificationObject", notificationObject);

        if (openOnApprove){
            builder.addAction(createApproveAndOpenAction(notificationIntent));
        }else{
            builder.addAction(createApproveAction(extra));
        }
        builder.addAction(createDenyAction(extra));
        builder.setContentIntent(createOnTapPendingIntent(notificationIntent));
        builder.setAutoCancel(true);
        sendNotification(builder);
    }

    /*
     * create an action that sends approve intent to the broadcast receiver
     */
    @NonNull
    private NotificationCompat.Action createApproveAction(Bundle bundle){
        Intent approveIntent = new Intent(context, SampleNotificationsActionsReceiver.class);
        approveIntent.setAction(ACTION_APPROVE);
        approveIntent.putExtra("extra", bundle);
        /*
         * Very important to set request code and flag, so the PendingIntent
         * will be unique within the system and the bundle containing data will not be lost
         */
        PendingIntent approvePendingIntent = PendingIntent.getBroadcast(
                context,
                1,
                approveIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
                        //a must-have since Android 12
                        | PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Action.Builder(
                0,
                context.getString(R.string.notification_action_approve),
                approvePendingIntent)
                .build();
    }

    /*
     * create an action that sends deny intent to the broadcast receiver
     */
    @NonNull
    private NotificationCompat.Action createDenyAction(Bundle bundle){
        Intent denyIntent = new Intent(context, SampleNotificationsActionsReceiver.class);
        denyIntent.setAction(ACTION_DENY);
        denyIntent.putExtra("extra", bundle);
        /*
         * Very important to set request code and flag, so the PendingIntent
         * will be unique within the system and the bundle containing data will not be lost
         */
        PendingIntent denyPendingIntent = PendingIntent.getBroadcast(
                context,
                2,
                denyIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
                        //a must-have since Android 12
                        | PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Action.Builder(
                0,
                context.getString(R.string.notification_action_deny),
                denyPendingIntent)
                .build();
    }

    /*
     * Since Android 10 it is restricted to start Activity from BroadcastReceiver.
     * Thus, to achieve expected functionality we create a PendingIntent to an Activity instead
     * of Broadcast.
     */
    @NonNull
    private NotificationCompat.Action createApproveAndOpenAction(@NonNull Intent notificationIntent){
        Intent approveAndOpenIntent = new Intent(context, MainActivity.class);
        Bundle data = new Bundle();
        NotificationObject notificationObject = notificationIntent.getParcelableExtra("PingOneNotification");
        data.putParcelable("PingOneNotification", notificationObject);

        approveAndOpenIntent.putExtras(data);
        approveAndOpenIntent.setAction(ACTION_APPROVE);
        approveAndOpenIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent approveAndOpenPendingIntent =  PendingIntent.getActivity(context, (int) (System.currentTimeMillis() & 0xfffffff), approveAndOpenIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        return new NotificationCompat.Action.Builder(
                0,
                context.getString(R.string.notification_action_approve), approveAndOpenPendingIntent)
                .build();

    }

    private PendingIntent createOnTapPendingIntent(@NonNull Intent notificationIntent){
        NotificationObject notificationObject = notificationIntent.getParcelableExtra("PingOneNotification");

        Intent intent = new Intent(context, SampleActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP |Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        Bundle data = new Bundle();
        data.putParcelable("PingOneNotification", notificationObject);
        if(notificationIntent.hasExtra("title")) {
            data.putString("title", notificationIntent.getStringExtra("title"));
        }
        if (notificationIntent.hasExtra("body")){
            data.putString("body", notificationIntent.getStringExtra("body"));
        }
        intent.putExtras(data);
        return PendingIntent.getActivity(context, (int) (System.currentTimeMillis() & 0xfffffff), intent, PendingIntent.FLAG_UPDATE_CURRENT |PendingIntent.FLAG_IMMUTABLE);
    }

    private void sendNotification(@NonNull NotificationCompat.Builder builder) {
        Notification newMessageNotification = builder.build();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(NOTIFICATION_ID_SAMPLE_APP, newMessageNotification);
    }
}
