package com.willowtree.namegame;

import com.google.gson.GsonBuilder;
import com.willowtree.namegame.api.WillowtreeApiService;

import org.junit.Test;

import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

import static java9.util.stream.StreamSupport.stream;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


public class APIIntegrationTest {
    /**
     * Scratchpad for testing required vs optional fields and field names
     */
    @Test
    public void parsingAndApiSetupCorrectly() throws Exception {

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message -> {
            Timber.d(message);
        });
        OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(loggingInterceptor).build();

        GsonConverterFactory gsonConverterFactory = GsonConverterFactory.create(
                new GsonBuilder()
                        .create());
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.WILLOWTREE_API_URL)
                .addConverterFactory(gsonConverterFactory)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build();

        retrofit.create(WillowtreeApiService.class).fetchAllProfiles()
                .subscribe((profiles, throwable) -> {
                    assertNull(throwable);
                    assertNotNull(profiles);
                    assertTrue(profiles.size() > 0);
                    assertTrue(stream(profiles).noneMatch(Objects::isNull));
                });
    }

}