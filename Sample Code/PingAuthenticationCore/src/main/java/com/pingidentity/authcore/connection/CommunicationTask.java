package com.pingidentity.authcore.connection;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pingidentity.authcore.beans.PingAuthenticationApiContract;
import com.pingidentity.authcore.beans.PingAuthenticationApiRequest;
import com.pingidentity.authcore.beans.PingAuthenticationApiResponse;
import com.pingidentity.authcore.beans.TokenExchangeRequest;
import com.pingidentity.authcore.models.AuthorizationResponse;
import com.pingidentity.authcore.models.ErrorState;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/*
 * See LICENSE.txt for the Ping Authentication licensing information.
 */
public class CommunicationTask implements Runnable {

    private final PingAuthenticationApiRequest request;
    private final CommunicationCallback communicationCallback;

    private static final CookieManager cookieManager = new CookieManager();

    CommunicationTask(PingAuthenticationApiRequest request, CommunicationCallback communicationCallback){
        this.request = request;
        this.communicationCallback = communicationCallback;
    }

    @Override
    public void run() {

        try {
            HttpsURLConnection connection = (HttpsURLConnection) ConnectionFactory.getConnection(request.getFlowId());

            for (String header : request.getHeaders().keySet()){
                connection.addRequestProperty(header, request.getHeaders().get(header));
            }
            if(cookieManager.getCookieStore().getCookies().size() > 0){
                connection.setRequestProperty(PingAuthenticationApiContract.COOKIE_HEADER,
                        TextUtils.join(";",  cookieManager.getCookieStore().getCookies()));
            }
            Log.d("Communication task", String.format("Request body: %s", request.toJsonString()));

            /*
             * Instances must be configured with setDoOutput(true) if they include a request body.
             * This also automatically sets request method to POST.
             */
            connection.setDoOutput(true);
            DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
            dataOutputStream.writeBytes(
                    (request instanceof TokenExchangeRequest?
                            encodeRequest(new Gson().fromJson(request.toJsonString(), JsonObject.class))
                            :
                            request.toJsonString()));
            dataOutputStream.flush();
            dataOutputStream.close();


            int responseCode = connection.getResponseCode();
            Log.d("Communication task", String.format("Received http response code %d", responseCode));

            switch (responseCode){
                case HttpURLConnection.HTTP_OK:
                    loadResponseCookies(connection);
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    /*
                     * StringBuilder objects are like String objects, except that they can be modified.
                     * Internally, these objects are treated like variable-length arrays that contain a
                     * sequence of characters.
                     */
                    StringBuilder response = new StringBuilder();
                    String inputLine;
                    while ((inputLine = bufferedReader.readLine())!=null){
                        response.append(inputLine);
                    }
                    Log.d("Communication Task", String.format("Received response: %s", response.toString()));
                    if(request instanceof TokenExchangeRequest){
                        communicationCallback.onSuccess(createAuthorizationObject(response));
                    }else {
                        communicationCallback.onSuccess(createResponseObject(response));
                    }
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                case HttpURLConnection.HTTP_UNAUTHORIZED:
                case HttpURLConnection.HTTP_BAD_REQUEST:
                    /*
                     * The client should not repeat this request without modification
                     */
                    InputStream inputStream = connection.getErrorStream();

                    BufferedReader errorBufferedReader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                    StringBuilder errorResponse = new StringBuilder();
                    String inputLine1;
                    while ((inputLine1 = errorBufferedReader.readLine())!=null){
                        errorResponse.append(inputLine1);
                    }
                    errorBufferedReader.close();
                    inputStream.close();
                    Log.d("CommunicationTask", String.format("Received error response: %s", errorResponse.toString()));

                    ErrorState errorState = new Gson().fromJson(errorResponse.toString(), ErrorState.class);
                    communicationCallback.onError(errorState);
                    break;
                default:
                    Log.w("Communication task", String.format("Received unexpected response code %d", responseCode));
            }
        } catch (IOException e) {
            communicationCallback.onException(e);
        }
    }

    private PingAuthenticationApiResponse createResponseObject(StringBuilder response) {
        return new Gson().fromJson(response.toString(), PingAuthenticationApiResponse.class);
    }

    private PingAuthenticationApiResponse createAuthorizationObject(StringBuilder response) {
        PingAuthenticationApiResponse pingAuthenticationApiResponse = new PingAuthenticationApiResponse();
        pingAuthenticationApiResponse.setStatus(PingAuthenticationApiContract.STATES.TOKEN_EXCHANGE_COMPLETED);
        pingAuthenticationApiResponse.setAuthorizationResponse(new Gson().fromJson(response.toString(), AuthorizationResponse.class));
        return pingAuthenticationApiResponse;
    }

    private void loadResponseCookies(HttpURLConnection connection){
        Map<String, List<String>> headerFields = connection.getHeaderFields();
        List<String> cookiesHeader = headerFields.get(PingAuthenticationApiContract.SET_COOKIE_HEADER);
        if (cookiesHeader != null) {
            for (String cookie : cookiesHeader) {
                cookieManager.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
            }
        }
    }

    /*
     * helper method to create HttpUrlEncoded String from Json Object
     */
    private String encodeRequest(JsonObject jsonObject) throws UnsupportedEncodingException {
        StringBuilder encodedResult = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, JsonElement> requestParameter : jsonObject.entrySet()){
            if(first){
                first = false;
            }else{
                encodedResult.append("&");
            }
            encodedResult.append(URLEncoder.encode(requestParameter.getKey(), StandardCharsets.UTF_8.name()));
            encodedResult.append("=");
            encodedResult.append(URLEncoder.encode(requestParameter.getValue().getAsString(), StandardCharsets.UTF_8.name()));
        }
        return encodedResult.toString();
    }
}
