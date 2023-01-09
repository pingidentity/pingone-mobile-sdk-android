package com.pingidentity.authcore.connection;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pingidentity.authcore.BuildConfig;

import java.io.IOException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * An example implementation of Connection that ignores certificates for https connections.
 * THIS SHOULD NOT BE USED IN PRODUCTION CODE.
 * It is intended to facilitate easier testing of Mobile Authentication Framework against
 * development servers only.
 *
 * See LICENSE.txt for the Ping Authentication licensing information.
 */
public class InsecureConnection implements Connection {
    private static final String ALLOWED_URI_CHARS = "@#&=*+-_.,:!?()/~'%";

    private static final HostnameVerifier ANY_HOSTNAME_VERIFIER = new HostnameVerifier() {
        @SuppressLint("BadHostnameVerifier")
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

    private static final TrustManager[] ANY_CERT_MANAGER = new TrustManager[] {
            new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @SuppressLint("TrustAllX509TrustManager")
                public void checkClientTrusted(X509Certificate[] certs, String authType) {}

                @SuppressLint("TrustAllX509TrustManager")
                public void checkServerTrusted(X509Certificate[] certs, String authType) {}
            }
    };
    @Nullable
    private static final SSLContext TRUSTING_CONTEXT;
    static {
        SSLContext context;
        try {
            context = SSLContext.getInstance("SSL");
        } catch (NoSuchAlgorithmException e) {
            Log.e("ConnBuilder", "Unable to acquire SSL context");
            context = null;
        }

        SSLContext initializedContext = null;
        if (context != null) {
            try {
                context.init(null, ANY_CERT_MANAGER, new java.security.SecureRandom());
                initializedContext = context;
            } catch (KeyManagementException e) {
                Log.e("Connection", "Failed to initialize trusting SSL context");
            }
        }

        TRUSTING_CONTEXT = initializedContext;
    }
    @NonNull
    @Override
    public HttpsURLConnection openConnection(@NonNull Uri uri) throws IOException {
        HttpsURLConnection connection = (HttpsURLConnection) new URL(Uri.encode(uri.toString(), ALLOWED_URI_CHARS)).openConnection();
        connection.setConnectTimeout(BuildConfig.HTTP_CONNECT_TIMEOUT);
        connection.setReadTimeout(BuildConfig.HTTP_READ_TIMEOUT);
        /*
         * For best performance, you should call either setFixedLengthStreamingMode(int) when the
         * body length is known in advance, or setChunkedStreamingMode(int) when it is not.
         * Otherwise HttpURLConnection will be forced to buffer the complete request body in memory
         * before it is transmitted, wasting (and possibly exhausting) heap and increasing latency.
         */
        connection.setChunkedStreamingMode(4096); //4kb

        connection.setSSLSocketFactory(TRUSTING_CONTEXT.getSocketFactory());
        connection.setHostnameVerifier(ANY_HOSTNAME_VERIFIER);
        return connection;
    }
}
