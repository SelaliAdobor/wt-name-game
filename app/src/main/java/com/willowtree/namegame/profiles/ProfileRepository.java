package com.willowtree.namegame.profiles;

import com.willowtree.namegame.api.profiles.Profile;
import com.willowtree.namegame.screens.namegame.models.Challenge;
import com.willowtree.namegame.screens.namegame.models.Game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.reactivex.Completable;
import io.reactivex.Single;


public interface ProfileRepository {

    String DEFAULT_HEADHSOT_IMAGE = "//images.ctfassets.net/3cttzl4i3k1h/5ZUiD3uOByWWuaSQsayAQ6/c630e7f851d5adb1876c118dc4811aed/featured-image-TEST1.png";

    Completable setProfiles(List<Profile> profiles);

    Single<Boolean> hasProfiles();

    Single<Profile> getById(String profileId);

    Single<List<Profile>> getRandomProfiles(int count, boolean needHeadshots, String... filteredHeadshotUrls);

    default Single<Challenge> getChallenge(int profileCount) {
        return getRandomProfiles(profileCount, true, DEFAULT_HEADHSOT_IMAGE)
                .map((profiles) -> {
                    int correctProfileIndex = new Random()
                            .nextInt(profiles.size());
                    Profile correctProfile = profiles.get(correctProfileIndex);

                    return Challenge.create(correctProfile, profiles);
                });
    }

    default Single<Game> getGame(int challengeCount, int profilesPerChallenge) {
        return getChallenge(profilesPerChallenge)
                .repeat(challengeCount)
                .collectInto(new ArrayList<Challenge>(), ArrayList::add)
                .map(Game::createGame);
    }
}
