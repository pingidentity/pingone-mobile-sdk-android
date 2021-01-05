package com.pingidentity.authcore;

import com.pingidentity.authcore.models.AuthenticationState;

/**
 * An interface to connect between hosting application/module with PingAuthenticationCore
 * The interface that can be implemented in any dependent module to retrieve the
 * {@link AuthenticationState} and act accordingly.
 *
 * See LICENSE.txt for the Ping Authentication licensing information.
 */
public interface AuthenticationCallback {
    void onStateChanged(AuthenticationState state);
}
