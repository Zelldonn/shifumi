package com.example.shifumi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class SignUpActivity extends AppCompatActivity {

    TextView sign_up_state;
    ImageView back_button;
    int state_number = 1;
    String state = "Création de compte " + state_number +"/2";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        sign_up_state = findViewById(R.id.sign_up_state);
        back_button = findViewById(R.id.back_button);

        sign_up_state.setText(state);

        back_button.setOnClickListener(v -> {
            //Intent myIntent = new Intent(this, MainActivity.class);
            //this.startActivity(myIntent);
            if(state_number == 1)
                replaceFragment(new SignUpFragmentTwo());
            else
                replaceFragment(new SignUpFragmentOne());

            state_number = (state_number == 1) ? 2 : 1;
            state = "Création de compte " + state_number +"/2";
            sign_up_state.setText(state);
        });

    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.sign_up_frame_layout,fragment);
        ft.commit();
    }
}