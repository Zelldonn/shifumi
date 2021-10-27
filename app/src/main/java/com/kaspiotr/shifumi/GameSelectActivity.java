package com.kaspiotr.shifumi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.ActionMenuItemView;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

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
import com.kaspiotr.shifumi.games.SoloGameActivity;

public class GameSelectActivity extends AppCompatActivity {

    MaterialButton solo1, solo2, solo3, multi1, multi2, multi3, info1, info2, info3;

    GameMode gameMode;
    GameType gameType;

    MaterialToolbar materialToolbar;
    ActionMenuItemView refresh_button;
    ActionMenuItemView leaderboard_button;

    boolean tutoNeeded = false;

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
        info1 = findViewById(R.id.info_1);
        info1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTuto(2,2);
            }
        });

        solo2 = findViewById(R.id.solo_2);
        solo2.setOnClickListener( v -> {
            gameMode = GameMode.SOLO;
            gameType = GameType.Twist;
            goTo(gameMode, gameType);
        });
        info2 = findViewById(R.id.info_2);
        info2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTuto(3,3);
            }
        });

        solo3 = findViewById(R.id.solo_3);
        solo3.setOnClickListener( v -> {
            gameMode = GameMode.SOLO;
            gameType = GameType.MeliMelo;
            goTo(gameMode, gameType);
        });
        info3 = findViewById(R.id.info_3);
        info3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTuto(4,5);
            }
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

        if(!readPreferences()){
            showTuto(0, 6);
        }

    }

    private Boolean readPreferences(){
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        return !sharedPref.getString("tutodone", "").isEmpty();
    }

    private void savePreferences() {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("tutodone", "yes");
        editor.apply();
    }

    private void showTuto(int index, int maxIndex){
        final int[] currentIndex = {index};

        int[] images = {R.drawable.a,R.drawable.z,R.drawable.e, R.drawable.r,R.drawable.t,R.drawable.y,R.drawable.u}; // 6 is max

        Dialog tuto  = new Dialog(this);
        tuto.requestWindowFeature(Window.FEATURE_NO_TITLE);
        tuto.setContentView(R.layout.custom_dialog);
        ImageView image_container = tuto.findViewById(R.id.image_container);
        image_container.setBackgroundResource(images[currentIndex[0]]);

        Button previous = tuto.findViewById(R.id.previous_button);
        Button next = tuto.findViewById(R.id.next_button);
        if(index == maxIndex)
            next.setText("QUITTER");

            previous.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // show previous
                    currentIndex[0]--;
                    if(currentIndex[0] == index )
                        previous.setVisibility(View.INVISIBLE);
                    image_container.setBackgroundResource(images[currentIndex[0]]);
                    next.setVisibility(View.VISIBLE);
                    if(next.getText().equals("QUITTER")){
                        next.setText("SUIVANT");
                    }
                }
            });
            previous.setVisibility(View.INVISIBLE);

            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(next.getText().equals("QUITTER")){
                        tuto.cancel();
                    }else{
                        // Show next
                        currentIndex[0]++;
                        if(currentIndex[0] >= images.length - 1 || currentIndex[0] >= maxIndex){
                            next.setText("QUITTER");
                        }
                        image_container.setBackgroundResource(images[currentIndex[0]]);
                        previous.setVisibility(View.VISIBLE);
                    }
                }
            });
            tuto.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    savePreferences();
                }
            });
        tuto.show();
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