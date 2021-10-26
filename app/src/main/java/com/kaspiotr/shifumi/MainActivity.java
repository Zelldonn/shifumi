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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button_sign_in = findViewById(R.id.sign_in_button);
        button_sign_up = findViewById(R.id.sign_up_button);

        button_sign_in.setOnClickListener(v -> {
            Intent myIntent = new Intent(this, SignInActivity.class);
            this.startActivity(myIntent);
        });
        button_sign_up.setOnClickListener(v -> {
            Intent myIntent = new Intent(this, SignUpActivity.class);
            this.startActivity(myIntent);
        });
    }
}
