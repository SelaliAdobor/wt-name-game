package com.willowtree.namegame.api.profiles;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

import io.realm.RealmObject;
import java9.util.Optional;


public class SocialLink extends RealmObject {
    @SerializedName("type")
    String type;

    @SerializedName("callToAction")
    String callToAction;

    @SerializedName("url")
    @Nullable
    String url;

    public String getType() {
        return type;
    }

    public String getCallToAction() {
        return callToAction;
    }

    @Nullable
    public Optional<String> getUrl() {
        return Optional.ofNullable(url);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SocialLink that = (SocialLink) o;
        return Objects.equals(type, that.type) &&
                Objects.equals(callToAction, that.callToAction) &&
                Objects.equals(url, that.url);
    }

    @Override
    public int hashCode() {

        return Objects.hash(type, callToAction, url);
    }

    @Override
    public String toString() {
        return "SocialLink{" +
                "type='" + type + '\'' +
                ", callToAction='" + callToAction + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}