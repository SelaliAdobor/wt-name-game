package com.willowtree.namegame.screens.namegame.models;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;
import com.willowtree.namegame.api.profiles.Profile;

@AutoValue
public abstract class Answer implements Parcelable {

    public boolean wasCorrect() {
        return guessedProfileId().equals(challenge().correctProfileId());
    }

    public abstract Challenge challenge();

    public abstract String guessedProfileId();

    public static Answer create(Challenge challenge, Profile guessedProfile) {
        return new AutoValue_Answer(challenge, guessedProfile.getId());
    }
}
