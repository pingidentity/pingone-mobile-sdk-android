package com.pingidentity.authcore.connection;

import android.net.Uri;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.net.HttpURLConnection;

import javax.net.ssl.HttpsURLConnection;

/**
 * Creates {@link java.net.HttpURLConnection} instances for use in direct interactions
 * with the authorization service, i.e. those not performed via a browser.
 *
 * See LICENSE.txt for the Ping Authentication licensing information.
 */

public interface Connection {

    /**
     * Creates a connection to the specified URL.
     * @param uri is the specified URL
     * @throws IOException if an error occurs while attempting to establish the connection.
     */
    @NonNull
    HttpURLConnection openConnection(@NonNull Uri uri) throws IOException;
}
