package com.willowtree.namegame.screens.mainmenu;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.willowtree.namegame.application.NameGameApplication;
import com.willowtree.namegame.profiles.ProfileRepository;
import com.willowtree.namegame.profiles.ProfileSource;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import timber.log.Timber;

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

}
