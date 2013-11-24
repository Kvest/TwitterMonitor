package com.kvest.twittermonitor.datamodel;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 24.11.13
 * Time: 23:07
 * To change this template use File | Settings | File Templates.
 */
public class Tweet {
    private long id;
    private User user;
    private String created_at;
    private String text;

    @Override
    public String toString() {
        return "[" + id + "][" + created_at + "]" + "[" + user.toString() + "]"  + text;
    }
}
