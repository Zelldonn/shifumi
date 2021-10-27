package com.kaspiotr.shifumi.SignInUp;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.kaspiotr.shifumi.R;

import java.util.ArrayList;


public class SignUpFragmentTwo extends Fragment implements View.OnClickListener{

    View v;

    TextInputEditText email, pswd, confirm_pswd;

    Button finish_button;

    //2 - Declare callback
    private onFinishClickedListener mCallback;

    // 1 - Declare our interface that will be implemented by any container activity
    public interface onFinishClickedListener {
        public void onFinishClicked(View view, ArrayList<String> msg);
    }


    private void initComponents(){
        email = v.findViewById(R.id.email_text_input);
        pswd = v.findViewById(R.id.pswd_text_input);
        confirm_pswd = v.findViewById(R.id.pswd_confirm_text_input);
        finish_button = v.findViewById(R.id.sign_up_finish_button);
        finish_button.setOnClickListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_sign_up_two, container, false);
        initComponents();
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // 4 - Call the method that creating callback after being attached to parent activity
        this.createCallbackToParentActivity();
    }


    @Override
    public void onClick(View view) {

        String mail = email.getText().toString();
        String psw_1 = pswd.getText().toString();
        String psw_2 = confirm_pswd.getText().toString();

        ArrayList<String> args = new ArrayList<String>();
        args.add(mail);
        args.add(psw_1);
        args.add(psw_2);
        mCallback.onFinishClicked(view, args);
    }

    private void createCallbackToParentActivity(){
        try{
            mCallback = (onFinishClickedListener) getActivity();

        } catch (ClassCastException e){
            throw new ClassCastException(e.toString() + " must implement OnButtonClickedListener");
        }
    }
}