package com.pingidentity.authenticationui.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.pingidentity.authcore.PingAuthenticationCore;
import com.pingidentity.authcore.beans.PingAuthenticationApiContract;
import com.pingidentity.authcore.models.AuthenticationState;
import com.pingidentity.authcore.models.RequestParams;
import com.pingidentity.authenticationui.PingAuthenticationUIActivity;
import com.pingidentity.authenticationui.R;


/**
 * A simple {@link Fragment} subclass that will present all the possible actions of the received
 * {@link AuthenticationState} as a {@link ListView}
 *
 * See LICENSE.txt for the Ping Authentication licensing information.
 */
public class ActionSelectionFragment extends Fragment {

    public ActionSelectionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_action_selection, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView actionsList = view.findViewById(R.id.actions_list);
        final AuthenticationState state = ActionSelectionFragmentArgs.fromBundle(getArguments()).getState();
        //todo error handling activity is closed
        requireActivity().setTitle(state.getStatus());
        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, state.getActions());
        actionsList.setAdapter(itemsAdapter);
        actionsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String actionClicked = ((TextView)view).getText().toString();
                switch (actionClicked){
                    case PingAuthenticationApiContract.ACTIONS.CHECK_USERNAME_PASSWORD:
                        navigate(state, view, R.id.loginFragment);
                        break;
                    case PingAuthenticationApiContract.ACTIONS.CHECK_OTP:
                        navigate(state, view, R.id.promptFragment);
                        break;
                    case PingAuthenticationApiContract.ACTIONS.CONTINUE_AUTHENTICATION:
                        if(state.getStatus().equalsIgnoreCase(PingAuthenticationApiContract.STATES.MOBILE_PAIRING_REQUIRED)){
                            ((PingAuthenticationUIActivity)requireActivity()).getPingAuthenticationUICallback().onStateChanged(state);
                        }else {
                            NavOptions navOptions = new NavOptions.Builder().setPopUpTo(R.id.actionSelectionFragment, true).build();
                            Navigation.findNavController(view).navigate(R.id.spinnerFragment, null, navOptions);
                            RequestParams params = new RequestParams();
                            params.setFlowId(state.getFlowId());
                            params.setAction(actionClicked);
                            PingAuthenticationCore.authenticate(params, ((PingAuthenticationUIActivity) requireActivity()).getPingAuthenticationUICallback());

                        }

                        break;
                    case PingAuthenticationApiContract.ACTIONS.AUTHENTICATE:
                        navigate(state, view, R.id.spinnerFragment);
                        RequestParams requestParams = new RequestParams();
                        requestParams.setFlowId(state.getFlowId());
                        requestParams.setMobilePayload(state.getMobilePayload());
                        requestParams.setAction(PingAuthenticationApiContract.ACTIONS.AUTHENTICATE);
                        PingAuthenticationCore.authenticate(requestParams, ((PingAuthenticationUIActivity) requireActivity()).getPingAuthenticationUICallback());
                        break;
                    case PingAuthenticationApiContract.ACTIONS.SELECT_DEVICE:
                        navigate(state, view, R.id.deviceSelectionFragment);
                        break;
                    default:
                        NavOptions navOptions = new NavOptions.Builder().setPopUpTo(R.id.actionSelectionFragment, true).build();
                        Navigation.findNavController(view).navigate(R.id.spinnerFragment, null, navOptions);
                        RequestParams params = new RequestParams();
                        params.setFlowId(state.getFlowId());
                        params.setAction(actionClicked);
                        PingAuthenticationCore.authenticate(params, ((PingAuthenticationUIActivity) requireActivity()).getPingAuthenticationUICallback());
                }
            }
        });
    }

    private void navigate(AuthenticationState state, View view, int destination){
        NavOptions navOptions = new NavOptions.Builder().setPopUpTo(R.id.actionSelectionFragment, true).build();
        Bundle args = new Bundle();
        args.putParcelable("state", state);
        Navigation.findNavController(view).navigate(destination, args, navOptions);
    }
}
