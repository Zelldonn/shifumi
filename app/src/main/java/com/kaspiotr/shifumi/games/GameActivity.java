package com.kaspiotr.shifumi.games;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.kaspiotr.GameType;
import com.kaspiotr.shifumi.R;

public class MultiActivity extends AppCompatActivity {

    ImageView[] imageView = new ImageView[8];

    ImageView move_p1, move_p2;

    public enum GameMode{
        MULTI, SOLO;
    }

    public static final int ROCK = 0, PAPER = 1, SCISSSORS = 2, HOLE = 3, WATER = 4, AIR = 5, SPONGE = 6, FIRE = 7;

    GameType gameType;

    GameMode gameMode;

    String id = "";

    private void initComponent(){
        move_p1 = findViewById(R.id.move_p1);
        move_p2 = findViewById(R.id.move_p2);
        move_p1.setBackgroundResource(R.drawable.waiting);
        move_p2.setBackgroundResource(R.drawable.waiting);

        imageView[ROCK] = findViewById(R.id.rock);
        imageView[FIRE] = findViewById(R.id.fire);
        imageView[PAPER] = findViewById(R.id.paper);
        imageView[SCISSSORS] = findViewById(R.id.scissors);
        imageView[WATER] = findViewById(R.id.water);
        imageView[HOLE] = findViewById(R.id.hole);
        imageView[HOLE].setOnClickListener(v ->{
            move_p1.setBackgroundResource(R.drawable.hole);
        });
        imageView[SPONGE] = findViewById(R.id.sponge);
        imageView[AIR] = findViewById(R.id.air);


    }

    private void allInvisible(){
        for(int i = 0; i < 8 ; i++){
            imageView[i].setVisibility(View.INVISIBLE);
        }
    }

    private void setVisibleTo(int endIndex){
        for(int i = 0; i < endIndex+1 ; i++){
            imageView[i].setVisibility(View.VISIBLE);
        }
    }

    private void showImage(){
        allInvisible();
        if(gameType == GameType.Classic){
            setVisibleTo(SCISSSORS);
        }else if(gameType == GameType.Twist){
            setVisibleTo(HOLE);
        }else{
            setVisibleTo(FIRE);
            imageView[HOLE].setVisibility(View.INVISIBLE);
        }
    }

    private void startGame(){
        if(id.isEmpty()){
            // This mean we created the game and we are waiting for a player to join
        }else{

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classique);
        Bundle extras = getIntent().getExtras();
        if ((extras != null) && (extras.containsKey("GameType"))) {
            gameType = (GameType) extras.getSerializable("GameType");
        }
        if ((extras != null) && (extras.containsKey("GameMode"))) {
            gameMode = (GameMode) extras.getSerializable("GameMode");
        }
        if ((extras != null) && (extras.containsKey("ID"))) {
            id = extras.getString("ID");
        }

/*
        Log.i("TYPE",gameType + " "+ gameMode + (id.isEmpty() ?"" : " optional id : " + id));
*/
        initComponent();
        showImage();
        startGame();
    }
}