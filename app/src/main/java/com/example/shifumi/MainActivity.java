package com.example.shifumi;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    Button button_sign_in , button_sign_up ;
    TextView t;

    private FirebaseAuth mAuth;
// ...
// Initialize Firebase Auth

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button_sign_in = findViewById(R.id.sign_in_button);
        button_sign_up = findViewById(R.id.sign_up_button);
        t = findViewById(R.id.first_connection_textView);
        button_sign_in.setOnClickListener(v -> {
            t.setText("SIGN IN");

        });
        button_sign_up.setOnClickListener(v -> {
            t.setText("SIGN UP");
            Intent myIntent = new Intent(this, SignUpActivity.class);
            this.startActivity(myIntent);
        });

        mAuth = FirebaseAuth.getInstance();


    }
    public void sendMessage(View view){

    }

}
