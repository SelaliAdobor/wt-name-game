package com.willowtree.namegame.screens.namegame.models;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;
import com.willowtree.namegame.api.profiles.Profile;

import java.util.List;

import java9.util.stream.Collectors;

import static java9.util.stream.StreamSupport.stream;

@AutoValue
public abstract class Challenge implements Parcelable {
    public abstract String correctProfileId();

    public abstract List<String> profileIds();


    public static Challenge create(Profile correctProfile, List<Profile> profiles) {

        List<String> profileIds = stream(profiles)
                .map(Profile::getId)
                .collect(Collectors.toList());

        return new AutoValue_Challenge(correctProfile.getId(), profileIds);
    }
}
