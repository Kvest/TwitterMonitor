package com.kvest.twittermonitor.network.request;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.google.gson.Gson;
import com.kvest.twittermonitor.datamodel.Tweet;
import com.kvest.twittermonitor.network.Urls;
import com.kvest.twittermonitor.network.response.TwitterSearchResponse;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 24.11.13
 * Time: 21:53
 * To change this template use File | Settings | File Templates.
 */
public class TwitterSearchRequest extends JsonRequest<TwitterSearchResponse> {
    private static final int ARTIFICIAL_DELAY = 1000;
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String AUTHORIZATION_PREFIX = "Bearer ";

    private static Gson gson = new Gson();
    private Map<String, String> headers;
    private TweetsResultProcessor tweetsResultProcessor;

    public TwitterSearchRequest(String access_token, SearchParams params,
                                TweetsResultProcessor tweetsResultProcessor,
                                Response.Listener<TwitterSearchResponse> listener,
                                Response.ErrorListener errorListener) {
        super(Method.GET, Urls.TWITTER_SEARCH_URL + "?" + params.toString(), null, listener, errorListener);

        this.tweetsResultProcessor = tweetsResultProcessor;

        //create headers with authorization
        headers = new HashMap<String, String>(1);
        headers.put(AUTHORIZATION_HEADER, AUTHORIZATION_PREFIX + access_token);
    }

    @Override
    public Map<String, String> getHeaders() {
        return headers;
    }

    @Override
    protected Response<TwitterSearchResponse> parseNetworkResponse(NetworkResponse response) {
        //1 second delay
        doArtificialDelay();

        try {
            //get string response
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            //Parse string response with Gson
            TwitterSearchResponse twitterSearchResponse = gson.fromJson(json, TwitterSearchResponse.class);

            //process tweets
            if (twitterSearchResponse != null && tweetsResultProcessor != null) {
                tweetsResultProcessor.processTweets(twitterSearchResponse.getStatuses());
            }

            return Response.success(twitterSearchResponse, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }

    private void doArtificialDelay() {
        try {
            Thread.sleep(ARTIFICIAL_DELAY);
        } catch (InterruptedException ie) {};
    }

    //Class - wrapper for search request params
    public static class SearchParams {
        private static final String DEFAULT_RESULT_TYPE = "mixed";
        private static final int DEFAULT_COUNT = 100;
        private static final long DEFAULT_ID = -1;

        private String q;
        private String resultType;
        private int count;
        private long sinceId;
        private long maxId;

        public SearchParams(String q) {
            this.q = q;
            resultType = DEFAULT_RESULT_TYPE;
            count = DEFAULT_COUNT;
            sinceId = DEFAULT_ID;
            maxId = DEFAULT_ID;
        }

        public void setQ(String q) {
            this.q = q;
        }

        public void setResultType(String resultType) {
            this.resultType = resultType;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public void setSinceId(long sinceId) {
            this.sinceId = sinceId;
        }

        public void setMaxId(long maxId) {
            this.maxId = maxId;
        }

        @Override
        public String toString() {
            String result = Urls.TWITTER_SEARCH_PARAM_Q + "=" + q + "&" +
                            Urls.TWITTER_SEARCH_PARAM_RESULT_TYPE + "=" + resultType + "&" +
                            Urls.TWITTER_SEARCH_PARAM_COUNT + "=" + Integer.toString(count);
            if (sinceId != DEFAULT_ID) {
                result += "&" + Urls.TWITTER_SEARCH_PARAM_SINCE_ID + "=" + Long.toString(sinceId);
            }
            if (maxId != DEFAULT_ID) {
                result += "&" + Urls.TWITTER_SEARCH_PARAM_MAX_ID + "=" + Long.toString(maxId);
            }

            return result;
        }

    }

    public interface TweetsResultProcessor {
        public void processTweets(List<Tweet> tweets);
    }
}
