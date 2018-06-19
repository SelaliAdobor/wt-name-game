package com.willowtree.namegame.api.profiles;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class Headshot extends RealmObject {
    @SerializedName("type")
    String type;

    @SerializedName("url")
    String url;

    public String getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }
}