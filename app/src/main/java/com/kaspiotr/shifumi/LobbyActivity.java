package com.kaspiotr.shifumi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.ActionMenuItemView;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.kaspiotr.GameType;
import com.kaspiotr.Move;
import com.kaspiotr.shifumi.games.GameActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.kaspiotr.shifumi.network.ServerConnection;
import com.kaspiotr.shifumi.network.ServerListener;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class LobbyActivity extends AppCompatActivity implements ServerListener {

    private ListView listView;
    private ArrayList<String> servers;
    private ArrayAdapter<String> adapter;

    private MaterialToolbar materialToolbar;

    private Button create_button;

    private GameType gameType;

    private Socket socket;

    private ServerConnection serverConnection;

    private String myName = "", uuid;

    private ActionMenuItemView refresh_button, leaderboard_button;


    private void makeToast(String s){
        this.runOnUiThread(() -> {
            Toast t = Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG);
            t.show();
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        servers = new ArrayList<>();

        refresh_button = findViewById(R.id.refresh_button);
        refresh_button.setOnClickListener(v->{
            makeToast("Refreshing ");
            this.serverConnection.sendListRequest(gameType);
        });
        leaderboard_button =findViewById(R.id.leader_board_button);
        leaderboard_button.setVisibility(View.INVISIBLE);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Get the Intent that started this activity and extract the string
        Bundle extras = getIntent().getExtras();
        if ((extras != null) && (extras.containsKey("GameType"))) {
            gameType = (GameType) extras.getSerializable("GameType");
        }
        if ((extras != null) && (extras.containsKey("MyName"))) {
            myName = extras.getString("MyName");
        }
        if ((extras != null) && (extras.containsKey("Uuid"))) {
            uuid = extras.getString("Uuid");
        }

        try {
            this.socket = new Socket("152.228.218.103", 12345);
            //this.socket = new Socket("86.76.170.124", 12345);
            this.serverConnection = new ServerConnection(socket, this);
            this.serverConnection.sendListRequest(gameType);
        } catch (IOException e) {
            e.printStackTrace();
        }


        create_button = findViewById(R.id.create_button);
        create_button.setOnClickListener(v->{
            // CREATE NEW GAME
            Intent myIntent = new Intent(this, MultiActivity.class);
            myIntent.putExtra("GameType", gameType);
            myIntent.putExtra("MyName", myName);
            myIntent.putExtra("Uuid", uuid);
            this.startActivity(myIntent);
        });

        listView = findViewById(R.id.list_view);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String id = servers.get(i);
                makeToast("Attempting to join id : "+ id);
                // JOIN EXISTING GAME
                joinActivity(id);
            }
        });

        materialToolbar = findViewById(R.id.tool_bar);
        materialToolbar.setTitle("MULTI | " + gameType);

        adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, servers);
        listView.setAdapter(adapter);
    }
    private void joinActivity(String id){
        Intent myIntent = new Intent(this, MultiActivity.class);
        myIntent.putExtra("GameType", gameType);
        myIntent.putExtra("ID", id);
        myIntent.putExtra("MyName", myName);
        myIntent.putExtra("Uuid", uuid);
        startActivity(myIntent);
    }


    @Override
    public void onLobbiesList(ServerConnection connection, String data) {
        servers.clear();
        //Log.i("LOBBY LIST", ___TEMP___);
        String[] res = data.split(",");

        if(res.length == 1)
            makeToast("NO SERVER FOUND");

        for(int i = 1; i < res.length ;i++){
            String[] subsplit = res[i].split("-");
            servers.add(subsplit[0]);
        }
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });


    }

    @Override
    public void onGameCreated(ServerConnection connection, String gameID) {

    }

    @Override
    public void onNewPlayer(ServerConnection connection, String playerName) {

    }

    @Override
    public void onGameStart(ServerConnection connection, GameType type) {

    }

    @Override
    public void onGameEnd(ServerConnection connection, String winner) {

    }

    @Override
    public void onRoundStart(ServerConnection connection, long duration) {

    }

    @Override
    public void onRoundEnd(ServerConnection connection, boolean isDraw, String winner, Move othersMove) {

    }

    @Override
    public void onError(ServerConnection connection, String errorMessage) {

    }

    @Override
    public void onConnectionClosed(ServerConnection connection) {

    }

    @Override
    public void onJoined(ServerConnection connection) {

    }

}