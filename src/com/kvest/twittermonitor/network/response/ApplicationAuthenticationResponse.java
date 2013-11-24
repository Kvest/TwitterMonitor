package com.kvest.twittermonitor.network.response;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 24.11.13
 * Time: 21:31
 * To change this template use File | Settings | File Templates.
 */
public class ApplicationAuthenticationResponse {
    private String token_type;
    private String access_token;

    public String getTokenType() {
        return token_type;
    }

    public String getAccessToken() {
        return access_token;
    }
}
