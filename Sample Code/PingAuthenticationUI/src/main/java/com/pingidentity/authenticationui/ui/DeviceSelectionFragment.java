package com.pingidentity.authenticationui.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.pingidentity.authcore.PingAuthenticationCore;
import com.pingidentity.authcore.beans.PingAuthenticationApiContract;
import com.pingidentity.authcore.models.AuthenticationState;
import com.pingidentity.authcore.models.Device;
import com.pingidentity.authcore.models.RequestParams;
import com.pingidentity.authenticationui.PingAuthenticationUIActivity;
import com.pingidentity.authenticationui.R;

import java.util.ArrayList;
import java.util.List;
/**
 * A simple {@link Fragment} subclass that will show a {@link ListView} of devices of the
 * authenticating user
 *
 * See LICENSE.txt for the Ping Authentication licensing information.
 */
public class DeviceSelectionFragment extends Fragment {

    public DeviceSelectionFragment(){
        //required empty public c-tor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_device_selection, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView deviceListView = view.findViewById(R.id.devices_list);
        List<String> listOfDeviceNames = new ArrayList<>();
        final List<String> listOfDeviceIds = new ArrayList<>();
        final AuthenticationState state = DeviceSelectionFragmentArgs.fromBundle(getArguments()).getState();
        for (Device device : state.getDevices()){
            if (!device.isUsable()) continue;
            listOfDeviceIds.add(device.getId());
            listOfDeviceNames.add(device.getType() + " " + (device.getName()!=null?device.getName():device.getTarget()));
        }
        ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, listOfDeviceNames);
        deviceListView.setAdapter(arrayAdapter);
        deviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NavOptions navOptions = new NavOptions.Builder().setPopUpTo(R.id.deviceSelectionFragment, true).build();
                Navigation.findNavController(view).navigate(R.id.spinnerFragment, null, navOptions);
                RequestParams params = new RequestParams();
                params.setFlowId(state.getFlowId());
                params.setAction(PingAuthenticationApiContract.ACTIONS.SELECT_DEVICE);
                params.setDeviceRef(listOfDeviceIds.get(position));
                params.setMobilePayload(state.getMobilePayload());
                PingAuthenticationCore.authenticate(params, ((PingAuthenticationUIActivity) requireActivity()).getPingAuthenticationUICallback());
            }
        });
    }
}
