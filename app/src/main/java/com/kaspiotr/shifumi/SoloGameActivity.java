package com.kaspiotr.shifumi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kaspiotr.FightRule;
import com.kaspiotr.GameType;
import com.kaspiotr.Move;
import com.kaspiotr.MoveMaker;
import com.kaspiotr.ShifumiGame;
import com.kaspiotr.shifumi.games.GameActivity;

import java.util.Random;

public class SoloGameActivity extends GameActivity {

    private FightRule rules;

    private int currentRound = 0;

    private MoveMaker ai = () -> {
        Random m = new Random();
        int index = 0;
        switch (gameType){
            case Classic:
                do{
                    index = m.nextInt(Move.values().length-1) + 1;
                }while(index != 1 && index != 2 && index != 3);
                break;
            case Twist:
                do{
                    index = m.nextInt(Move.values().length-1) + 1;
                }while(index != 1 && index != 2 && index != 3 && index != 4);
                break;
            case MeliMelo:
                do{
                    index = m.nextInt(Move.values().length-1) + 1;
                }while(index ==4);
                break;
        }
        return Move.values()[index];
    };

    private FirebaseAuth mAuth;
    FirebaseFirestore db;
    FirebaseUser user;

    @Override
    protected boolean onMove(Move move) {
        doMove(move);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if ((extras != null) && (extras.containsKey("GameType"))) {
            gameType = (GameType) extras.getSerializable("GameType");
        }
        rules = new FightRule();

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();

        switch (gameType){
            case Classic:
                initClassic();
                break;
            case Twist:
                initTwist();
                break;
            case MeliMelo:
                initMeliMelo();
                break;
        }
        setGameStatus_text_view("A vous de jouer !");
        setP2_text_view("IA");
        getProgressBar().setVisibility(View.INVISIBLE);
    }

    private void initClassic(){
        rules.addRule( Move.Pierre, Move.Ciseau );  // Pierre bat Ciseau
        rules.addRule( Move.Ciseau, Move.Feuille ); // Ciseau bat Feuille
        rules.addRule( Move.Feuille, Move.Pierre ); // Feuille bat Pierre

        currentRound = 1;
        p1_score = 0;
    }
    private void initTwist(){
        rules.addRule( Move.Pierre, Move.Ciseau );  // Pierre bat Ciseau
        rules.addRule( Move.Ciseau, Move.Feuille ); // Ciseau bat Feuille
        rules.addRule( Move.Puit, Move.Pierre ); // Puit bat Pierre
        rules.addRule( Move.Puit, Move.Ciseau ); // Puit bat Ciseau
        rules.addRule( Move.Feuille, Move.Pierre ); // Feuille bat Pierre
        rules.addRule( Move.Feuille, Move.Puit ); // Feuille bat Puit

        currentRound = 1;
        p1_score = 0;
    }
    private void initMeliMelo(){
        rules.addRule( Move.Pierre, Move.Feu );  // Pierre bat Ciseau
        rules.addRule( Move.Feu, Move.Ciseau ); // Ciseau bat Feuille
        rules.addRule( Move.Ciseau, Move.Eponge ); // Feuille bat Pierre
        rules.addRule( Move.Eponge, Move.Feuille );  // Pierre bat Ciseau
        rules.addRule( Move.Feuille, Move.Air ); // Ciseau bat Feuille
        rules.addRule( Move.Air, Move.Eau ); // Feuille bat Pierre
        rules.addRule( Move.Eau, Move.Pierre );  // Pierre bat Ciseau

        rules.addRule( Move.Pierre, Move.Ciseau );  // Pierre bat Ciseau
        rules.addRule( Move.Pierre, Move.Eponge ); // Ciseau bat Feuille

        rules.addRule( Move.Feu, Move.Eponge ); // Feuille bat Pierre
        rules.addRule( Move.Feu, Move.Feuille );  // Pierre bat Ciseau

        rules.addRule( Move.Ciseau, Move.Air ); // Ciseau bat Feuille
        rules.addRule( Move.Ciseau, Move.Feuille ); // Feuille bat Pierre

        rules.addRule( Move.Eponge, Move.Air );  // Pierre bat Ciseau
        rules.addRule( Move.Eponge, Move.Eau );  // Pierre bat Ciseau

        rules.addRule( Move.Feuille, Move.Eau ); // Ciseau bat Feuille
        rules.addRule( Move.Feuille, Move.Pierre ); // Feuille bat Pierre

        rules.addRule( Move.Air, Move.Feu );  // Pierre bat Ciseau
        rules.addRule( Move.Air, Move.Pierre ); // Ciseau bat Feuille

        rules.addRule( Move.Eau, Move.Feu ); // Feuille bat Pierre
        rules.addRule( Move.Eau, Move.Ciseau );  // Pierre bat Ciseau


        currentRound = 1;
        p1_score = 0;
    }
    public void doMove(Move m){

        Move aiMove = ai.pick();

        setRound_text_view("Manche " + currentRound);
        setMove_p2(aiMove);

        int fightResult = rules.fight( m, aiMove );
        if( fightResult == FightRule.FIGHT_WIN){
            //We win this round
            p1_score ++;
            currentRound++;
            setGameStatus_text_view("Continuez comme ça !");
        }
        else if(fightResult == FightRule.FIGHT_LOOSE){
            //AI wins the round
            currentRound++;
            p2_score++;
            setGameStatus_text_view("Vous ferai mieux la prochaine fois !");
        }
        else{
            //Draw
            setGameStatus_text_view("Match nul, rejouez !");
        }
        setP2_text_view("IA " + p2_score);
        setP1_text_view("You " + p1_score);

        if(p1_score >= 3 || p2_score >= 3){
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);

            System.out.println("End of the game");
            if(p1_score > 2){
                updateScore(true);
                setRound_text_view("VICTOIRE !");
                builder.setTitle("Partie terminée, vous avez gagné !");
            }
            else{
                updateScore(false);
                setRound_text_view("DEFAITE :(");
                builder.setTitle("Partie terminée, vous avez perdu !");
            }

            builder.setMessage("Que souhaitez vous faire");
            builder.setPositiveButton("Rejouer", (dialogInterface, i) -> {
                Intent intent = new Intent(this, SoloGameActivity.class);
                intent.putExtra("GameType", gameType);
                startActivity(intent);
            });
            builder.setNegativeButton("Quitter", (dialogInterface, i) -> {
                Intent intent = new Intent(this, GameSelectActivity.class);
                startActivity(intent);
            });
            builder.setCancelable(false);
            builder.show();
        }
    }
    public int getPlayerScore(){
        return p1_score;
    }

    public int getAIScore() {
        return p2_score;
    }

    private void updateScore(boolean isWinner){

        DocumentReference docRef = db.collection("users").document(user.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("FIREBASE", "DocumentSnapshot data: " + document.getData());

                        int score = Integer.parseInt(document.getData().get("score").toString());
                        int newScore = calculateNewScore(score, isWinner, gameType);

                        docRef.update("score", newScore);

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