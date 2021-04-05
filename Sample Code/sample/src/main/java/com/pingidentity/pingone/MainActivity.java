package com.pingidentity.pingone;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.firebase.FirebaseApp;
import com.pingidentity.pingidsdkv2.PingOne;
import com.pingidentity.pingidsdkv2.PingOneSDKError;

public class MainActivity extends SampleActivity {

    private static final String ALLOW_PUSH_KEY = "allow_push";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(MainActivity.this);
        setContentView(R.layout.activity_main);
        //show the version in a text field
        TextView textViewVersion = findViewById(R.id.text_view_app_version);
        textViewVersion.setText(String.format("%s (%s)",
                BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE ));

        Button b1 = findViewById(R.id.button_pairing_key);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PairActivity.class));
            }
        });

        Button b2 = findViewById(R.id.button_pairing_oidc);
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, OIDCActivity.class));
            }
        });

        Button b3 = findViewById(R.id.button_authentication_api);
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MobileAuthenticationFrameworkActivity.class));
            }
        });

        Button b4 = findViewById(R.id.button_send_logs);
        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PingOne.sendLogs(MainActivity.this, new PingOne.PingOneSendLogsCallback() {
                    @Override
                    public void onComplete(@Nullable final String supportId, @Nullable PingOneSDKError pingOneSDKError) {
                        if(supportId!=null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    new AlertDialog.Builder(MainActivity.this)
                                            .setTitle("Logs sent")
                                            .setMessage("Support id : " + supportId)
                                            .setPositiveButton(android.R.string.ok, null)
                                            .show()
                                            .getButton(DialogInterface.BUTTON_POSITIVE).setContentDescription(MainActivity.this.getString(R.string.alert_dialog_button_ok));
                                }
                            });
                        }
                    }
                });
            }
        });

        ToggleButton toggleAllowPush = findViewById(R.id.toggle_allow_push);
        boolean allowPush = getSharePrefs().getBoolean(ALLOW_PUSH_KEY, true);
        toggleAllowPush.setChecked(allowPush);
        toggleAllowPush.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                getSharePrefs().edit().putBoolean(ALLOW_PUSH_KEY, isChecked);
                PingOne.allowPushNotifications(buttonView.getContext(), isChecked);
            }
        });

        findViewById(R.id.btn_one_time_passcode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, TOTPActivity.class));
            }
        });
    }

    public SharedPreferences getSharePrefs(){
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("prefs_file", Context.MODE_PRIVATE);
        return sharedPreferences;
    }

}
