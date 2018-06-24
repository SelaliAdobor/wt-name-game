package com.willowtree.namegame.screens.namegame;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.willowtree.namegame.api.profiles.Headshot;
import com.willowtree.namegame.application.NameGameApplication;
import com.willowtree.namegame.profiles.ProfileRepository;
import com.willowtree.namegame.screens.namegame.models.Answer;
import com.willowtree.namegame.screens.namegame.models.Challenge;
import com.willowtree.namegame.screens.namegame.models.Game;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.Completable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import java9.util.Optional;
import java9.util.stream.Collectors;
import java9.util.stream.StreamSupport;
import timber.log.Timber;

import javax.inject.Inject;

import static java9.util.stream.StreamSupport.stream;

public class NameGameViewModel extends AndroidViewModel {

    CompositeDisposable lifecycleDisposables = new CompositeDisposable();
    @Inject
    ProfileRepository profileRepository;

    MutableLiveData<Game> gameData = new MutableLiveData<>();
    MutableLiveData<State> stateData = new MutableLiveData<>();

    public NameGameViewModel(@NonNull Application application) {
        super(application);
        ((NameGameApplication) application).getApplicationComponent().inject(this);

        setupState();
    }

    private void setupState() {
        stateData.observeForever(state -> {
            Timber.i("State changed, State: %s Game: %s", state, getCurrentGame());
        });

        gameData.observeForever(game -> {
            Timber.i("Game changed, State: %s Game: %s", getCurrentState(), game);
            if (game.isFinished() && getCurrentState() == State.ANSWERING) {
                stateData.postValue(State.SCORING);
            }
        });
    }

    public void goToLoadState() {
        if(getCurrentState() != null){
            Timber.w("Not going to LOADING state due to existing state data");
            return;
        }
        stateData.setValue(State.LOADING);

        lifecycleDisposables.add(
                Completable.merge(getHeadshotCompletables(getCurrentGame()))
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> stateData.postValue(State.ANSWERING))
        );
    }

    private List<Completable> getHeadshotCompletables(Game game) {
        return stream(game.challenges())
                .map(Challenge::profileIds)
                .flatMap(StreamSupport::stream)
                .map(profileIds -> profileRepository.getById(profileIds))
                .map(profileSingle ->
                        profileSingle.flatMapCompletable(profile -> {
                            Optional<Headshot> headshot = profile.getHeadshot();
                            if (headshot.isPresent() && headshot.get().getSanitizedUrl().isPresent()) {
                                return Completable.create((emitter) ->
                                        Picasso.get()
                                                .load(headshot.get().getSanitizedUrl().get())
                                                .fetch(new Callback() {
                                                    @Override
                                                    public void onSuccess() {
                                                        emitter.onComplete();
                                                    }

                                                    @Override
                                                    public void onError(Exception e) {
                                                        emitter.onError(e);
                                                    }
                                                }));
                            } else {
                                return Completable.complete();
                            }
                        })
                ).collect(Collectors.toList());
    }

    public void addAnswer(Answer answer) {
        updateGame(Game.addAnswer(getCurrentGame(), answer));
    }

    private Game getCurrentGame() {

        Game currentGame = getGame().getValue();
        if (currentGame == null) {
            throw new IllegalStateException("Attempt to retrieve Game from GameViewModel before initialization");
        }
        return currentGame;
    }

    public LiveData<State> getState() {
        return stateData;
    }
    public State getCurrentState() {
        return stateData.getValue();
    }
    public void setInitalGameData(Game game) {
        if (gameData.getValue() == null) {
            gameData.setValue(game);
        }
    }

    public void updateGame(Game game) {
        gameData.postValue(game);
    }

    public LiveData<Game> getGame() {
        return gameData;
    }

    enum State {
        LOADING,
        ANSWERING,
        SCORING,
    }
}
