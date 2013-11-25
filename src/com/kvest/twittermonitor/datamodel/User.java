package com.kvest.twittermonitor.datamodel;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 24.11.13
 * Time: 23:14
 * To change this template use File | Settings | File Templates.
 */
public class User {
    private String name;
    private boolean default_profile_image;
    private String profile_image_url;

    public String getName() {
        return name;
    }

    public String getProfileImageUrl() {
        return default_profile_image ? "" : profile_image_url;
    }

    @Override
    public String toString() {
        return name + " " + profile_image_url + "(" + default_profile_image + ")";
    }
}
