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

import androidx.navigation.fragment.NavHostFragment;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainMenuFragment extends Fragment {

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
}
