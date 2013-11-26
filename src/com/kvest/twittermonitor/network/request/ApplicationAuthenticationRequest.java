package com.kvest.twittermonitor.network.request;

import android.util.Base64;
import com.android.volley.*;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.kvest.twittermonitor.network.Urls;
import com.kvest.twittermonitor.network.response.ApplicationAuthenticationResponse;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 24.11.13
 * Time: 20:40
 * To change this template use File | Settings | File Templates.
 */
public class ApplicationAuthenticationRequest extends Request<ApplicationAuthenticationResponse> {
    /** Content type for request. */
    private static final String PROTOCOL_CONTENT_TYPE = "application/x-www-form-urlencoded;charset=UTF-8";
    private static final String REQUEST_BODY = "grant_type=client_credentials";
    private static final String PROTOCOL_CHARSET = "utf-8";
    private static final String AUTHORIZATION_HEADER = "Authorization";

    private Response.Listener<ApplicationAuthenticationResponse> listener;
    private Map<String, String> headers;

    public ApplicationAuthenticationRequest(String consumerKey, String consumerSecret,
                                            Response.Listener<ApplicationAuthenticationResponse> listener, Response.ErrorListener errorListener) {
        super(Method.POST, Urls.TWITTER_TOKEN_URL, errorListener);
        this.listener = listener;

        //create headers with authorization
        headers = new HashMap<String, String>(1);
        try {
            headers.put(AUTHORIZATION_HEADER, createAuthorizationHeader(consumerKey, consumerSecret));
        } catch (UnsupportedEncodingException uee) {
            uee.printStackTrace();
        }
    }

    @Override
    public Map<String, String> getHeaders() {
        return headers;
    }

    private String createAuthorizationHeader(String consumerKey, String consumerSecret) throws UnsupportedEncodingException {
        // Concatenate the encoded consumer key, a colon character, and the
        // encoded consumer secret
        String combined = URLEncoder.encode(consumerKey, "UTF-8") + ":" + URLEncoder.encode(consumerSecret, "UTF-8");

        // Base64 encode the string
        return "Basic " + Base64.encodeToString(combined.getBytes(), Base64.NO_WRAP);
    }

    @Override
    protected void deliverResponse(ApplicationAuthenticationResponse response) {
        listener.onResponse(response);
    }

    @Override
    protected Response<ApplicationAuthenticationResponse> parseNetworkResponse(NetworkResponse response) {
        try {
            //get string response
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            //Parse string response with Gson
            Gson gson = new Gson();
            return Response.success(gson.fromJson(json, ApplicationAuthenticationResponse.class),
                                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    public String getPostBodyContentType() {
        return getBodyContentType();
    }

    @Override
    public byte[] getPostBody() {
        return getBody();
    }

    @Override
    public String getBodyContentType() {
        return PROTOCOL_CONTENT_TYPE;
    }

    @Override
    public byte[] getBody() {
        try {
            return REQUEST_BODY.getBytes(PROTOCOL_CHARSET);
        } catch (UnsupportedEncodingException uee) {
            return null;
        }
    }
}
