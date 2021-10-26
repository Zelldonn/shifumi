package com.example.shifumi;

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
    String email_, pswd_;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        mAuth = FirebaseAuth.getInstance();
        initComponent();
    }
    private Boolean readPreferences(){
        /*SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        email_ = sharedPref.getString("email", "");
        pswd_ = sharedPref.getString("pswd", "");*/
        email_ = "stringaripierre@hotmail.fr";
        pswd_ = "motorolaxtr446";
        return !email_.isEmpty() && !pswd_.isEmpty();
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
            email.setText(email_);
            pswd.setText(pswd_);
            signIn(email.getText().toString().trim(), pswd.getText().toString());
        }

        Log.i("SHARED",email_ +" " + pswd_);
        email.setText(email_);
        pswd.setText(pswd_);
    }

    private void signIn(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(getApplicationContext(), "Authentication success ",
                                    Toast.LENGTH_SHORT).show();
                            Intent myIntent = new Intent(SignInActivity.this, ShifumiActivity.class);
                            startActivity(myIntent);
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