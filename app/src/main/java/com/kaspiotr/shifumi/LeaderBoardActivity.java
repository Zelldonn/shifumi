package com.kaspiotr.shifumi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.ActionMenuItemView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class LeaderBoardActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayList<String> players;
    private ArrayAdapter<String> adapter;

    private FirebaseAuth mAuth;
    FirebaseFirestore db;
    FirebaseUser user;

    private ActionMenuItemView refresh_button, leader_board_button;
    MaterialToolbar leader_board_tool_bar;

    private void makeToast(String s){
        this.runOnUiThread(() -> {
            Toast t = Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG);
            t.show();
        });
    }
    private void updateUI(){
        this.runOnUiThread(() -> adapter.notifyDataSetChanged());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);

        refresh_button = findViewById(R.id.refresh_button);
        refresh_button.setOnClickListener(v->{
            makeToast("Refreshing ");
            getRank();
        });
        leader_board_tool_bar = findViewById(R.id.leader_board_tool_bar);
        leader_board_tool_bar.setOnClickListener(v -> {
            onBackPressed();
        });
        leader_board_button = findViewById(R.id.leader_board_button);
        leader_board_button.setVisibility(View.INVISIBLE);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();

        players = new ArrayList<>();
        getRank();

        listView = findViewById(R.id.leader_board_list_view);

        adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, players);
        listView.setAdapter(adapter);

    }
    private void getRank(){
        players.clear();
        String myuuid = user.getUid();
        db.collection("users")
                .orderBy("score", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int rank = 1, lastScore = -1, score = -1, index = 0 ;
                            boolean exaequo = false;
                            String lastRank = "";
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                exaequo = false;
                                score = Integer.parseInt(document.getData().get("score").toString());
                                String name = document.getData().get("name").toString();
                                if(score == lastScore){

                                    String newRank = lastRank +  " (exaequo)";
                                    players.set(index-1, newRank);
                                    exaequo = true;
                                    rank--;
                                }

                                String info = name + " : "+ score + (score > 1 ? " points | " : " point | ") + rank + (rank == 1 ?"er" : "e") + (exaequo ? " (exaequo)" : "");
                                lastRank = info;
                                players.add(info);
                                Log.d("SUCCESS GETTING LEADERBOARD", info);
                                lastScore = score;
                                rank++;
                                index++;
                            }
                            updateUI();
                        } else {
                            Log.d("ERROR GETTING LEADERBOARD", "Error getting documents: ", task.getException());
                        }
                    }
        });
    }
}