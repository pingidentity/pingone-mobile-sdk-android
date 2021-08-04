package com.pingidentity.pingone;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.pingidentity.pingidsdkv2.PingOne;
import com.pingidentity.pingidsdkv2.PingOneSDKError;
import com.pingidentity.pingidsdkv2.types.PairingInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class PairActivity extends SampleActivity {
    private static final String TAG = PairActivity.class.getCanonicalName();
    Logger logger = LoggerFactory.getLogger(PairActivity.class);

    private EditText activationCodeInput;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pair);
        activationCodeInput = findViewById(R.id.activation_code_input);
        final Button buttonPair = findViewById(R.id.button_pair);
        buttonPair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonPair.setEnabled(false);
                String activationCode = activationCodeInput.getText().toString();
                PingOne.pair(PairActivity.this, activationCode, new PingOne.PingOneSDKPairingCallback() {
                    @Override
                    public void onComplete(PairingInfo pairingInfo, @Nullable PingOneSDKError error) {
                            this.onComplete(error);
                    }

                    @Override
                    public void onComplete(@Nullable final PingOneSDKError error) {
                        if (error==null) {
                            logger.info("Pairing complete");
                            runOnUiThread(() -> {
                                SharedPreferences.Editor sharedPreferences = getSharedPreferences("InternalPrefs", MODE_PRIVATE).edit();
                                sharedPreferences.putBoolean("paired", true);
                                sharedPreferences.apply();
                                showOkDialog("Device is paired successfully");
                                buttonPair.setEnabled(true);

                            });
                        }else{
                            runOnUiThread(() -> {
                                buttonPair.setEnabled(true);
                                showOkDialog(error.toString());
                            });
                        }
                    }
                });
            }
        });
    }



    private boolean getPairingStatus(){
        return getSharedPreferences("InternalPrefs", MODE_PRIVATE).getBoolean("paired", false);
    }

    private void showOkDialog(String message){
        new AlertDialog.Builder(PairActivity.this)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .show()
                .getButton(DialogInterface.BUTTON_POSITIVE).setContentDescription(this.getString(R.string.alert_dialog_button_ok));
    }

    /*
     * To share log file use this method. The FileProvider for your application_id and
     * PingOne log file is provided by the SDK component.
     */
    private void shareLogFile(Context context){
        /*
         * This is the full path to the PingOne log file
         */
        File file = new File(context.getFilesDir().getAbsolutePath()
                .concat(File.separator)
                .concat("pingone.log"));
        /*
         * Create an URI using FileProvider to make sure it will work on every Android version
         */
        Uri uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID, file);
        /*
         * Bundle an Uri into Intent and activate it
         */
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/*");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
        /*
         * Set flag to give temporary permission to external app to use your FileProvider
         */
        sharingIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        /*
         * Activate an intent (title is mandatory and configurable)
         */
        startActivity(Intent.createChooser(sharingIntent, "Choose an application"));

    }
}
