package com.willowtree.namegame.screens.namegame.models;

import com.google.auto.value.AutoValue;
import com.willowtree.namegame.api.profiles.Profile;

@AutoValue
public abstract class Answer {

    public boolean wasCorrect() {
        return guessedProfile().getId().equals(challenge().correctProfileId());
    }

    public abstract Challenge challenge();

    public abstract Profile guessedProfile();

    public static Answer create(Challenge challenge, Profile guessedProfile) {
        return new AutoValue_Answer(challenge, guessedProfile);
    }
}
