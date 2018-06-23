package com.willowtree.namegame.api.profiles;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import java9.util.Optional;

public class Headshot extends RealmObject {
    @SerializedName("type")
    String type;

    @SerializedName("url")
    String url;

    public String getType() {
        return type;
    }

    public Optional<String> getSanitizedUrl() {
        if (url == null) {
            return Optional.empty();
        }
        if (url.startsWith("//")) {
            return Optional.of("http:" + url);
        }

        return Optional.of(url);
    }
}