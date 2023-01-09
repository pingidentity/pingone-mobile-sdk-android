package com.pingidentity.authcore.connection;

import android.net.Uri;
import android.util.Log;

import com.pingidentity.authcore.BuildConfig;
import com.pingidentity.authcore.beans.PingAuthenticationApiContract;

import java.io.IOException;
import java.net.HttpURLConnection;

/*
 * See LICENSE.txt for the Ping Authentication licensing information.
 */
public class ConnectionFactory {

    public static HttpURLConnection getConnection(String flowId) throws IOException {
        Uri fullPathUrl = new ConnectionUtils().createFullPath(flowId);
        Log.d("Connection Factory", String.format("Preparing connection to %s", fullPathUrl));
        if (BuildConfig.USE_HTTPS){
            /*
             * In a typical SSL usage scenario, a server is configured with a certificate containing
             * a public key as well as a matching private key. As part of the handshake between an
             * SSL client and server, the server proves it has the private key by signing its
             * certificate with public-key cryptography.
             */
            return new SecureConnection().openConnection(fullPathUrl);
        }else {
            return new InsecureConnection().openConnection(fullPathUrl);
        }
    }

    private static class ConnectionUtils{

        Uri createFullPath(String flowId){
            Uri fullPathUrl = Uri.parse(BuildConfig.OIDC_ISSUER);
            /*
             * if flowID exists but it is empty prepare a token exchange connection
             */
            if(flowId!= null && flowId.equalsIgnoreCase("")){
                fullPathUrl = fullPathUrl.buildUpon()
                        .appendPath("as")
                        .appendPath("token.oauth2")
                        .build();
            }else if (flowId != null) {
                fullPathUrl = fullPathUrl.buildUpon()
                        .appendPath("pf-ws")
                        .appendPath("authn")
                        .appendPath("flows")
                        .appendPath(flowId)
                        .build();
            }else{
                /*
                 * if flowID doesn't exists run the initial request
                 */
                fullPathUrl = fullPathUrl.buildUpon()
                        .appendPath("as")
                        .appendPath("authorization.oauth2")
                        .appendQueryParameter(PingAuthenticationApiContract.PARAMS.SCOPE, BuildConfig.SCOPE)
                        .appendQueryParameter(PingAuthenticationApiContract.PARAMS.RESPONSE_TYPE, BuildConfig.RESPONSE_TYPE)
                        .appendQueryParameter(PingAuthenticationApiContract.PARAMS.RESPONSE_MODE, BuildConfig.RESPONSE_MODE)
                        .appendQueryParameter(PingAuthenticationApiContract.PARAMS.CLIENT_ID, BuildConfig.CLIENT_ID)
                        .build();
            }
            return fullPathUrl;
        }
    }
}
