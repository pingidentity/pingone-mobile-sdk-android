package com.pingidentity.pingone;

import static com.pingidentity.pingone.notification.SampleNotificationsActionsReceiver.ACTION_APPROVE;
import static com.pingidentity.pingone.notification.SampleNotificationsManager.NOTIFICATION_ID_SAMPLE_APP;

import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.pingidentity.pingidsdkv2.NotificationObject;
import com.pingidentity.pingidsdkv2.PingOne;
import com.pingidentity.pingidsdkv2.PingOneSDKError;

public class SampleActivity extends AppCompatActivity {
    AlertDialog alertDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getIntent().hasExtra("PingOneNotification")){
            handleNotificationObjectIntent(getIntent());
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(intent.hasExtra("PingOneNotification")){
            handleNotificationObjectIntent(intent);
        }
    }

    private void handleNotificationObjectIntent(@NonNull Intent intent){
        NotificationObject pingOneNotificationObject = (NotificationObject) intent.getExtras().get("PingOneNotification");
        if (pingOneNotificationObject!=null) {
            /*
             * in "auth_open" push category scenario we want to silent-approve the auth, this means
             * we trigger the approve() method of the NotificationObject without asking user approval
             * and dismiss the notification
             */
            if (intent.getAction()!=null && intent.getAction().equalsIgnoreCase(ACTION_APPROVE)){
                NotificationManagerCompat.from(this).cancel(NOTIFICATION_ID_SAMPLE_APP);
                pingOneNotificationObject.approve(this, "auth_approve", new PingOne.PingOneSDKCallback() {
                    @Override
                    public void onComplete(@Nullable PingOneSDKError pingOneSDKError) {
                        if (pingOneSDKError!=null){
                            Log.e("Sample activity", "Silent approve action returned error " + pingOneSDKError.getMessage());
                        }else{
                            Log.i("Sample activity", "Silent approve action completed");
                        }
                    }
                });
                /*
                 * in "auth_open" push category scenario do not build the approve/deny user dialog
                 * as notification object already approved at this point
                 */
                return;
            }
            String title = "Authenticate?";
            String body = null;
            if (intent.hasExtra("title")) {
                title = intent.getStringExtra("title");
            }
            if (intent.hasExtra("body")) {
                body = intent.getStringExtra("body");
            }
            if (pingOneNotificationObject.getClientContext() != null) {
                JsonObject jsonObject = new Gson().fromJson(pingOneNotificationObject.getClientContext(), JsonObject.class);
                if (jsonObject.has("header_font_color")) {
                    changeTitleColor(jsonObject.get("header_font_color").getAsString());
                }
            }
            if (pingOneNotificationObject.isTest()) {
                showOkDialog(body);
            } else {
                showApproveDenyDialog(pingOneNotificationObject, title, body);
            }
        }
    }

    private void showOkDialog(String message){
        new AlertDialog.Builder(SampleActivity.this)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok,
                        (dialog, which) -> finish())
                .show()
                .getButton(DialogInterface.BUTTON_POSITIVE).setContentDescription(this.getString(R.string.alert_dialog_button_ok));
    }

    private void showApproveDenyDialog(final NotificationObject pingOneNotificationObject, String title, String body){
        if(alertDialog!=null && alertDialog.isShowing()){
            alertDialog.cancel();
        }
        alertDialog = new AlertDialog.Builder(this)
                .setTitle(title==null?"Authenticate?":title)
                .setMessage(body==null?"":body)
                .setPositiveButton(R.string.approve_button_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pingOneNotificationObject.approve(SampleActivity.this, "user", new PingOne.PingOneSDKCallback() {
                            @Override
                            public void onComplete(@Nullable final PingOneSDKError pingOneSDKError) {
                                runOnUiThread(() -> {
                                    if (pingOneSDKError != null) {
                                        showOkDialog(pingOneSDKError.toString());
                                    }else{
                                        finish();
                                    }
                                });

                            }
                        });
                    }
                })
                .setNegativeButton(R.string.deny_button_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pingOneNotificationObject.deny(SampleActivity.this, new PingOne.PingOneSDKCallback() {
                            @Override
                            public void onComplete(@Nullable final PingOneSDKError pingOneSDKError) {
                                runOnUiThread(() -> {
                                    if (pingOneSDKError != null) {
                                        showOkDialog(pingOneSDKError.toString());
                                    }else{
                                        finish();
                                    }
                                });
                            }
                        });
                    }
                })
                .setOnCancelListener(
                        dialog -> finish())
                .create();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();

        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setContentDescription(getString(R.string.button_approve));
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setContentDescription(getString(R.string.button_deny));
    }

    private void changeTitleColor(String color){
        if (getSupportActionBar()!=null) {
            Spannable text = new SpannableString(getSupportActionBar().getTitle());
            text.setSpan(new ForegroundColorSpan(Color.parseColor(color)), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            getSupportActionBar().setTitle(text);
        }
    }
}
