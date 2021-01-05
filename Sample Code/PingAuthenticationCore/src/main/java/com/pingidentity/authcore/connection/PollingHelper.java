package com.pingidentity.authcore.connection;

import android.os.Handler;
import android.util.Log;

import com.pingidentity.authcore.AuthenticationCoreStateMachine;
import com.pingidentity.authcore.beans.PingAuthenticationApiContract;
import com.pingidentity.authcore.models.AuthenticationState;

/**
 * This class implements a task that polls the server for state changes. On receiving any state
 * different from PUSH_CONFIRMATION_WAITING returns response to {@link AuthenticationCoreStateMachine}
 *
 * See LICENSE.txt for the Ping Authentication licensing information.
 */
public class PollingHelper {

    public static final int POLLING_RETRIES = 15; //call every second
    private final AuthenticationCoreStateMachine callback;
    private final String flowId;

    public PollingHelper(String flowId, final AuthenticationCoreStateMachine callback){
        this.callback = callback;
        this.flowId = flowId;
    }

    public void poll(final int counter){
        // Create the Handler object (on the main thread by default)
        final Handler handler = new Handler();
        // Define the code block to be executed
        final Runnable runnableCode = new Runnable() {
            @Override
            public void run() {
                Log.d("PollingHelper", "Called poll on secondary thread");
                CommunicationManager.getInstance().sendPollingRequest(flowId, new AuthenticationCoreStateMachine(null){
                    @Override
                    public void onStateChanged(final AuthenticationState state) {
                        if (state.getStatus().equalsIgnoreCase(PingAuthenticationApiContract.STATES.PUSH_CONFIRMATION_WAITING)){
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    int retries = counter-1;
                                    if (retries==0){
                                        //stop polling and return timed out state, without polling action
                                        state.setStatus(PingAuthenticationApiContract.STATES.PUSH_CONFIRMATION_TIMED_OUT);
                                        state.removeAction(PingAuthenticationApiContract.ACTIONS.POLL);
                                        callback.onStateChanged(state);
                                        return;
                                    }
                                    poll(retries);
                                }
                            }, 1000);
                        } else {
                            callback.onStateChanged(state);
                        }
                    }
                });
            }
        };
        // Start the initial runnable task after delay by posting through the handler
        handler.postDelayed(runnableCode, 1000);
    }
}
