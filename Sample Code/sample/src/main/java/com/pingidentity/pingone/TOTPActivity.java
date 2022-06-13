package com.pingidentity.pingone;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.pingidentity.pingidsdkv2.PingOne;
import com.pingidentity.pingidsdkv2.PingOneSDKError;
import com.pingidentity.pingidsdkv2.types.OneTimePasscodeInfo;

public class TOTPActivity extends SampleActivity{

    private Handler handler = new Handler();
    private TextView passcode;
    private TextView passcode_timer;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_totp);

        passcode = findViewById(R.id.tv_passcode);
        passcode_timer = findViewById(R.id.tv_timer);
    }

    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler=null;
    }
    
    @Override
    protected void onResume(){
        super.onResume();
        startPassCodeSequence();
    }

    @Override
    protected void onPause(){
        super.onPause();
        if(countDownTimer != null){
            countDownTimer.cancel();
        }
    }

    private void startPassCodeSequence(){
        if(getPairingStatus()){
            PingOne.getOneTimePassCode(getApplicationContext(), new PingOne.PingOneOneTimePasscodeCallback() {
                @Override
                public void onComplete(@Nullable OneTimePasscodeInfo otpData, @Nullable PingOneSDKError error) {
                    if(otpData!=null){
                        startAnimation(otpData);
                    }else{
                        if (error!=null){
                            showOkDialog(error.toString());
                        }
                        passcode.setText("Passcode error");
                        passcode_timer.setText("");
                    }
                }
            });
        }else{
            passcode_timer.setText("");

            passcode.setText("Not Initialized");
        }
    }

    private boolean getPairingStatus(){
        return getSharedPreferences("InternalPrefs", MODE_PRIVATE).getBoolean("paired", false);
    }

    private void startAnimation(final OneTimePasscodeInfo otpData){
        final long runTime = (long) (otpData.getValidUntil()*1000 - System.currentTimeMillis());
        handler.post(new Runnable() {
            @Override
            public void run() {
                passcode.setText(otpData.getPasscode());
                countDownTimer = new CountDownTimer(runTime, 1000) {
                    public void onTick(long millisUntilFinished) {
                        passcode_timer.setText(String.valueOf(millisUntilFinished / 1000) + "s");
                    }

                    public void onFinish() {
                        if(handler!=null){
                            startPassCodeSequence();
                        }
                    }
                }.start();
            }
        });
    }

    private void showOkDialog(String message){
        new AlertDialog.Builder(TOTPActivity.this)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok,
                        (dialog, which) -> finish())
                .show()
                .getButton(DialogInterface.BUTTON_POSITIVE).setContentDescription(this.getString(R.string.alert_dialog_button_ok));
    }


}
