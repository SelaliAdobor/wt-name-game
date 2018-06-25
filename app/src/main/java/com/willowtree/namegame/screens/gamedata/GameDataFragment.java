package com.willowtree.namegame.screens.gamedata;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.willowtree.namegame.R;

import androidx.navigation.fragment.NavHostFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class GameDataFragment extends Fragment {
    public static final int SUCCESS_DELAY_MS = 2000;
    Unbinder unbinder;

    @BindView(R.id.gamedata_loading_content)
    View loadingContent;

    @BindView(R.id.gamedata_failed_content)
    View failedContent;

    GameDataViewModel gameDataViewModel;

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        gameDataViewModel = ViewModelProviders
                .of(this)
                .get(GameDataViewModel.class);


        gameDataViewModel
                .getLoadingState()
                .observe(this, this::updateContentVisibility);
    }

    @Override
    public void onStart() {
        super.onStart();
        gameDataViewModel.startLoading(SUCCESS_DELAY_MS);
    }

    private void updateContentVisibility(GameDataViewModel.LoadingState loadingState) {
        switch (loadingState) {
            case LOADING:
                loadingContent.setVisibility(View.VISIBLE);
                failedContent.setVisibility(View.GONE);
                break;
            case FAILED:
                loadingContent.setVisibility(View.GONE);
                failedContent.setVisibility(View.VISIBLE);
                break;
            case SUCCESS:
                NavHostFragment.findNavController(this).navigate(R.id.action_goToMainMenuAfterGameDataLoad);
                break;
        }
    }


    @OnClick(R.id.gamedata_retry_button)
    public void onGameDataRetryButton() {
        gameDataViewModel.startLoading(SUCCESS_DELAY_MS);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.gamedata_fragment, container, false);
        unbinder = ButterKnife.bind(this, inflatedView);
        return inflatedView;
    }
}
