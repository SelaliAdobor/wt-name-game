package com.willowtree.namegame.api.profiles;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import io.realm.RealmObject;
import java9.util.Optional;


public class SocialLink extends RealmObject{
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
}