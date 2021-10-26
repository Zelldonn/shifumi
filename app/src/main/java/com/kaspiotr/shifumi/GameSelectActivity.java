package com.kaspiotr.shifumi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.button.MaterialButton;
import com.kaspiotr.GameType;
import com.kaspiotr.shifumi.games.GameActivity;

public class ShifumiActivity extends AppCompatActivity {

    MaterialButton solo1, solo2, solo3, multi1, multi2, multi3;

    GameMode gameMode;
    GameType gameType;

    public enum GameMode{
        MULTI, SOLO;
    }

    private void goTo(GameMode gm, GameType gt){
        Intent intent;
        if(gm == GameMode.MULTI){
            intent = new Intent(this, LobbyActivity.class);
        }else{
            intent = new Intent(this, SoloGame.class);
        }
        intent.putExtra("GameType", gt);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shifumi);

        solo1 = findViewById(R.id.solo_1);
        solo1.setOnClickListener( v -> {
            gameMode = GameMode.SOLO;
            gameType = GameType.Classic;
            goTo(gameMode, gameType);
        });

        solo2 = findViewById(R.id.solo_2);
        solo2.setOnClickListener( v -> {
            gameMode = GameMode.SOLO;
            gameType = GameType.Twist;
            goTo(gameMode, gameType);
        });

        solo3 = findViewById(R.id.solo_3);
        solo3.setOnClickListener( v -> {
            gameMode = GameMode.SOLO;
            gameType = GameType.MeliMelo;
            goTo(gameMode, gameType);
        });

        /* ------------- MULTIPLAYER ------------------*/

        multi1 = findViewById(R.id.multi_1);
        multi1.setOnClickListener(v -> {
            gameMode = GameMode.MULTI;
            gameType = GameType.Classic;
            goTo(gameMode, gameType);
        });

        multi2 = findViewById(R.id.multi_2);
        multi2.setOnClickListener(v -> {
            gameMode = GameMode.MULTI;
            gameType = GameType.Twist;
            goTo(gameMode, gameType);
        });

        multi3 = findViewById(R.id.multi_3);
        multi3.setOnClickListener(v -> {
            gameMode = GameMode.MULTI;
            gameType = GameType.MeliMelo;
            goTo(gameMode, gameType);
        });
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(false);
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }
}