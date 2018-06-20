package com.willowtree.namegame.profiles;

import com.willowtree.namegame.api.profiles.Profile;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;


public interface ProfileRepository {
    Completable setProfiles(List<Profile> profiles);

    Single<Boolean> hasProfiles();
}
