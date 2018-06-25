package com.willowtree.namegame.api.profiles;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

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

    @Override
    public String toString() {
        return "Headshot{" +
                "type='" + type + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Headshot headshot = (Headshot) o;
        return Objects.equals(type, headshot.type) &&
                Objects.equals(url, headshot.url);
    }

    @Override
    public int hashCode() {

        return Objects.hash(type, url);
    }
}