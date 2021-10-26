package com.kaspiotr.shifumi;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioGroup;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class SignUpFragmentOne extends Fragment implements View.OnClickListener {

    Button continue_button;
    RadioGroup radioGroup;
    TextInputEditText name_input, surname_input, precise_input, birthdate_input;
    TextInputLayout l;

    String gender = "male";


    //2 - Declare callback
    private OnButtonClickedListener mCallback;

    // 1 - Declare our interface that will be implemented by any container activity
    public interface OnButtonClickedListener {
        public void onContinueClicked(View view, ArrayList<String> msg);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initComponent(){
        name_input =  view.findViewById(R.id.name_text_input);
        surname_input =  view.findViewById(R.id.surname_text_input);
        birthdate_input = view.findViewById(R.id.birthdate_text_input);
        l = view.findViewById(R.id.birthdate_text_layout);
        birthdate_input.setOnTouchListener((view, motionEvent) -> {
            if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                MaterialDatePicker datePicker =
                        MaterialDatePicker.Builder.datePicker()
                                .setTitleText("Select date")
                                .setSelection(813319810000L)
                                .build();

                datePicker.show(getParentFragmentManager(), datePicker.toString());
                datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
                    @Override
                    public void onPositiveButtonClick(Object selection) {
                        String epoch = selection.toString();
                        long l = Long.parseLong(epoch);
                        Date date = new Date(l);
                        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                        String strDate = format.format(date);
                        birthdate_input.setText(strDate);
                    }
                });

            }

            return false;
        });
        radioGroup = view.findViewById(R.id.radio_group);
        radioGroup.check(R.id.radio_button_male);
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            // checkedId is the RadioButton selected
            switch(checkedId) {
                case(R.id.radio_button_male):
                    gender = "male";
                    break;
                case(R.id.radio_button_female):
                    gender = "female";
                    break;
            }
        });
        precise_input =  view.findViewById(R.id.other_sex_text_input);
        continue_button = view.findViewById(R.id.continue_button);
        continue_button.setOnClickListener(this);


    }

    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_sign_up_one, container, false);

        initDatePicker();
        initComponent();

        return view;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // 4 - Call the method that creating callback after being attached to parent activity
        this.createCallbackToParentActivity();
    }

    private void initDatePicker(){
        DatePickerDialog.OnDateSetListener dataSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = day + " " + month + " " + year;
                birthdate_input.setText(date);
            }
        };

        //datePickerDialog = new DatePickerDialog(this.getContext());

    }

    @Override
    public void onClick(View view) {

        String name = name_input.getText().toString();
        String surname = surname_input.getText().toString();

        String birthdate = birthdate_input.getText().toString();

        ArrayList<String> args = new ArrayList<String>();
        args.add(name);
        args.add(surname);
        args.add(birthdate);
        args.add(gender);
        mCallback.onContinueClicked(view, args);
    }

    private void createCallbackToParentActivity(){
        try{
            mCallback = (OnButtonClickedListener) getActivity();

        } catch (ClassCastException e){
            throw new ClassCastException(e.toString() + " must implement OnButtonClickedListener");
        }
    }
}