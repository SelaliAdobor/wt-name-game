package com.willowtree.namegame.profiles;

import com.willowtree.namegame.api.profiles.Profile;

import java.util.List;

import io.reactivex.Single;

public interface ProfileSource {
    Single<List<Profile>> getProfiles();
}
