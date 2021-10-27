package com.kaspiotr.shifumi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.kaspiotr.shifumi.SignInUp.SignInActivity;
import com.kaspiotr.shifumi.SignInUp.SignUpActivity;

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
