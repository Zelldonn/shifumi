package com.kaspiotr.shifumi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.ActionMenuItemView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kaspiotr.GameType;

public class GameSelectActivity extends AppCompatActivity {

    MaterialButton solo1, solo2, solo3, multi1, multi2, multi3;

    GameMode gameMode;
    GameType gameType;

    MaterialToolbar materialToolbar;
    ActionMenuItemView refresh_button;
    ActionMenuItemView leaderboard_button;



    public enum GameMode{
        MULTI, SOLO;
    }

    private FirebaseAuth mAuth;
    FirebaseFirestore db;
    FirebaseUser user;

    String userName = "", score = "", uuid = "";

    private void goTo(GameMode gm, GameType gt){
        Intent intent;
        if(gm == GameMode.MULTI){
            intent = new Intent(this, LobbyActivity.class);
            intent.putExtra("MyName", userName);
        }else{
            intent = new Intent(this, SoloGameActivity.class);
        }
        intent.putExtra("GameType", gt);

        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_select);

        materialToolbar = findViewById(R.id.top_bar);
        refresh_button = findViewById(R.id.refresh_button);
        refresh_button.setVisibility(View.INVISIBLE);

        leaderboard_button = findViewById(R.id.leader_board_button);
        leaderboard_button.setOnClickListener(v->{
            Intent intent = new Intent(this, LeaderBoardActivity.class);
            startActivity(intent);
        });


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

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();

        getUserInfo();

    }

    @Override
    protected void onResume() {
        super.onResume();
        getUserInfo();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(false);
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

    private void getUserInfo(){
        uuid = user.getUid();
        DocumentReference docRef = db.collection("users").document(uuid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        userName = document.getData().get("name").toString();
                        score = document.getData().get("score").toString();
                        materialToolbar.setTitle(userName +" (" + score + " pts)");
                    } else {
                        Log.d("FIREBASE", "No such document");
                    }
                } else {
                    Log.d("FIREBASE", "get failed with ", task.getException());
                }
            }
        });
    }




}