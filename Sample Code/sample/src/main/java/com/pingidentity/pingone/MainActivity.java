package com.pingidentity.pingone;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.firebase.messaging.FirebaseMessaging;
import com.pingidentity.pingidsdkv2.PingOne;

public class MainActivity extends SampleActivity {

    private static final String ALLOW_PUSH_KEY = "allow_push";
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        logFCMRegistrationToken();
        //show the version in a text field
        TextView textViewVersion = findViewById(R.id.text_view_app_version);
        textViewVersion.setText(String.format("%s (%s)",
                BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE ));

        Button b1 = findViewById(R.id.button_pairing_key);
        b1.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, PairActivity.class)));

        Button b2 = findViewById(R.id.button_pairing_oidc);
        b2.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, OIDCActivity.class)));

        Button b3 = findViewById(R.id.button_authentication_api);
        b3.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, MobileAuthenticationFrameworkActivity.class)));

        Button b4 = findViewById(R.id.button_send_logs);
        b4.setOnClickListener(v -> PingOne.sendLogs(MainActivity.this, (supportId, pingOneSDKError) -> {
            if(supportId!=null) {
                runOnUiThread(() -> new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Logs sent")
                        .setMessage("Support id : " + supportId)
                        .setPositiveButton(android.R.string.ok, null)
                        .show()
                        .getButton(DialogInterface.BUTTON_POSITIVE).setContentDescription(MainActivity.this.getString(R.string.alert_dialog_button_ok)));
            }
        }));

        ToggleButton toggleAllowPush = findViewById(R.id.toggle_allow_push);
        boolean allowPush = getSharedPreferences().getBoolean(ALLOW_PUSH_KEY, true);
        toggleAllowPush.setChecked(allowPush);
        toggleAllowPush.setOnCheckedChangeListener((buttonView, isChecked) -> {
            getSharedPreferences().edit().putBoolean(ALLOW_PUSH_KEY, isChecked).apply();
            PingOne.allowPushNotifications(buttonView.getContext(), isChecked);
        });

        findViewById(R.id.btn_one_time_passcode).setOnClickListener(v -> startActivity(new Intent(MainActivity.this, TOTPActivity.class)));
    }

    public SharedPreferences getSharedPreferences(){
        return getSharedPreferences("InternalPrefs", Context.MODE_PRIVATE);
    }

    private void logFCMRegistrationToken(){
        SharedPreferences prefs = getSharedPreferences("InternalPrefs", MODE_PRIVATE);
        String token = prefs.getString("pushToken", null);
        if(token==null){
            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {

                    SharedPreferences.Editor editor = getSharedPreferences("InternalPrefs", MODE_PRIVATE).edit();
                    editor.putString("pushToken", task.getResult());
                    editor.apply();
                    Log.d(TAG,"FCM Token = " + task.getResult());
                });
        }else{
            Log.d(TAG,"FCM Token = " + token);
        }
    }

}
