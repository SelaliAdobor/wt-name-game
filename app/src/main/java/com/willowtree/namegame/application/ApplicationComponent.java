package com.willowtree.namegame.application;

import javax.inject.Singleton;

import dagger.Component;
import dagger.android.AndroidInjectionModule;

@Singleton
@Component(modules = {AndroidInjectionModule.class, ActivityBindingModule.class, ApplicationModule.class})
public interface ApplicationComponent {
    void inject(NameGameApplication nameGameApplication);
}
