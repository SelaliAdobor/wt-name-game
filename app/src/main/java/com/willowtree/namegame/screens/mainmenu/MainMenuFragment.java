package com.willowtree.namegame.screens.mainmenu;


import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.willowtree.namegame.R;
import com.willowtree.namegame.screens.namegame.NameGameFragment;

import java.util.List;

import androidx.navigation.fragment.NavHostFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import java9.util.Lists;
import java9.util.stream.Collectors;
import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

import static java9.util.stream.StreamSupport.stream;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainMenuFragment extends Fragment {
    @BindView(R.id.main_menu_konfettiView)
    KonfettiView konfettiView;

    MainMenuViewModel mainMenuViewModel;

    private Unbinder unbinder;

    public MainMenuFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mainMenuViewModel = ViewModelProviders
                .of(this)
                .get(MainMenuViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflatedView = inflater.inflate(R.layout.main_menu_fragment, container, false);
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

        if (!mainMenuViewModel.hasData()) {
            NavHostFragment.findNavController(this).navigate(R.id.action_goToGameDataLoadFromMainMenu);
        }
    }

    @SuppressLint("CheckResult")
    private void startGame() {

        //noinspection ResultOfMethodCallIgnored
        mainMenuViewModel.getGame()
                .subscribe(game -> {
                    Bundle bundle = new Bundle();

                    bundle.putParcelable(NameGameFragment.ARGUMENTS_GAME_KEY, game);
                    NavHostFragment.findNavController(this).navigate(R.id.action_mainMenuToMainGame, bundle);
                });
    }

    @OnClick(R.id.main_menu_play_button)
    void onMainMenuPlayButtonPressed() {
        startGame();
    }

    @OnClick(R.id.main_menu_confetti_button)
    void onConfettiButtonPressed() {
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
                .streamFor(50, 10000L);
    }
}
