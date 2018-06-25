package com.willowtree.namegame.screens.gamedata;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.willowtree.namegame.application.NameGameApplication;
import com.willowtree.namegame.profiles.ProfileRepository;
import com.willowtree.namegame.profiles.ProfileSource;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;
import timber.log.Timber;

public class GameDataViewModel extends AndroidViewModel {

    private Disposable loadingDisposable;


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


    public void startLoading(int minimumDelayMs, boolean forcedLoading) {
        if (loadingDisposable != null) {
            loadingDisposable.isDisposed();
            if(!forcedLoading){
                //Only loads once per view model instance to allow leaving app and coming back without restarting load
                return;
            }
        }

        //Minimum delay prevents confusing flash on screen
        //Logic would be contained in Presenter for traditional app
        //WouldDo: Revisit logic, suspect delaySubscription should be zipWith so that network request starts immediately
        loadingState.postValue(LoadingState.LOADING);
        loadingDisposable = profileSource.getProfiles()
                .delaySubscription(minimumDelayMs, TimeUnit.MILLISECONDS)
                .flatMapCompletable(profiles -> profileRepository.setProfiles(profiles))
                .subscribe(
                        () -> loadingState.postValue(LoadingState.SUCCESS),
                        throwable -> {
                            loadingState.postValue(LoadingState.FAILED);
                            loadingError.postValue(throwable);
                        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        loadingDisposable.dispose();
    }
}
