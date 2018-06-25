package com.willowtree.namegame.screens.mainmenu;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.willowtree.namegame.application.NameGameApplication;
import com.willowtree.namegame.profiles.ProfileRepository;
import com.willowtree.namegame.screens.namegame.models.Game;

import javax.inject.Inject;

import io.reactivex.Single;

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


    public Single<Game> getGame(int numberOfQuestions, int numberOfProfilesPerQuestion) {
        return profileRepository.getGame(numberOfQuestions, numberOfProfilesPerQuestion);
    }
}


