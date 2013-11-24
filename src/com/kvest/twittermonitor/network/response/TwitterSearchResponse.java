package com.kvest.twittermonitor.network.response;

import com.kvest.twittermonitor.datamodel.Tweet;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 24.11.13
 * Time: 21:54
 * To change this template use File | Settings | File Templates.
 */
public class TwitterSearchResponse {
    private List<Tweet> statuses;

    public List<Tweet> getStatuses() {
        return statuses;
    }
}
