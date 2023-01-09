package com.pingidentity.authcore.connection;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.pingidentity.authcore.BuildConfig;

import java.io.IOException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/*
 * See LICENSE.txt for the Ping Authentication licensing information.
 */
public class SecureConnection implements Connection {

    @NonNull
    @Override
    public HttpsURLConnection openConnection(@NonNull Uri uri) throws IOException {
        HttpsURLConnection connection = (HttpsURLConnection) new URL(uri.toString()).openConnection();
        connection.setConnectTimeout(BuildConfig.HTTP_CONNECT_TIMEOUT);
        connection.setReadTimeout(BuildConfig.HTTP_READ_TIMEOUT);
        /*
         * For best performance, you should call either setFixedLengthStreamingMode(int) when the
         * body length is known in advance, or setChunkedStreamingMode(int) when it is not.
         * Otherwise HttpURLConnection will be forced to buffer the complete request body in memory
         * before it is transmitted, wasting (and possibly exhausting) heap and increasing latency.
         */
        connection.setChunkedStreamingMode(4096); //4kb

        return connection;
    }


}
