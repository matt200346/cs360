// Matt Eaton
// CS-360
// 10/16/22
// This App tracks the users weight using SQLite


package com.cs360.matt_eaton_weight_tracking_app;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class user_settings extends AppCompatActivity {

    // variables and objects
    private EditText newGoalWeight;
    private EditText newUsername;
    private EditText newPassword;
    private ToggleButton smsToggle;
    private User updatedUser;
    WeightDatabase mAppDatabase;
    private final int REQUEST_WRITE_CODE = 0;
    private String smsPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_settings_screen);
        // set objects initially
        mAppDatabase = WeightDatabase.getDatabase(getApplicationContext());
        newGoalWeight = findViewById(R.id.goalSettingsEditTextField);
        newUsername = findViewById(R.id.usernameSettingsEditTextField);
        newPassword = findViewById(R.id.passwordSettingsEditTextField);

        // check if sms permission are allowed and set the toggle accordingly
        smsPermission = Manifest.permission.SEND_SMS;
        ToggleButton toggle = findViewById(R.id.smsPermissionToggle);
        if (ContextCompat.checkSelfPermission(getApplicationContext(), smsPermission) !=PackageManager.PERMISSION_GRANTED) {
            toggle.setChecked(false);
        }
        else {
            toggle.setChecked(true);
        }
        // create a listener to prompt the user for the permissions when it is checked
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    requestPermissions();
                    }
            }
        });
    }

    // callback for the submit button
    public void onSubmitButtonClick(View view) {
         try {
             // if the goal weight is not empty update it
             if (!newGoalWeight.getText().toString().isEmpty()) {
                 mAppDatabase.updateGoal(newGoalWeight.getText().toString());
             }
             // if the username or password fields are not empty
             if (!newUsername.getText().toString().isEmpty() ||
                     !newPassword.getText().toString().isEmpty()) {
                 // if the password field is empty tell the user to enter both
                 if (newPassword.getText().toString().isEmpty()) {
                     Toast.makeText(getApplicationContext()
                             , "Please Enter a password for the new username"
                             , Toast.LENGTH_SHORT).show();
                 }
                 // if the username is empty tell the user to enter both
                 else if (newUsername.getText().toString().isEmpty()) {
                     Toast.makeText(getApplicationContext()
                             , "Please Enter your username with the new password"
                             , Toast.LENGTH_SHORT).show();
                 }
                 // change the username and password
                 else {
                     String user = newUsername.getText().toString();
                     String pass = newPassword.getText().toString();
                     updatedUser = new User(user, pass);
                     mAppDatabase.updateUser(updatedUser);
                 }

             }
             // notify the user that the values where applied
             Toast.makeText(getApplicationContext(), "new values applied!"
                     , Toast.LENGTH_SHORT).show();
         }
         catch (Exception e) {

         }
    }
    // request sms permissions
    public void requestPermissions () {
        ActivityCompat.requestPermissions(this, new String[] {smsPermission}, REQUEST_WRITE_CODE);
    }
    
}
