package com.pingidentity.authcore;

import com.pingidentity.authcore.beans.PingAuthenticationApiContract;
import com.pingidentity.authcore.connection.CommunicationManager;
import com.pingidentity.authcore.connection.PollingHelper;
import com.pingidentity.authcore.models.RequestParams;

/*
 * See LICENSE.txt for the Ping Authentication licensing information.
 */
class LogicLayer {


    void executeRequest(RequestParams params, AuthenticationCoreStateMachine callback){
        /*
         * if no request parameters provided we treat it as initial request
         */
        if (params == null){
            CommunicationManager.getInstance().sendInitialRequest(callback);
        }else switch (params.getAction()){
                case PingAuthenticationApiContract.ACTIONS.AUTHENTICATE:
                    CommunicationManager.getInstance().sendAuthenticationRequest(params.getFlowId(), params.getMobilePayload(), callback);
                break;
                case PingAuthenticationApiContract.ACTIONS.POLL:
                    PollingHelper pollingHelper = new PollingHelper(params.getFlowId(), callback);
                    pollingHelper.poll(PollingHelper.POLLING_RETRIES);
                break;
            case PingAuthenticationApiContract.ACTIONS.CHECK_USERNAME_PASSWORD:
                CommunicationManager.getInstance().sendCheckUsernamePasswordRequest(params.getFlowId(), params.getUsername(), params.getPassword(), callback);
                break;
            case PingAuthenticationApiContract.ACTIONS.SELECT_DEVICE:
                CommunicationManager.getInstance().sendDeviceSelectionRequest(params.getFlowId(), params.getDeviceRef(), params.getMobilePayload(), callback);
                break;
            case PingAuthenticationApiContract.ACTIONS.CHECK_OTP:
                CommunicationManager.getInstance().sendCheckOtpRequest(params.getFlowId(), params.getOtp(), params.getMobilePayload(), callback);
                break;
            default:
                CommunicationManager.getInstance().sendGenericActionRequest(params.getFlowId(), params.getAction(), callback);
        }
    }

}
