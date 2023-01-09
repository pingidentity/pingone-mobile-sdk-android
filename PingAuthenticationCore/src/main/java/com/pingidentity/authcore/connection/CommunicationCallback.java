package com.pingidentity.authcore.connection;

import com.pingidentity.authcore.beans.PingAuthenticationApiResponse;
import com.pingidentity.authcore.models.ErrorState;

/**
 * This interface describes a callback that will call a corresponding method in calling Thread
 * after {@link CommunicationTask} is finished.
 *
 * See LICENSE.txt for the Ping Authentication licensing information.
 */
public interface CommunicationCallback {
    void onSuccess(PingAuthenticationApiResponse response);
    void onError(ErrorState error);
    void onException(Exception e);
}
