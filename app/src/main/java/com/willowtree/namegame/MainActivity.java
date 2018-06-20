package com.willowtree.namegame;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.willowtree.namegame.screens.gamedata.GameDataFragment;

import dagger.android.AndroidInjection;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AndroidInjection.inject(this);
    }
}
