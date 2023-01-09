package com.pingidentity.pingone;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.pingidentity.authenticationui.PingAuthenticationUI;
import com.pingidentity.pingidsdkv2.PairingObject;
import com.pingidentity.pingidsdkv2.PingOne;
import com.pingidentity.pingidsdkv2.PingOneSDKError;
import com.pingidentity.pingidsdkv2.types.PairingInfo;

public class MobileAuthenticationFrameworkActivity extends AppCompatActivity {

    private PingAuthenticationUI pingAuthenticationUI;
    AlertDialog alertDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mob_auth_framework);
        Button start = findViewById(R.id.button_start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pingAuthenticationUI = new PingAuthenticationUI();
                pingAuthenticationUI.authenticate(MobileAuthenticationFrameworkActivity.this, PingOne.generateMobilePayload(MobileAuthenticationFrameworkActivity.this), null);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PingAuthenticationUI.AUTHENTICATION_UI_ACTIVITY_REQUEST_CODE){
            if (resultCode == Activity.RESULT_OK){
                if(data!=null && data.hasExtra("serverPayload")){
                    PingOne.processIdToken(data.getStringExtra("serverPayload"), new PingOne.PingOnePairingObjectCallback() {
                        @Override
                        public void onComplete(@Nullable PairingObject pairingObject, @Nullable PingOneSDKError error) {
                            if (pairingObject!=null){
                                showApproveDenyDialog(pairingObject);
                            }
                        }
                    });
                }
                if (data!=null && data.hasExtra("access_token")){
                    Log.i("Main Activity", "Retrieved access token");
                    String accessToken = data.getStringExtra("access_token");
                    Log.i("Access token",
                            new String(Base64.decode(accessToken
                                            .substring(accessToken.indexOf('.'), accessToken.lastIndexOf('.')),
                                    Base64.DEFAULT)));
                }
            }
        }
    }

    private void showApproveDenyDialog(final PairingObject pairingObject){
        if(alertDialog!=null && alertDialog.isShowing()){
            alertDialog.cancel();
        }
        alertDialog = new AlertDialog.Builder(this)
                .setTitle("Allow pairing?")
                .setMessage("Do you want to pair this device?")
                .setPositiveButton(R.string.approve_button_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pairingObject.approve(MobileAuthenticationFrameworkActivity.this, new PingOne.PingOneSDKPairingCallback() {
                            @Override
                            public void onComplete(@Nullable final PingOneSDKError error) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (error != null) {
                                            Log.e("Pairing failed", error.getMessage());
                                        }else{
                                            pingAuthenticationUI.continueAuthentication(MobileAuthenticationFrameworkActivity.this);
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onComplete(@Nullable PairingInfo pairingInfo, @Nullable final PingOneSDKError pingOneSDKError) {
                                this.onComplete(pingOneSDKError);
                            }
                        });
                    }
                })
                .setNegativeButton(R.string.deny_button_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MobileAuthenticationFrameworkActivity.this, "Pairing declined", Toast.LENGTH_LONG).show();
                        alertDialog.dismiss();
                    }
                })
                .create();
        alertDialog.show();
        //for automation tests
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setContentDescription(getString(R.string.button_approve));
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setContentDescription(getString(R.string.button_deny));

    }
}
