// Matt Eaton
// CS-360
// 10/16/22
// This App tracks the users weight using SQLite


package com.cs360.matt_eaton_weight_tracking_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Login extends AppCompatActivity {
    private EditText username;
    private EditText password;
    private WeightDatabase mAppDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);
        username = findViewById(R.id.userNameTextField);
        password = findViewById(R.id.editTextPassword);
        mAppDatabase = WeightDatabase.getDatabase(getApplicationContext());
    }

    // validate user information
    public boolean validateUser(String username, String password){
        if (mAppDatabase.checkForUser(username, password)) {
            Toast.makeText(getApplicationContext()
                    , "Welcome back " + username + " !"
                    , Toast.LENGTH_SHORT).show();
            return true;
        }
        else {
            Toast.makeText(getApplicationContext(), username +
                    " either does not have an account or the password is incorrect. Press create" +
                            " to make a new account"
                    , Toast.LENGTH_LONG).show();
            return false;
        }
    }

    // add a new user
    public boolean addNewUser(String username, String password) {
        if (mAppDatabase.checkForUser(username, password)) {
            Toast.makeText(getApplicationContext(), "User already exists!"
                    , Toast.LENGTH_LONG).show();
            return false;
        }
        else {
            User newUser = new User(username, password);
            if (mAppDatabase.addUser(newUser)) {
                Toast.makeText(getApplicationContext(), "user added!"
                        , Toast.LENGTH_LONG).show();
                return true;
            }
            else {
                Toast.makeText(getApplicationContext(), "Failed to add user!"
                        , Toast.LENGTH_LONG).show();
                return false;
            }
        }
    }

    public void login (android.view.View view) {
        String user = username.getText().toString();
        String pass = password.getText().toString();
        if (validateUser(user, pass)) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    public void onLoginButtonClick(View view) {
        login(view);
    }

    public void onCreateButtonClick(View view) {
        String user = username.getText().toString();
        String pass = password.getText().toString();
        addNewUser(user, pass);
    }
}
