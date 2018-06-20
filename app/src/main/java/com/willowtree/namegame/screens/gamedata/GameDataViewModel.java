package com.willowtree.namegame.screens.gamedata;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.willowtree.namegame.api.profiles.Profile;
import com.willowtree.namegame.application.NameGameApplication;
import com.willowtree.namegame.profiles.ProfileRepository;
import com.willowtree.namegame.profiles.ProfileSource;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;

import io.realm.Realm;
import timber.log.Timber;

public class GameDataViewModel extends AndroidViewModel {
    @Inject
    ProfileSource profileSource;

    @Inject
    ProfileRepository profileRepository;

    enum LoadingState {
        LOADING,
        FAILED,
        SUCCESS
    }

    private MutableLiveData<LoadingState> loadingState = new MutableLiveData<>();

    public MutableLiveData<Throwable> getLoadingError() {
        return loadingError;
    }

    private MutableLiveData<Throwable> loadingError = new MutableLiveData<>();
    private MutableLiveData<Boolean> hasData = new MutableLiveData<>();

    public MutableLiveData<LoadingState> getLoadingState() {
        return loadingState;
    }

    public GameDataViewModel(@NonNull Application application) {
        super(application);
        ((NameGameApplication) application).getApplicationComponent().inject(this);
        loadingState.observeForever(state -> Timber.d("Changed GameData state %s", state.toString()));
    }

    public boolean hasData() {
        //Works on assumption count is a quick operation
        return profileRepository
                .hasProfiles()
                .blockingGet();
    }

    public void startLoading(int minimumDelayMs) {
        //Logic would be contained in Presenter for traditional app

        loadingState.postValue(LoadingState.LOADING);
        profileSource.getProfiles()
                .delaySubscription(minimumDelayMs, TimeUnit.MILLISECONDS)
                .subscribe(
                        profiles ->
                                profileRepository.setProfiles(profiles)
                                        .subscribe(() -> loadingState.postValue(LoadingState.SUCCESS)),
                        throwable -> {
                            loadingState.postValue(LoadingState.FAILED);
                            loadingError.postValue(throwable);
                        });
    }
}
