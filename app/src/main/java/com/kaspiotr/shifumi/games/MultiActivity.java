package com.kaspiotr.shifumi.games;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kaspiotr.GameType;
import com.kaspiotr.Move;
import com.kaspiotr.shifumi.GameSelectActivity;
import com.kaspiotr.shifumi.LobbyActivity;
import com.kaspiotr.shifumi.games.GameActivity;
import com.kaspiotr.shifumi.network.ServerConnection;
import com.kaspiotr.shifumi.network.ServerListener;

import java.io.IOException;
import java.net.Socket;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class MultiActivity extends GameActivity implements ServerListener {

    private Socket socket;

    private ServerConnection serverConnection;

    private String id = "", myName = "", hisName = "opponent", uuid ="";

    boolean gameStarted = false, canPlay = true;

    Move lastMove;

    private FirebaseAuth mAuth;
    FirebaseFirestore db;
    FirebaseUser user;

    @Override
    protected boolean onMove(Move move) {
        if(!gameStarted || !canPlay)
            return false;


        runOnUiThread(() -> setPlayerIndication(hisName + " choisi un élèment..."));
        canPlay = false;
        serverConnection.sendMove(move);
        lastMove = move;
        setMove_p2(Move.None);
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        Bundle extras = getIntent().getExtras();
        if ((extras != null) && (extras.containsKey("GameType"))) {
            gameType = (GameType) extras.getSerializable("GameType");
        }
        if ((extras != null) && (extras.containsKey("ID"))) {
            id = extras.getString("ID");
        }
        if ((extras != null) && (extras.containsKey("MyName"))) {
            Log.i("GETTING EXTRA", "test");
            myName = extras.getString("MyName");
        }
        if ((extras != null) && (extras.containsKey("Uuid"))) {
            Log.i("GETTING EXTRA", "test");
            uuid = extras.getString("Uuid");
        }

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();

        try {
            this.socket = new Socket("152.228.218.103", 12345);
            //this.socket = new Socket("86.76.170.124", 12345);
            this.serverConnection = new ServerConnection(socket, this);
            if(id.isEmpty()){
                // Creator
                serverConnection.sendCreate(gameType);
            }else{
                // Joiner
                serverConnection.sendJoin(id);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLobbiesList(ServerConnection connection, String ___TEMP___) {

    }

    @Override
    public void onGameCreated(ServerConnection connection, String gameID) {
        serverConnection.sendInfo(myName);
        setGameStatus_text_view("Attendez que quelqu'un rejoigne la partie !");
        Log.i("DEBUG",gameID);
    }

    @Override
    public void onNewPlayer(ServerConnection connection, String playerName) {
        setP2_text_view(playerName);
        hisName = playerName;

        setGameStatus_text_view(playerName + " a rejoint la partie !");
        Log.i("DEBUG","player name : "  + playerName);
    }

    @Override
    public void onGameStart(ServerConnection connection, GameType type) {
        runOnUiThread(() -> setPlayerIndication("Choississez un élèment"));
        canPlay = true;
        //Log.i("DEBUG","Game started type : " + type);
        gameStarted = true;
    }

    @Override
    public void onGameEnd(ServerConnection connection, String winner) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        if(winner.equals("win")){
            updateScore(true);
            setRound_text_view("VICTOIRE !");
            builder.setTitle("Partie terminée, vous avez gagné !");

        }else{
            updateScore(false);
            setRound_text_view("DEFAITE :(");
            builder.setTitle("Partie terminée, vous avez perdu !");
        }
        builder.setMessage("Que souhaitez vous faire");
        builder.setPositiveButton("Rejouer", (dialogInterface, i) -> {
            Intent intent = new Intent(this, LobbyActivity.class);
            intent.putExtra("MyName", myName);
            intent.putExtra("GameType", gameType);
            startActivity(intent);
        });
        builder.setNegativeButton("Quitter", (dialogInterface, i) -> {
            Intent intent = new Intent(this, GameSelectActivity.class);
            startActivity(intent);
        });
        builder.setCancelable(false);
        runOnUiThread(() -> builder.show());

    }

    @Override
    public void onRoundStart(ServerConnection connection, long duration) {
        runOnUiThread(() -> setPlayerIndication("Choississez un élèment"));
        canPlay = true;
        setRound_text_view("Manche " + round);
        setMove_p1(Move.None);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            socket.close();
            Log.i("SOC CLOSED", "closed");
            Intent intent = new Intent(this, GameSelectActivity.class);
            startActivity(intent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRoundEnd(ServerConnection connection, boolean isDraw, String winner, Move othersMove) {
        //makeToast("Round ended, isDraw : " + isDraw + ", winner : "+ winner + "Opponent played : " +othersMove.toString());
        setMove_p2(othersMove);

        if(!isDraw){
            if(winner.equals("win")){
                p1_score++;
                setGameStatus_text_view("Votre " + lastMove.toString() + " a battu " + othersMove.toString());
            }
            else{
                p2_score++;
                setGameStatus_text_view("Votre " + lastMove.toString() + " s'est fait battre par " + othersMove.toString());
            }

            setP2_text_view(hisName + " " + p2_score);
            setP1_text_view("You " + p1_score);
            round++;
        }
        if(isDraw)
            setGameStatus_text_view("Match nul, rejouez !");

    }

    @Override
    public void onError(ServerConnection connection, String errorMessage) {
        //makeToast("server error " + errorMessage);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Le serveur a envoyé une erreur...");
        builder.setMessage(errorMessage);
        builder.setPositiveButton("OK :(", (dialogInterface, i) -> {
            Intent intent = new Intent(this, LobbyActivity.class);
            intent.putExtra("MyName", myName);
            intent.putExtra("GameType", gameType);
            startActivity(intent);
        });
        builder.setCancelable(false);
        runOnUiThread(() -> builder.show());
    }

    @Override
    public void onConnectionClosed(ServerConnection connection) {
        makeToast("Connection closed");
    }

    @Override
    public void onJoined(ServerConnection connection) {
        serverConnection.sendInfo(myName);
    }

    private void makeToast(String s){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast t = Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT);
                t.show();
            }
        });
    }

    private void updateScore(boolean isWinner){
        uuid = user.getUid();
        DocumentReference docRef = db.collection("users").document(uuid);
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