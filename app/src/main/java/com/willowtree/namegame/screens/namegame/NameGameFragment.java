package com.willowtree.namegame.screens.namegame;


import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.anastr.speedviewlib.SpeedView;
import com.willowtree.namegame.R;
import com.willowtree.namegame.screens.gamedata.GameDataViewModel;
import com.willowtree.namegame.screens.namegame.models.Answer;
import com.willowtree.namegame.screens.namegame.models.Game;

import java.util.Locale;

import androidx.navigation.fragment.NavHostFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
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

    public NameGameFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View inflatedView = inflater.inflate(R.layout.name_game_fragment, container, false);
        unbinder = ButterKnife.bind(this, inflatedView);
        return inflatedView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void renderScoring(Game game) {
        int totalAnswers = game.answers().size();
        long correctAnswers = stream(game.answers())
                .filter(Answer::wasCorrect)
                .count();

        scoreView.setMinSpeed(0);
        scoreView.setMaxSpeed(totalAnswers);

        scoreView.speedTo(correctAnswers);

        String unitText = String.format(Locale.US, "answers out of %d", correctAnswers);
        scoreView.setUnit(unitText);
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
                loadingContent.setVisibility(View.VISIBLE);
                answeringContent.setVisibility(View.GONE);
                scoringContent.setVisibility(View.VISIBLE);
                renderScoring(game);
                break;
        }
    }

    private void renderAnswering(Game game) {
        game.firstUnansweredChallenge().ifPresent(challenge -> {
            Timber.i("Current challenge: %s", challenge);
        });
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

        nameGameViewModel.getGame().observe(this, this::renderGame);
        nameGameViewModel.getState().observe(this, state -> this.renderGame(nameGameViewModel.getGame().getValue()));
        nameGameViewModel.setInitalGameData(game);
        nameGameViewModel.loadProfileHeadshots();
    }
}
