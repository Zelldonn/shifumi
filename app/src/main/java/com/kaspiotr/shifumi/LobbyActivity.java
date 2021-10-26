package com.example.shifumi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.shifumi.adapter.ServerAdapter;
import com.example.shifumi.games.ClassiqueActivity;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class LobbyActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<String> servers;
    ArrayAdapter<String> adapter;

    MaterialToolbar materialToolbar;

    Button create_button;

    String game_type;

    private Boolean populateListView(){
        servers = new ArrayList<>();
        servers.add("ID1");
        servers.add("ID2");
        return true;
    }

    private void makeToast(String s){
        Toast t = Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT);
        t.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String message = intent.getStringExtra("game_type");
        Log.e("GAME TYPE", message);
        game_type = message;

        listView = findViewById(R.id.list_view);
        create_button = findViewById(R.id.create_button);
        create_button.setOnClickListener(v->{
            // CREATE NEW GAME
            Intent myIntent = new Intent(this, ClassiqueActivity.class);
            this.startActivity(myIntent);
        });
        populateListView();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String t = servers.get(i);
                makeToast(t);

                // JOIN EXISTING GAME
            }
        });


        materialToolbar = findViewById(R.id.tool_bar);
        materialToolbar.setTitle("Lobby" + " | " + game_type);

        adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, servers);
        listView.setAdapter(adapter);

    }
}