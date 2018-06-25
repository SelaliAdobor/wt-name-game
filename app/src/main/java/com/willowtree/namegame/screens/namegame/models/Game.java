package com.willowtree.namegame.screens.namegame.models;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;

import java.util.ArrayList;
import java.util.List;

import java9.util.Optional;

import static java9.util.stream.StreamSupport.stream;

@AutoValue
public abstract class Game implements Parcelable {
    public abstract List<Challenge> challenges();

    public abstract List<Answer> answers();

    public static Game createGame(List<Challenge> challenges) {

        return new AutoValue_Game(challenges, new ArrayList<>());
    }

    public static Game addAnswer(Game initalGame, Answer answer) {
        ArrayList<Answer> updatedAnswers = new ArrayList<>(initalGame.answers());

        updatedAnswers.add(answer);

        return new AutoValue_Game(initalGame.challenges(), updatedAnswers);
    }

    public boolean isFinished() {
        return answers().size() >= challenges().size();
    }

    public Optional<Challenge> firstUnansweredChallenge() {
        return stream(challenges()) //Find the first challenge that doesn't have a matching answer
                .filter(challenge ->
                        stream(answers())
                                .noneMatch(answer -> answer.challenge().equals(challenge))
                ).findFirst();
    }
}
