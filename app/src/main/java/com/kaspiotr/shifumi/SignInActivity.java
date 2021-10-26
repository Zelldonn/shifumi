package com.kaspiotr.shifumi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignInActivity extends AppCompatActivity {


    Button sign_in_button;
    ImageView back_button;

    TextInputEditText email, pswd;
    String retrieved_email, retrieved_pswd;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        mAuth = FirebaseAuth.getInstance();
        initComponent();
    }
    private Boolean readPreferences(){
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        retrieved_email = sharedPref.getString("email", "");
        retrieved_pswd = sharedPref.getString("pswd", "");
        return !retrieved_email.isEmpty() && !retrieved_pswd.isEmpty();
    }



    private void savePreferences(String e, String p) {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("email", e);
        editor.putString("pswd", p);
        editor.apply();
    }

    private void initComponent(){

        email = findViewById(R.id.sign_in_email_input);
        pswd = findViewById(R.id.sign_in_pswd_input);
        back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(v -> {
            this.onBackPressed();
        });

        sign_in_button = findViewById(R.id.last_sign_in_button);
        sign_in_button.setOnClickListener(v -> {
            signIn(email.getText().toString().trim(), pswd.getText().toString());
        });

        if(readPreferences()){
            email.setText(retrieved_email);
            pswd.setText(retrieved_pswd);
        }

        Log.i("SHARED", retrieved_email +" " + retrieved_pswd);
        email.setText(retrieved_email);
        pswd.setText(retrieved_pswd);
    }

    private void signIn(String email_, String password_){
        mAuth.signInWithEmailAndPassword(email_, password_)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(getApplicationContext(), "Authentication success ",
                                    Toast.LENGTH_SHORT).show();
                            // Save preferences
                            savePreferences(email_, password_);
                            Intent myIntent = new Intent(SignInActivity.this, GameSelectActivity.class);
                            startActivity(myIntent);

                            Log.i("MY USER", mAuth.getCurrentUser().toString());

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("FIREBASE", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed : " +task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
        });
    }
}