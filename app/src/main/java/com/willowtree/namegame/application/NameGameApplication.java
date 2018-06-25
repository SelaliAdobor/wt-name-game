package com.willowtree.namegame.application;

import android.app.Activity;
import android.app.Application;

import com.facebook.soloader.SoLoader;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;
import com.willowtree.namegame.BuildConfig;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import io.realm.Realm;
import timber.log.Timber;


public class NameGameApplication extends Application implements HasActivityInjector {
    @Inject
    DispatchingAndroidInjector<Activity> dispatchingActivityInjector;
    private ApplicationComponent applicationComponent;

    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }

    public static final int PICASSO_CACHE_SIZE_BYTES = 100000000; //Store up to 100MB of images

    @Override
    public void onCreate() {
        super.onCreate();

        SoLoader.init(this, false);
        Realm.init(this);
        if (BuildConfig.DEBUG) {
            Realm.getDefaultInstance().executeTransaction(realm -> {
                realm.deleteAll();
            });

            Timber.plant(new Timber.DebugTree());
        }


        Picasso picasso = new Picasso.Builder(this)
                .downloader(new OkHttp3Downloader(getCacheDir(), PICASSO_CACHE_SIZE_BYTES)).build();
        Picasso.setSingletonInstance(picasso);

        applicationComponent = DaggerApplicationComponent.create();
        applicationComponent
                .inject(this);
    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return dispatchingActivityInjector;
    }
}