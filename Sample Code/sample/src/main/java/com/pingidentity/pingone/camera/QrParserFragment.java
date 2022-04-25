package com.pingidentity.pingone.camera;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pingidentity.pingidsdkv2.AuthenticationObject;
import com.pingidentity.pingidsdkv2.PingOne;
import com.pingidentity.pingidsdkv2.communication.models.PingOneDataModel;
import com.pingidentity.pingone.R;
import com.pingidentity.pingone.models.UserModel;
import com.pingidentity.pingone.models.UsersAdapter;

import java.util.ArrayList;

public class QrParserFragment extends Fragment {

    private ProgressBar progressBar;
    private ListView listView;
    private AuthenticationObject authenticationObject;
    private static final String TAG = "QrParserFragment";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_qr_parsing, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = view.findViewById(R.id.qr_authentication_progress_bar);
        listView = view.findViewById(R.id.usersList);
        if (getArguments()!=null && getArguments().containsKey("qrCodeContent")) {
            showVerifyingLayout();
            PingOne.authenticate(requireContext(), getArguments().getString("qrCodeContent"),
                    (object, error) -> {
                if(error!=null){
                    Log.e(TAG, error.toString());
                    showDialog(error.toString());
                }else {
                    if (object!=null) {
                        authenticationObject = object;
                        parseAuthenticationObject(authenticationObject);
                    }
                }
            });
        }else{
            showDialog("Error: insufficient parameters");
        }
    }

    private void showVerifyingLayout(){
        requireActivity().runOnUiThread(() ->
                progressBar.setVisibility(View.VISIBLE));
    }

    private void hideVerifyingLayout(){
        requireActivity().runOnUiThread(() ->
                progressBar.setVisibility(View.GONE));
    }

    private void parseAuthenticationObject(AuthenticationObject authenticationObject) {
        hideVerifyingLayout();
        switch (authenticationObject.getStatus().toUpperCase()){
            case "COMPLETED":
            case "DENIED":
            case "EXPIRED":
                showDialog(String.format(getString(R.string.alert_dialog_format_message),
                        authenticationObject.getStatus()));
                break;
            case "CLAIMED":
                handleClaimedStatusOfAuthentication(authenticationObject);
                break;
            default:
                Log.e(TAG, "Error: unexpected status");
                showDialog("Error: unexpected status");
                break;
            }
    }

    private void approveManualAuthentication(String userId){
        showVerifyingLayout();
        authenticationObject.approve(requireContext(), userId, (status, error) -> {
            hideVerifyingLayout();
            if(status!=null){
                showDialog(String.format(getString(R.string.alert_dialog_format_message), status));
            }else{
                if (error!=null) {
                    Log.e(TAG, error.toString());
                    showDialog(error.toString());
                }
            }
        });
    }

    private void denyManualAuthentication(String userId){
        showVerifyingLayout();
        authenticationObject.deny(requireContext(), userId, (status, error) -> {
            hideVerifyingLayout();
            if(status!=null){
                showDialog(String.format(getString(R.string.alert_dialog_format_message), status));
            }else{
                if (error!=null) {
                    Log.e(TAG,error.toString());
                    showDialog(error.toString());
                }
            }
        });
    }


    private void handleClaimedStatusOfAuthentication(AuthenticationObject authenticationObject){
        if (authenticationObject.getUsers().size()>1){
            handleSeveralUsersFlow(authenticationObject);
        }else{
            handleSingleUserFlow(authenticationObject);
        }
    }

    private void handleSeveralUsersFlow(AuthenticationObject authenticationObject) {
        ArrayList<UserModel> users = new ArrayList<>();
        for (JsonElement user: authenticationObject.getUsers()){
            UserModel userModel = new UserModel();
            JsonObject jsonUser = user.getAsJsonObject();
            //mandatory fields
            userModel.setUsername(jsonUser.get("username").getAsString());
            userModel.setUserId(jsonUser.get("id").getAsString());
            /*
             * non-mandatory fields if not set getAsString will throw a null pointer exception
             * thus developer should check for a null value or get a nullable String with
             * */
            userModel.setEmail(jsonUser.get("email").toString());
            userModel.setGiven(jsonUser.get("name").getAsJsonObject().get("given").toString());
            userModel.setFamily(jsonUser.get("name").getAsJsonObject().get("family").toString());
            users.add(userModel);
        }
        UsersAdapter adapter = new UsersAdapter(requireContext(), users);
        requireActivity().runOnUiThread(() -> {
            listView.setAdapter(adapter);
            listView.setOnItemClickListener((parent, view, position, id) -> {
                if (authenticationObject.getNeedsApproval().
                        equalsIgnoreCase(PingOneDataModel.AUTHENTICATION_USER_APPROVAL_VALUE.REQUIRED.name())){
                    showApproveDenyDialog(users.get(position).getUserId());
                }else{
                    approveManualAuthentication(users.get(position).getUserId());
                }
            });
        });
    }

    private void handleSingleUserFlow(AuthenticationObject authenticationObject) {
        if (authenticationObject.getNeedsApproval().
                equalsIgnoreCase(PingOneDataModel.AUTHENTICATION_USER_APPROVAL_VALUE.REQUIRED.name())){
            String userId = authenticationObject.getUsers().get(0).getAsJsonObject().get("id").getAsString();
            showApproveDenyDialog(userId);
        }else{
            //should never get here
            Log.e(TAG,"Error: unexpected single user status");
            showDialog("Error: unexpected single user status");
        }
    }

    private void showDialog(String message){
        hideVerifyingLayout();
        new Handler(Looper.getMainLooper()).post(() ->
                new AlertDialog.Builder(requireContext())
                        .setMessage(message)
                        .setPositiveButton("OK", (dialog, which) ->
                                requireActivity().finish())
                        .setOnCancelListener(dialog ->
                                requireActivity().finish())
                        .show());
    }

    private void showApproveDenyDialog(String userId){
        hideVerifyingLayout();
        new Handler(Looper.getMainLooper()).post(() -> {
                AlertDialog mDialog;

                mDialog = new AlertDialog.Builder(requireContext())
                .setMessage("Do you want to approve authentication for a user " + userId)
                .setPositiveButton("Approve", (dialog, which) ->
                        approveManualAuthentication(userId))
                .setNegativeButton("Deny", (dialog, which) ->
                        denyManualAuthentication(userId))
                .setOnCancelListener(dialog -> {
                    if (authenticationObject.getUsers().size()<2){
                        //close if single user canceled dialog
                        requireActivity().finish();
                    }
                })
                .create();
                 mDialog.setCanceledOnTouchOutside(true);
                 mDialog.show();
                });

    }
}
