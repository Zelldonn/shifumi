package com.kaspiotr.shifumi.SignInUp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kaspiotr.shifumi.GameSelectActivity;
import com.kaspiotr.shifumi.MainActivity;
import com.kaspiotr.shifumi.R;
import com.kaspiotr.shifumi.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity implements SignUpFragmentOne.OnButtonClickedListener, SignUpFragmentTwo.onFinishClickedListener  {

    TextView sign_up_step, birthdate_input;
    TextInputLayout name_input, surname_input, precise_input, email_input, pswd_input, pswd_confirm_input;
    ImageView back_button;
    RadioGroup radioGroup;
    Button continue_button, finish_button;

    int step = 1;
    Boolean canContinue = false;
    String state = "Création de compte " + step +"/2";


    Fragment one, two;

    private FirebaseAuth mAuth;
    FirebaseFirestore db;

    User myUser;

    private void handleBackButton(){
        if(step == 1){
            Intent myIntent = new Intent(this, MainActivity.class);
            this.startActivity(myIntent);
        }
        else{
            replaceFragment(one);
        }

        step = (step == 2) ? 1 : 1;
        state = "Création de compte " + step +"/2";
        sign_up_step.setText(state);
    }

    private void initFragments(){
        one = new SignUpFragmentOne();
        two = new SignUpFragmentTwo();

        sign_up_step = findViewById(R.id.sign_up_state);
        back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(v -> {
            handleBackButton();
        });

        name_input = findViewById(R.id.name_text_input);
        surname_input = findViewById(R.id.surname_text_input);
        birthdate_input = findViewById(R.id.birthdate_text_input);
        radioGroup = findViewById(R.id.radio_group);

        continue_button = findViewById(R.id.continue_button);


        email_input = findViewById(R.id.email_text_input);
        pswd_input = findViewById(R.id.pswd_text_input);
        pswd_confirm_input = findViewById(R.id.pswd_confirm_text_input);

        finish_button = findViewById(R.id.sign_up_finish_button);

        sign_up_step.setText(state);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        myUser = new User();

        initFragments();

        replaceFragment(one);

    }

    @Override
    public void onBackPressed() {
        handleBackButton();
    }


    private Boolean validateArgs(ArrayList<String> args){
        return true;
    }

    private void replaceFragment(Fragment fragment){
        //Bundle data = new Bundle();
        //fragment.setArguments(data);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.sign_up_frame_layout,fragment);
        ft.commit();
    }

    @Override
    public void onContinueClicked(View view, ArrayList<String> args) {

        myUser.setName(args.get(0));
        myUser.setSurname(args.get(1));
        myUser.setBirthdate(args.get(2));
        myUser.setGender(args.get(3));

        //myUser.printInfo();

        User.SIGN_UP_STATE sign_up_state = myUser.partOneState();
        if(sign_up_state == User.SIGN_UP_STATE.OK){
            replaceFragment(two);
            step = 2;
            state = "Création de compte " + step +"/2";
            sign_up_step.setText(state);
        }else{
            Context context = getApplicationContext();
            CharSequence text= "Erreur\n";
            int duration = Toast.LENGTH_SHORT;

            switch (sign_up_state){
                case NAME:
                    text += "prénom invalide\n";
                case SURNAME:
                    text += "nom invalide\n";
                case BIRTHDATE:
                    text += "date invalide\n";
                case GENDER:
                    text += "sexe invalide";

            }
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }

    private void signUp(){
        mAuth.createUserWithEmailAndPassword(myUser.getEmail(), myUser.getPassword())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            myUser.setId(user.getUid());
                            addData(myUser);
                        } else {
                            Log.w("FIREBASE", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed : " +task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
        });

    }

    private void addData(User user){
        Map<String, Object> data = new HashMap<>();
        data.put("name", user.getName());
        data.put("surname", user.getSurname());
        data.put("birthdate", user.getBirthdate());
        data.put("gender", user.getGender());
        data.put("score", 0);

        db.collection("users").document(user.getId())
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // We can go to the game

                        Toast.makeText(getApplicationContext(), "Authentication success ",
                                Toast.LENGTH_SHORT).show();
                        Intent myIntent = new Intent(SignUpActivity.this, GameSelectActivity.class);
                        startActivity(myIntent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Authentication failed : " +e,
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }



    @Override
    public void onFinishClicked(View view, ArrayList<String> args) {
        myUser.setEmail(args.get(0));

        if(!args.get(1).equals(args.get(2))){
            // Password are not identical !
            Context context = getApplicationContext();
            CharSequence text = "Les mots de passe doivent être identique !";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            return;
        }
        myUser.setPassword(args.get(1));

        User.SIGN_UP_STATE sign_up_state = myUser.partTwoState();
        if(sign_up_state == User.SIGN_UP_STATE.OK){
            Log.i("STATE", "signing up");
            signUp();
        }else{
            Context context = getApplicationContext();
            CharSequence text= "Erreur\n";
            int duration = Toast.LENGTH_SHORT;

            switch (sign_up_state){
                case EMAIL:
                    text += "email invalide\n";
                case PASSWORD:
                    text += "le mot de passe doit contenir 6 caractères minimum\n";

            }
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }
}