package com.willowtree.namegame.screens.namegame;


import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.litho.Component;
import com.facebook.litho.ComponentContext;
import com.facebook.litho.LithoView;
import com.facebook.litho.widget.ComponentRenderInfo;
import com.facebook.litho.widget.GridLayoutInfo;
import com.facebook.litho.widget.Recycler;
import com.facebook.litho.widget.RecyclerBinder;
import com.facebook.litho.widget.RenderInfo;
import com.github.anastr.speedviewlib.SpeedView;
import com.willowtree.namegame.R;
import com.willowtree.namegame.api.profiles.Profile;
import com.willowtree.namegame.screens.namegame.models.Answer;
import com.willowtree.namegame.screens.namegame.models.Challenge;
import com.willowtree.namegame.screens.namegame.models.Game;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import java9.util.Lists;
import java9.util.stream.Collectors;

import androidx.navigation.fragment.NavHostFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;
import timber.log.Timber;

import static java9.util.stream.StreamSupport.stream;

public class NameGameFragment extends Fragment {
    Unbinder unbinder;


    public static final String ARGUMENTS_GAME_KEY = "game_key";
    private NameGameViewModel nameGameViewModel;

    @BindView(R.id.namegame_answering_content)
    View answeringContent;

    @BindView(R.id.namegame_loading_content)
    View loadingContent;

    @BindView(R.id.namegame_scoring_content)
    View scoringContent;

    @BindView(R.id.namegame_scoring_speedView)
    SpeedView scoreView;

    @BindView(R.id.namegame_answering_lithoView)
    LithoView lithoView;

    @BindView(R.id.namegame_scoring_label)
    TextView scoreLabel;

    @BindView(R.id.namegame_scoring_konfettiView)
    KonfettiView konfettiView;

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    public NameGameFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.name_game_fragment, container, false);
        unbinder = ButterKnife.bind(this, inflatedView);
        return inflatedView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void renderScoring(Game game) {
        int totalAnswers = game.answers().size();
        int correctAnswers = (int) stream(game.answers())
                .filter(Answer::wasCorrect)
                .count();

        scoreView.setMinSpeed(0);
        scoreView.setMaxSpeed(totalAnswers);

        scoreView.speedTo(correctAnswers);

        boolean allAnswersCorrect = correctAnswers == totalAnswers;

        String scoreLabel = allAnswersCorrect ?
                getResources().getString(R.string.namegame_scoring_guage_label_allCorrect) :
                getResources().getQuantityString(R.plurals.namegame_scoring_guage_label, correctAnswers, correctAnswers, totalAnswers);

        this.scoreLabel.setText(scoreLabel);

        if (allAnswersCorrect) {
            startConfetti();
        }
    }

    private void startConfetti() {
        List<Integer> confettiColors = stream(Lists.of(R.color.confetti_yellow, R.color.confetti_orange, R.color.confetti_purple, R.color.confetti_pink))
                .map(colorId -> getResources().getColor(colorId))
                .collect(Collectors.toList());

        konfettiView.build()
                .addColors(confettiColors)
                .setDirection(0.0, 359.0)
                .setSpeed(1f, 5f)
                .setFadeOutEnabled(true)
                .setTimeToLive(2000L)
                .addShapes(Shape.RECT, Shape.CIRCLE)
                .addSizes(new Size(12, 5))
                .setPosition(konfettiView.getX() + konfettiView.getWidth() / 2, konfettiView.getY() + konfettiView.getHeight() / 3)
                .streamFor(300, 10000L);
    }

    private void renderGame(Game game) {
        switch (nameGameViewModel.getCurrentState()) {
            case LOADING:
                loadingContent.setVisibility(View.VISIBLE);
                answeringContent.setVisibility(View.GONE);
                scoringContent.setVisibility(View.GONE);
                renderLoading();
                break;
            case ANSWERING:
                loadingContent.setVisibility(View.GONE);
                answeringContent.setVisibility(View.VISIBLE);
                scoringContent.setVisibility(View.GONE);
                renderAnswering(game);
                break;
            case SCORING:
                loadingContent.setVisibility(View.GONE);
                answeringContent.setVisibility(View.GONE);
                scoringContent.setVisibility(View.VISIBLE);
                renderScoring(game);
                break;
        }
    }

    private void renderAnswering(Game game) {
        game.firstUnansweredChallenge()
                .ifPresent(challenge -> {
                    Timber.i("Current challenge: %s", challenge);


                    ComponentContext componentContext = new ComponentContext(getActivity());

                    RecyclerBinder recyclerBinder = new RecyclerBinder.Builder()
                            .layoutInfo(new GridLayoutInfo(getActivity(), 3))
                            .build(componentContext);

                    Recycler recyclerComponent = Recycler.create(componentContext)
                            .binder(recyclerBinder)
                            .build();

                    lithoView.setComponent(recyclerComponent);


                    updateRecyclerContent(recyclerBinder, componentContext, challenge);
                });
    }

    private void updateRecyclerContent(RecyclerBinder recyclerBinder, ComponentContext componentContext, Challenge challenge) {
        List<RenderInfo> listItems = new ArrayList<>();
        compositeDisposable.add(
                Single.merge(stream(challenge.profileIds())
                        .map(profileId -> nameGameViewModel.profileRepository.getById(profileId))
                        .collect(Collectors.toList()))
                        .collectInto(new ArrayList<Profile>(), ArrayList::add)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe((profiles) -> {
                            for (Profile profile : profiles) {

                                Component layoutComponent = ProfileLayout
                                        .create(componentContext)
                                        .profile(profile)
                                        .clickEventHandler(() -> handleAnswer(profile, challenge))
                                        .build();

                                listItems.add(ComponentRenderInfo
                                        .create()
                                        .component(layoutComponent)
                                        .build()
                                );
                            }

                            recyclerBinder.removeRangeAt(0, recyclerBinder.getItemCount());
                            recyclerBinder.insertRangeAt(0, listItems);
                        })
        );
    }

    private void handleAnswer(Profile profileSelected, Challenge challenge) {
        if (challenge.correctProfileId().equals(profileSelected.getId())) {
            nameGameViewModel.addAnswer(Answer.create(challenge, profileSelected));
        }
    }

    private void renderLoading() {

    }

    @OnClick(R.id.namegame_scoring_returnToMainMenuButton)
    public void onClickReturnToMainMenu() {
        NavHostFragment
                .findNavController(this)
                .navigate(R.id.action_mainGameToMainMenu);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        nameGameViewModel = ViewModelProviders
                .of(this)
                .get(NameGameViewModel.class);

        Bundle arguments = getArguments();

        if (arguments == null) {
            throw new IllegalStateException("Started NameGameFragment without arguments");
        }

        Game game = arguments.getParcelable(ARGUMENTS_GAME_KEY);

        //Update the screen when either the Game or State are changed
        nameGameViewModel.getGame().observe(this, this::renderGame);
        nameGameViewModel.getState().observe(this, state -> this.renderGame(nameGameViewModel.getGame().getValue()));

        nameGameViewModel.setInitalGameData(game);

        nameGameViewModel.goToLoadState();
    }
}
