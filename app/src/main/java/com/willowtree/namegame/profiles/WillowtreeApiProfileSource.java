package com.willowtree.namegame.profiles;

import com.willowtree.namegame.api.WillowtreeApiService;
import com.willowtree.namegame.api.profiles.Profile;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;


public class WillowtreeApiProfileSource implements ProfileSource {
    private WillowtreeApiService apiService;


    public WillowtreeApiProfileSource(WillowtreeApiService apiService) {
        this.apiService = apiService;
    }

    @Override
    public Single<List<Profile>> getProfiles() {
        return apiService.fetchAllProfiles()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterSuccess((profiles) -> Timber.i("Got %d profiles from API", profiles.size()))
                .doOnError(throwable -> Timber.e(throwable, "Failed to load profiles from API"));
    }
}
