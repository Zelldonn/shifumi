package com.kaspiotr.shifumi.games;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaspiotr.GameType;
import com.kaspiotr.Move;
import com.kaspiotr.shifumi.R;

public abstract class GameActivity extends AppCompatActivity {

    private ImageView[] imageView = new ImageView[8];

    private ImageView move_p1, move_p2;
    protected int p1_score = 0, p2_score = 0, round = 1;

    public static final int ROCK = 0, PAPER = 1, SCISSSORS = 2, HOLE = 3, WATER = 6, AIR = 7, SPONGE = 4, FIRE = 5;

    protected GameType gameType;

    private TextView gameStatus_text_view, round_text_view, p1_text_view, p2_text_view;


    protected TextView playerIndication;

    public static int calculateNewScore(int score, boolean isWinner, GameType gt){
        int newScore = score;
        if(isWinner){
            if(gt == GameType.Classic){
                newScore += 3;
            }else if(gt == GameType.Twist){
                newScore += 5;
            }else{
                newScore += 8;
            }
        }else{
            if(gt == GameType.Classic){
                newScore -= 1;
            }else if(gt == GameType.Twist){
                newScore -= 2;
            }else{
                newScore -= 3;
            }
        }
        if(newScore < 0)
            return 0;
        else
            return newScore;
    }


    private int getImageFromMove(Move move){
        switch (move){
            case None: return R.drawable.waiting;
            case Pierre: return R.drawable.rock;
            case Feuille: return R.drawable.paper;
            case Ciseau:return R.drawable.scissors;
            case Puit:return R.drawable.hole;
            case Eponge:return R.drawable.sponge;
            case Feu:return R.drawable.fire;
            case Eau:return R.drawable.water;
            case Air:return R.drawable.air;
        }
        return R.drawable.waiting;
    }

    public void setMove_p1(Move move) {
        move_p1.setBackgroundResource(getImageFromMove(move));
    }

    public void setMove_p2(Move move) {
        move_p2.setBackgroundResource(getImageFromMove(move));
    }

    private void initComponent(){
        gameStatus_text_view = findViewById(R.id.game_status_text_view);
        round_text_view = findViewById(R.id.round_text_view);
        p1_text_view = findViewById(R.id.you_text);
        p2_text_view = findViewById(R.id.opponent_text);
        playerIndication = findViewById(R.id.player_indication);

        move_p1 = findViewById(R.id.move_p1);
        move_p2 = findViewById(R.id.move_p2);
        move_p1.setBackgroundResource(R.drawable.waiting);
        move_p2.setBackgroundResource(R.drawable.waiting);

        imageView[ROCK] = findViewById(R.id.rock);
        imageView[ROCK].setOnClickListener(v -> {
            if(onMove(Move.Pierre))
                move_p1.setBackgroundResource(R.drawable.rock);
        });

        imageView[FIRE] = findViewById(R.id.fire);
        imageView[FIRE].setOnClickListener(v -> {
            if(onMove(Move.Feu))
                move_p1.setBackgroundResource(R.drawable.fire);
        });
        imageView[PAPER] = findViewById(R.id.paper);
        imageView[PAPER].setOnClickListener(v -> {
            if(onMove(Move.Feuille))
                move_p1.setBackgroundResource(R.drawable.paper);
        });

        imageView[SCISSSORS] = findViewById(R.id.scissors);
        imageView[SCISSSORS].setOnClickListener(v -> {
            if(onMove(Move.Ciseau))
                move_p1.setBackgroundResource(R.drawable.scissors);
        });

        imageView[WATER] = findViewById(R.id.water);
        imageView[WATER].setOnClickListener(v -> {
            if(onMove(Move.Eau))
                move_p1.setBackgroundResource(R.drawable.water);
        });

        imageView[HOLE] = findViewById(R.id.hole);
        imageView[HOLE].setOnClickListener(v -> {
            if(onMove(Move.Puit))
                move_p1.setBackgroundResource(R.drawable.hole);
        });

        imageView[SPONGE] = findViewById(R.id.sponge);
        imageView[SPONGE].setOnClickListener(v -> {
            if(onMove(Move.Eponge))
                move_p1.setBackgroundResource(R.drawable.sponge);
        });

        imageView[AIR] = findViewById(R.id.air);
        imageView[AIR].setOnClickListener(v -> {
            if(onMove(Move.Air))
                move_p1.setBackgroundResource(R.drawable.air);
        });
    }

    protected abstract boolean onMove(Move move);

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
            setVisibleTo(AIR);
            imageView[HOLE].setVisibility(View.INVISIBLE);
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
        initComponent();
        showImage();
    }

    public TextView getGameStatus_text_view() {
        return gameStatus_text_view;
    }

    public void setGameStatus_text_view(String text) {
        this.gameStatus_text_view.setText(text);
    }

    public TextView getRound_text_view() {
        return round_text_view;
    }

    public void setRound_text_view(String text) {
        this.round_text_view.setText(text);
    }

    public TextView getP1_text_view() {
        return p1_text_view;
    }

    public void setP1_text_view(String text) {
        this.p1_text_view.setText(text);
    }

    public TextView getP2_text_view() {
        return p2_text_view;
    }

    public void setP2_text_view(String text) {
        this.p2_text_view.setText(text);
    }

    public TextView getPlayerIndication() {
        return playerIndication;
    }

    public void setPlayerIndication(String text) {
        playerIndication.setText(text);
    }
}