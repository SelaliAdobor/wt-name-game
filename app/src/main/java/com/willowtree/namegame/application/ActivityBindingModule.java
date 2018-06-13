package com.willowtree.namegame.application;

import com.willowtree.namegame.MainActivity;
import com.willowtree.namegame.MainActivityModule;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBindingModule {

    @ContributesAndroidInjector(modules = MainActivityModule.class)
    abstract MainActivity mainActivity();
}
