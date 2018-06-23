package com.willowtree.namegame.screens.mainmenu;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.willowtree.namegame.api.profiles.Profile;
import com.willowtree.namegame.application.NameGameApplication;
import com.willowtree.namegame.profiles.ProfileRepository;
import com.willowtree.namegame.profiles.ProfileSource;
import com.willowtree.namegame.screens.namegame.models.Challenge;
import com.willowtree.namegame.screens.namegame.models.Game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Single;
import java9.util.Lists;
import java9.util.stream.Collectors;
import timber.log.Timber;

import static java9.util.stream.StreamSupport.stream;

public class MainMenuViewModel extends AndroidViewModel {
    @Inject
    ProfileRepository profileRepository;

    public MainMenuViewModel(@NonNull Application application) {
        super(application);
        ((NameGameApplication) application).getApplicationComponent().inject(this);
    }

    public boolean hasData() {
        //Works on assumption count is a quick operation
        return profileRepository
                .hasProfiles()
                .blockingGet();
    }


    public Single<Game> getGame(int numberOfQuestions) {
        return profileRepository.getGame(numberOfQuestions);
    }
}


