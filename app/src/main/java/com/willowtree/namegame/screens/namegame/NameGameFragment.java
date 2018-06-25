package com.willowtree.namegame.screens.namegame;


import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
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
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.SimpleShowcaseEventListener;
import com.github.amlcurran.showcaseview.targets.ActionViewTarget;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.github.anastr.speedviewlib.SpeedView;
import com.github.anastr.speedviewlib.Speedometer;
import com.github.anastr.speedviewlib.TubeSpeedometer;
import com.willowtree.namegame.R;
import com.willowtree.namegame.api.profiles.Profile;
import com.willowtree.namegame.screens.namegame.models.Answer;
import com.willowtree.namegame.screens.namegame.models.Challenge;
import com.willowtree.namegame.screens.namegame.models.Game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Completable;
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
    Speedometer scoreView;

    @BindView(R.id.namegame_answering_lithoView)
    LithoView lithoView;

    @BindView(R.id.namegame_scoring_label)
    TextView scoreLabel;
    @BindView(R.id.namegame_scoring_title)
    TextView scoreTitle;

    @BindView(R.id.namegame_answering_correctNameTextView)
    TextView nameLabel;

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
        compositeDisposable.clear();
    }

    private void renderScoring(Game game) {
        int totalAnswers = game.answers().size();
        int correctAnswers = (int) stream(game.answers())
                .filter(Answer::wasCorrect)
                .count();

        scoreView.setMinSpeed(0);
        scoreView.setMaxSpeed(totalAnswers);

        scoreView.speedTo(correctAnswers);

        updateScoreText(totalAnswers, correctAnswers);

        if (correctAnswers == totalAnswers) {
            startConfetti(); //Very important!!
        }
    }

    private void updateScoreText(int totalAnswers, int correctAnswers) {
        boolean allAnswersCorrect = correctAnswers == totalAnswers;

        String scoreLabelText;

        if (allAnswersCorrect) {
            scoreLabelText = getResources().getString(R.string.namegame_scoring_guage_label_allCorrect);
        } else if (correctAnswers == 0) {
            scoreLabelText = getResources().getString(R.string.namegame_scoring_guage_none_label);
        } else {
            scoreLabelText = getResources().getQuantityString(R.plurals.namegame_scoring_guage_label, correctAnswers, correctAnswers, totalAnswers);
        }

        scoreLabel.setText(scoreLabelText);

        float percentScore = (float) correctAnswers / totalAnswers;

        @StringRes int scoreTitleResId;
        if (allAnswersCorrect) {
            scoreTitleResId = R.string.namegame_scoring_all_title;
        } else if (percentScore > 0.4) {
            scoreTitleResId = R.string.namegame_scoring_some_title;
        } else {
            scoreTitleResId = R.string.namegame_scoring_few_title;
        }

        scoreTitle.setText(scoreTitleResId);
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

    RecyclerBinder recyclerBinder;
    Recycler recyclerComponent;
    ComponentContext componentContext;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        componentContext = new ComponentContext(getActivity());

        recyclerBinder = new RecyclerBinder.Builder()
                .layoutInfo(new GridLayoutInfo(getActivity(), 3))
                .build(componentContext);

        recyclerComponent = Recycler.create(componentContext)
                .binder(recyclerBinder)
                .build();


        lithoView.setComponent(recyclerComponent);
    }

    private void renderAnswering(Game game) {
        game.firstUnansweredChallenge()
                .ifPresent(challenge -> {
                    Timber.i("Current challenge: %s", challenge);


                    updateRecyclerContent(challenge);

                    compositeDisposable.add(
                            nameGameViewModel.profileRepository
                                    .getById(challenge.correctProfileId())
                                    .map(Profile::getFullName)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(correctName -> nameLabel.setText(correctName))
                    );

                });
    }

    private void updateRecyclerContent(Challenge challenge) {
        List<RenderInfo> listItems = new ArrayList<>();
        compositeDisposable.add(
                Single.merge( //Create an Flowable that returns all the profiles that were found
                        stream(challenge.profileIds())
                                .map(profileId -> nameGameViewModel.profileRepository.getById(profileId))
                                .collect(Collectors.toList())
                )
                        .collectInto(new ArrayList<Profile>(), ArrayList::add)//Collect all results of that observable into an array
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe((profiles) -> {

                            AtomicBoolean answerSelected = new AtomicBoolean(false);

                            for (Profile profile : profiles) {
                                Component layoutComponent = ProfileLayout
                                        .create(componentContext)
                                        .profile(profile)
                                        .clickEventHandler(() -> {
                                            //We only want to be able to select an answer once per challenge
                                            //Because the AtomicBoolean is scoped outside of this loop, it acts as shared state between all the buttons shown
                                            if (!answerSelected.getAndSet(true)) {
                                                handleAnswer(profiles, Answer.create(challenge, profile));
                                            }
                                        })
                                        .build();

                                listItems.add(ComponentRenderInfo
                                        .create()
                                        .component(layoutComponent)
                                        .build()
                                );

                                if (profile.getId().equals(challenge.correctProfileId())) { //Won't run in release build due to Timber config
                                    Timber.d("If you're playing, look away... the correct name is %d items down from the top", listItems.size());
                                }
                            }

                            recyclerBinder.removeRangeAt(0, recyclerBinder.getItemCount());
                            recyclerBinder.insertRangeAt(0, listItems);
                        })
        );
    }

    private void handleAnswer(List<Profile> profiles, Answer answer) {
        compositeDisposable.add(
                nameGameViewModel.profileRepository
                        .getById(answer.challenge().correctProfileId())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(correctProfile -> {
                            recyclerBinder.scrollToPosition(profiles.indexOf(correctProfile), false);

                            //Post this since the RecyclerView update will be in the queue
                            new Handler(Looper.getMainLooper())
                                    .post(() ->
                                            compositeDisposable.add(
                                                    highlightAnswer(profiles, answer, correctProfile)
                                                            .subscribe(() -> nameGameViewModel.addAnswer(answer))
                                            )
                                    );

                        })
        );
    }

    private Completable highlightAnswer(List<Profile> profiles, Answer answer, Profile correctProfile) {
        return Completable.create(emitter -> {
            LithoView profileView = recyclerBinder
                    .getComponentAt(profiles.indexOf(correctProfile))
                    .getLithoView();

            if (profileView == null) {
                Timber.w("Recycler took unexpected amount of time to update");
                emitter.onComplete();
                return;
            }

            ViewTarget target = new ViewTarget(profileView);

            String styleTitle = answer.wasCorrect() ?
                    "Correct!" :
                    String.format("That's not %s...", correctProfile.getFirstName());

            //Show the profile's bio if available, otherwise show their job title
            //If neither is available, just show their full name
            String styleContent = answer.wasCorrect() ?
                    correctProfile
                            .getBio()
                            .or(correctProfile::getJobTitle)
                            .map(bioOrJobTitle -> bioOrJobTitle + "\n —" + correctProfile.getFirstName())
                            .orElse(correctProfile.getFullName()) :
                    String.format("You selected %s", answer.guessedProfile().getFullName());

            @StyleRes int showcaseStyle = answer.wasCorrect() ?
                    R.style.CorrectAnswerShowcaseTheme :
                    R.style.WrongAnswerShowcaseTheme;
            TextPaint textPaint = new TextPaint();
            int color = ContextCompat.getColor(getActivity(), R.color.answer_showcase_content_text_color);
            textPaint.setColor(color);
            ShowcaseView showcaseView = new ShowcaseView.Builder(getActivity())
                    .setTarget(target)
                    .setContentTitle(styleTitle)
                    .setContentText(styleContent)
                    .hideOnTouchOutside()
                    .setStyle(showcaseStyle)
                    .setContentTextPaint(textPaint)
                    .build();

            showcaseView.setOnShowcaseEventListener(new SimpleShowcaseEventListener() {
                @Override
                public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
                    emitter.onComplete();
                }
            });
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

        //Update the screen when either the Game or State are changed
        nameGameViewModel.getGame().observe(this, this::renderGame);
        nameGameViewModel.getState().observe(this, state -> this.renderGame(nameGameViewModel.getGame().getValue()));

        nameGameViewModel.setInitalGameData(game);

        nameGameViewModel.goToLoadState();
    }
}
