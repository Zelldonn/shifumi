package com.kaspiotr.shifumi;


import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

public class User {
    private String name, surname, gender, birthdate, id, password, email;

    static public enum SIGN_UP_STATE {
        EMAIL, NAME, SURNAME, BIRTHDATE, GENDER, OK, PASSWORD
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getGender() {
        return gender;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    static public Boolean isValidDate(String date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(date.trim());
        } catch (ParseException pe) {
            return false;
        }
        return true;
    }

    static public Boolean isValidEmail(String email){
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

    public SIGN_UP_STATE partOneState() {

        if(name.isEmpty())
            return SIGN_UP_STATE.NAME;
        else if(surname.isEmpty())
            return SIGN_UP_STATE.SURNAME;
        else if(!isValidDate(birthdate))
            return SIGN_UP_STATE.BIRTHDATE;
        else if(gender.isEmpty()){
            return SIGN_UP_STATE.GENDER;
        }
        return SIGN_UP_STATE.OK;
    }
    public SIGN_UP_STATE partTwoState(){

        if(!isValidEmail(email))
            return SIGN_UP_STATE.EMAIL;
        else if(password.length() < 6)
            return SIGN_UP_STATE.PASSWORD;
        return  SIGN_UP_STATE.OK;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void printInfo(){
        String info = name + " " + surname + " " + gender + " " + birthdate;
        Log.i("MY USER", info);
    }
}
