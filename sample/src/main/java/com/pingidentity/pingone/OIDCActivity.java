package com.pingidentity.pingone;

import static android.app.PendingIntent.FLAG_MUTABLE;

import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.pingidentity.pingidsdkv2.PairingObject;
import com.pingidentity.pingidsdkv2.PingOne;
import com.pingidentity.pingidsdkv2.PingOneSDKError;
import com.pingidentity.pingidsdkv2.types.PairingInfo;

import net.openid.appauth.AppAuthConfiguration;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.ResponseTypeValues;

import java.util.HashMap;
import java.util.Map;

/*
 * This is sample OIDC Activity which shows how to use the AppAuth open code library
 * to implement login via OpenID Connect protocol.
 */
public class OIDCActivity extends AppCompatActivity {
    private static final String TAG = "OIDC_Activity";
    AuthorizationService authorizationService;
    AlertDialog alertDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oidc);

        AppAuthConfiguration.Builder appAuthConfigurationBuilder = new AppAuthConfiguration.Builder();

        authorizationService = new AuthorizationService(this, appAuthConfigurationBuilder.build());

        Button oidcButton = findViewById(R.id.button_pair_oidc);
        oidcButton.setOnClickListener(v -> discoverAndAuthorize());
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.i(TAG, "onResume triggered");

        AuthorizationResponse resp = AuthorizationResponse.fromIntent(getIntent());
        if(resp!=null){
            Log.i(TAG, "Authorization response retrieved");
            authorizationService.performTokenRequest(resp.createTokenExchangeRequest(),
                    (response, ex) -> {
                if(null == response || null==response.idToken){
                    return;
                }
                Log.i(TAG, "OpenIDConnect Authorization token retrieved");

                PingOne.processIdToken(response.idToken, (pairingObject, pingOneSDKError) -> {
                    if(pingOneSDKError!=null){
                        Log.i(TAG, pingOneSDKError.toString());
                        showOkDialog(pingOneSDKError.toString());
                        return;
                    }
                    if (pairingObject!=null){
                        //should show approve/deny dialogue
                        showApproveDenyDialog(pairingObject);
                    }
                });
            });
        }
    }

    private void discoverAndAuthorize(){
        if ((Uri.parse(BuildConfig.OIDC_ISSUER).getScheme() == null ||
                !Uri.parse(BuildConfig.OIDC_ISSUER).getScheme().startsWith("https"))){
            showOkDialog("Error: OIDC Issuer must start with https scheme");
            return;
        }
        AuthorizationServiceConfiguration.fetchFromIssuer(Uri.parse(BuildConfig.OIDC_ISSUER),
                (serviceConfiguration, ex) -> {
            if(ex!=null){
                Log.e(TAG, "failed to fetch configuration");
                ex.printStackTrace();
                return;
            }
            /*
             * We need to retrieve from the PingOne SDK an Object called MobilePayload
             * and add it to the AuthorizationRequest
             */
            Map<String, String> payload = new HashMap<>();
            String mobilePayload = PingOne.generateMobilePayload(OIDCActivity.this);
            payload.put("mobilePayload", mobilePayload);
            if(serviceConfiguration==null){
                Log.e(TAG, "failed to fetch configuration");
                return;
            }

            AuthorizationRequest.Builder authRequestBuilder =
                    new AuthorizationRequest.Builder(
                            serviceConfiguration, // the authorization service configuration
                            BuildConfig.CLIENT_ID, // the client ID, typically pre-registered and static
                            ResponseTypeValues.CODE, // the response_type value we want is a code
                            Uri.parse(BuildConfig.OIDC_REDIRECT_URI)); // the redirect URI to which the auth response is sent

            AuthorizationRequest authRequest = authRequestBuilder
                    .setScope(BuildConfig.SCOPE)
                    .setAdditionalParameters(payload) //pass the mobile payload to the server
                    .build();

            authorizationService.performAuthorizationRequest(authRequest,
                    createPostAuthorizationIntent());
            authorizationService.dispose();
            finish();
        });
    }

    private PendingIntent createPostAuthorizationIntent(){
        Intent intent = new Intent(this, this.getClass());
        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | FLAG_MUTABLE);
    }

    /*
     * Overriding the onBackPressed to dispose authorization service correctly
     * to avoid unnecessary error log messages
     */
    @Override
    public void onBackPressed() {
        if(authorizationService!=null){
            authorizationService.dispose();
        }
        super.onBackPressed();
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
                        /*
                         * the approve method will trigger the PingOneSDKPairingCallback object's
                         * onComplete(@Nullable PairingInfo, @Nullable PingOneSDKError) method.
                         * Since PingOneSDKPairingCallback extends PingOneSDKCallback (for backward
                         * compatibility reasons) the onComplete(@Nullable PingOneSDKError) method should
                         * be overridden as well. You can choose to do nothing in this method, it won't be
                         * called by default.
                         */
                        pairingObject.approve(OIDCActivity.this, new PingOne.PingOneSDKPairingCallback() {
                            @Override
                            public void onComplete(@Nullable PairingInfo pairingInfo, @Nullable PingOneSDKError error) {
                                Log.i(TAG, "PingOneSDKPairingCallback onComplete method triggered");
                                /*
                                 * you can parse a pairingInfo if needed:
                                 *
                                 * if(pairingInfo)!=null{
                                 *     //do what you need
                                 * }
                                 */

                                /*
                                 * proceed with the process by handling the error object. You can parse
                                 * the error here and never call this.onComplete(error) or separate error
                                 * handling to the second callback for more clean code
                                 */
                                this.onComplete(error);
                            }

                            @Override
                            public void onComplete(@Nullable final PingOneSDKError error) {
                                runOnUiThread(() -> {
                                    if (error != null) {
                                        showOkDialog(error.toString());
                                    }else{
                                        SharedPreferences.Editor sharedPreferences = getSharedPreferences("InternalPrefs", MODE_PRIVATE).edit();
                                        sharedPreferences.putBoolean("paired", true);
                                        sharedPreferences.apply();
                                        showOkDialog("Device is paired successfully");
                                    }
                                });
                            }
                        });
                    }
                })
                .setNegativeButton(R.string.deny_button_text, (dialog, which) -> {
                    Toast.makeText(OIDCActivity.this, "Pairing declined", Toast.LENGTH_LONG).show();
                    alertDialog.dismiss();
                })
                .create();
        alertDialog.show();

        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setContentDescription(getString(R.string.button_approve));
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setContentDescription(getString(R.string.button_deny));

    }

    private void showOkDialog(String message){
        new AlertDialog.Builder(OIDCActivity.this)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setNeutralButton("Copy", (dialog, which) -> {
                    ClipboardManager manager = (ClipboardManager)
                            getSystemService(Context.CLIPBOARD_SERVICE);
                    manager.setPrimaryClip(ClipData.newPlainText("Message Content", message));
                    Toast.makeText(OIDCActivity.this, "Copied", Toast.LENGTH_SHORT).show();
                })
                .show()
                .getButton(DialogInterface.BUTTON_POSITIVE).setContentDescription(this.getString(R.string.alert_dialog_button_ok));
    }
}
