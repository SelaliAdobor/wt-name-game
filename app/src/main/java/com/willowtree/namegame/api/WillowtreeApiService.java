package com.willowtree.namegame.api;

import com.willowtree.namegame.api.profiles.Profile;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.GET;


public interface WillowtreeApiService {
    @GET("api/v1.0/profiles")
    Single<List<Profile>> fetchAllProfiles();
}
