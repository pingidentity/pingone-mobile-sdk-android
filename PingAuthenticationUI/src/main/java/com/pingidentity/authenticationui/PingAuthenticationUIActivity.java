package com.pingidentity.authenticationui;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.pingidentity.authcore.PingAuthenticationCore;
import com.pingidentity.authcore.beans.PingAuthenticationApiContract;
import com.pingidentity.authcore.models.AuthenticationState;

import static com.pingidentity.authcore.beans.PingAuthenticationApiContract.STATES.*;
/*
 * See LICENSE.txt for the Ping Authentication licensing information.
 */
public class PingAuthenticationUIActivity extends AppCompatActivity {

    private PingAuthenticationUICallback pingAuthenticationUICallback;
    AuthenticationStateViewModel authenticationStateViewModel;
    private String mobilePayload;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authn_ui_activity);
        /*
         * Create a ViewModel the first time the system calls an activity's onCreate() method.
         * Recreated activities receive the same ViewModel instance created by the first activity
         */
        authenticationStateViewModel = new ViewModelProvider(this).get(AuthenticationStateViewModel.class);
        authenticationStateViewModel.getAuthenticationStateMutableLiveData().observe(this, new Observer<AuthenticationState>() {
            @Override
            public void onChanged(AuthenticationState state) {
                navigateAccordingToState(state);
            }
        });

        pingAuthenticationUICallback = new PingAuthenticationUICallback(authenticationStateViewModel);

        if (getIntent()!=null && getIntent().hasExtra("flowId") && !getIntent().hasExtra("mobilePayload")){
            String flowId = getIntent().getStringExtra("flowId");
            AuthenticationState state = new AuthenticationState(flowId, PingAuthenticationApiContract.STATES.MOBILE_PAIRING_COMPLETED);
            state.addAction(PingAuthenticationApiContract.ACTIONS.CONTINUE_AUTHENTICATION);
            pingAuthenticationUICallback.onStateChanged(state);
        } else if(getIntent()!=null && getIntent().hasExtra("mobilePayload")) {
            this.mobilePayload = getIntent().getStringExtra("mobilePayload");
            PingAuthenticationCore.authenticate(null, pingAuthenticationUICallback);

        }
    }


    /**
     * This method is responsible for navigation across elements of UI according to
     * {@link AuthenticationState}
     * @param authenticationState - current AuthenticationState
     */
    private void navigateAccordingToState(@NonNull final AuthenticationState authenticationState){
        switch (authenticationState.getStatus()){

            case USERNAME_PASSWORD_REQUIRED:
                PingAuthenticationUI.setFlowId(authenticationState.getFlowId());
                navigate(authenticationState, R.id.spinnerFragment, R.id.actionSelectionFragment);
                break;
            /*
             * Indicates that authentication is required.
             * This state will be returned from the adapter unless authentication is impossible for
             * the user and then MFA_COMPLETED or MFA_FAILED states will be returned with a
             * corresponding code.
             */
            case AUTHENTICATION_REQUIRED:
                authenticationState.setMobilePayload(mobilePayload);
                navigate(authenticationState, R.id.loginFragment, R.id.actionSelectionFragment);
                break;
            case OTP_REQUIRED:
            /*
             * Indicates a successful MFA. The API client must call continueAuthentication in order
             * to proceed the OIDC flow.
             */
            case MFA_COMPLETED:
                navigate(authenticationState, R.id.spinnerFragment, R.id.actionSelectionFragment);
                break;
            case DEVICE_SELECTION_REQUIRED:
                authenticationState.setMobilePayload(mobilePayload);
                navigate(authenticationState, R.id.spinnerFragment, R.id.actionSelectionFragment);
                break;
            case MOBILE_PAIRING_COMPLETED:
                //todo
                Bundle args1 = new Bundle();
                args1.putParcelable("state", authenticationState);
                NavOptions navOptions1 = new NavOptions.Builder().setPopUpTo(R.id.actionSelectionFragment, true).build();
                Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.actionSelectionFragment, args1, navOptions1);
                break;

            case MOBILE_PAIRING_REQUIRED:
                Intent resultIntent = new Intent();
                String serverPayload = authenticationState.getServerPayload();
                resultIntent.putExtra("serverPayload", serverPayload);
                resultIntent.putExtra(PingAuthenticationApiContract.STATE, authenticationState.getFlowId());
                setResultAndFinishDialog(MOBILE_PAIRING_REQUIRED, resultIntent);
                break;
            case PUSH_CONFIRMATION_WAITING:
                authenticationState.setMobilePayload(mobilePayload);
                navigate(authenticationState, R.id.spinnerFragment, R.id.actionSelectionFragment);
                break;
            case PUSH_CONFIRMATION_TIMED_OUT:
                authenticationState.setMobilePayload(mobilePayload);
                navigate(authenticationState, R.id.spinnerFragment, R.id.actionSelectionFragment);
                break;
            case COMPLETED:
                Toast.makeText(this, R.string.authentication_completed, Toast.LENGTH_LONG).show();
                break;
            case TOKEN_EXCHANGE_COMPLETED:
                Intent resultIntent1 = new Intent();
                resultIntent1.putExtra("access_token", authenticationState.getAccessToken());
                setResultAndFinishDialog("TOKEN EXCHANGE COMPLETED", resultIntent1);
                break;
            case PUSH_CONFIRMATION_REJECTED:
                authenticationState.setMobilePayload(mobilePayload);
                navigate(authenticationState, R.id.spinnerFragment, R.id.actionSelectionFragment);
                break;
            /*
             * Indicates a dead end. The API client can proceed with the OIDC flow by calling
             * cancelAuthentication. The adapter will return FAILURE.
             */
            case MFA_FAILED:
                showDeadEndErrorDialog(authenticationState);
                break;
            case ERROR_RECEIVED:
                if(authenticationState.getCode()!=null
                        && authenticationState.getCode().equalsIgnoreCase(PingAuthenticationApiContract.VALIDATION_ERROR)){
                    showErrorMessageDialog(authenticationState.getFullErrorJson());
                    break;
                }
            case FAILED:
                showDeadEndErrorDialog(authenticationState);
            default:

                break;
        }
    }

    private void navigate(AuthenticationState state, int popUpToFragment, int destination){
        Bundle args = new Bundle();
        args.putParcelable(PingAuthenticationApiContract.STATE, state);
        NavOptions navOptions = new NavOptions.Builder().setPopUpTo(popUpToFragment, true).build();
        Navigation.findNavController(this, R.id.nav_host_fragment).navigate(destination, args, navOptions);
    }

    private void setResultAndFinishDialog(String message, final Intent intent){
        new AlertDialog.Builder(this)
                .setTitle(message)
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    }
                })
                .setPositiveButton(getText(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    }
                }).create().show();
    }

    /*
     * Builds a dialog with error title and message. On this error user can dismiss dialog
     * and retry action
     */
    private void showErrorMessageDialog(String message){
        new AlertDialog.Builder(this)
                .setTitle(R.string.error_dialog_title)
                .setMessage(message)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
    }

    /*
     * Builds a dialog with error title and message. On this error user has no choice but close
     * Authentication Framework.
     */
    private void showDeadEndErrorDialog(AuthenticationState authenticationState){
        new AlertDialog.Builder(this)
                .setTitle(authenticationState.getStatus())
                .setMessage(authenticationState.getCode())
                .setPositiveButton(getText(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        setResult(Activity.RESULT_CANCELED);
                        finish();
                    }
                })
                .create().show();
    }

    public PingAuthenticationUICallback getPingAuthenticationUICallback(){
        return pingAuthenticationUICallback;
    }


}
