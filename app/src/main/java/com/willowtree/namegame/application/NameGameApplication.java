package com.willowtree.namegame.application;

import android.app.Activity;
import android.app.Application;

import com.facebook.soloader.SoLoader;
import com.willowtree.namegame.BuildConfig;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import timber.log.Timber;


public class NameGameApplication extends Application implements HasActivityInjector {
    @Inject
    DispatchingAndroidInjector<Activity> dispatchingActivityInjector;
    private ApplicationComponent applicationComponent;

    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }

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

        applicationComponent = DaggerApplicationComponent.create();
        applicationComponent
                .inject(this);
    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return dispatchingActivityInjector;
    }
}