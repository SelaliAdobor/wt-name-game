package com.willowtree.namegame.application;

import com.willowtree.namegame.screens.gamedata.GameDataViewModel;
import com.willowtree.namegame.screens.mainmenu.MainMenuViewModel;
import com.willowtree.namegame.screens.namegame.NameGameViewModel;

import javax.inject.Singleton;

import dagger.Component;
import dagger.android.AndroidInjectionModule;

@Singleton
@Component(modules = {AndroidInjectionModule.class, ActivityBindingModule.class, ApplicationModule.class})
public interface ApplicationComponent {
    void inject(NameGameApplication nameGameApplication);

    //Would use ViewModelFactory to support automatic injection
    void inject(GameDataViewModel gameDataViewModel);

    void inject(MainMenuViewModel mainMenuViewModel);

    void inject(NameGameViewModel nameGameViewModel);
}
