// Matt Eaton
// CS-360
// 10/16/22
// This App tracks the users weight using SQLite
// oh man this took so much work I need a nap after this I can hear my bed calling me it is
//  telling me to jump into it's soft embrace


package com.cs360.matt_eaton_weight_tracking_app;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    // variables and objects
    private int weightsListCount;
    private String weightInput;
    private boolean goalNotified;

    private WeightDatabase mAppDatabase;
    private TextView goalWeight;
    private Handler mHandler;
    private Weight tempWeightObj;
    private Weight tempDeleteWeightObj;
    private Weight tempEditWeightObj;
    private List<Weight> weightsList;
    private TextView weight1;
    private TextView date1;
    private TextView weight2;
    private TextView date2;
    private TextView weight3;
    private TextView date3;
    private TextView weight4;
    private TextView date4;
    private TextView weight5;
    private TextView date5;
    private TextView goalReached;

    private Button addWeightButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);
        // get the database
        mAppDatabase = WeightDatabase.getDatabase(getApplicationContext());
        // temp empty Weight object
        tempWeightObj = new Weight();
        goalNotified = false;

        //check sms permissions notify user if they are not on
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED)
        {
            Toast.makeText(getApplicationContext(), "sms permissions not granted"
                    ,Toast.LENGTH_SHORT).show();
        }

        //------------text boxes------------------
        goalWeight = findViewById(R.id.goalTextView);
        weight1 = findViewById(R.id.weight1);
        date1 = findViewById(R.id.date1);
        weight2 = findViewById(R.id.weight2);
        date2 = findViewById(R.id.date2);
        weight3 = findViewById(R.id.weight3);
        date3 = findViewById(R.id.date3);
        weight4 = findViewById(R.id.weight4);
        date4 = findViewById(R.id.date4);
        weight5 = findViewById(R.id.weight5);
        date5 = findViewById(R.id.date5);
        goalReached = findViewById(R.id.goalCongrats);
        //------------end text boxes--------------

        //Add new weight button
        addWeightButton = findViewById(R.id.newWeightButton);

        // handler to handle updating the ui
        mHandler = new Handler();
        mHandler.post(mUpdateUI);

        // initial update for the UI
        if (goalWeight.getText().toString().isEmpty()) {
            if (mAppDatabase.getGoal().isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please set a goal in settings"
                        ,Toast.LENGTH_SHORT).show();
            }
        else {
                goalWeight.setText(mAppDatabase.getGoal());
            }
        }
    }

    // runnable that updates the UI----------------------------------------
    private Runnable mUpdateUI = new Runnable() {

        @Override
        public void run() {
            // get all the weights and the size of the list
            weightsList = mAppDatabase.getAllWeights();
            weightsListCount = weightsList.size();

            // set goal weight
            goalWeight.setText(mAppDatabase.getGoal());

            //check if goal weight has been reached
            if (weightsListCount >= 1) {
                // local variables to check if goal has been reached
                int currentWeight = Integer.parseInt(weightsList.get(0).getWeight());
                int goalWeightInt = Integer.parseInt(mAppDatabase.getGoal());
                if(currentWeight <= goalWeightInt) {
                    // set text at the bottom to notify the user that the gaol has been reached
                    goalReached.setText("GOAL REACHED!");

                    // notifies the user if they have reached the goal
                    // first with an sms, then with a notification
                    // this will only activate if the goal has not been reached
                    if (!goalNotified) {
                        smsNotify();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        ShowNotif(getApplicationContext(), intent, 1);
                        goalNotified = true;
                    }
                }
                // reset goal being reached if the user gains weight
                else {
                    goalReached.setText("");
                    goalNotified = false;
                }
            }

            // set all of the weights and dates on the chart when there are 5 or more
            if (weightsListCount >= 5) {
                for (int i = 0; i < 5; i++) {
                    tempWeightObj = weightsList.get(i);
                    switch (i) {
                        case 0:
                            weight1.setText(tempWeightObj.getWeight());
                            date1.setText(tempWeightObj.getDate());
                            break;
                        case 1:
                            weight2.setText(tempWeightObj.getWeight());
                            date2.setText(tempWeightObj.getDate());
                            break;
                        case 2:
                            weight3.setText(tempWeightObj.getWeight());
                            date3.setText(tempWeightObj.getDate());
                            break;
                        case 3:
                            weight4.setText(tempWeightObj.getWeight());
                            date4.setText(tempWeightObj.getDate());
                            break;
                        case 4:
                            weight5.setText(tempWeightObj.getWeight());
                            date5.setText(tempWeightObj.getDate());
                            break;

                    }
                }
                // set all of the weights and dates on the chart when there are less than 5
            } else {
                for (int i = 0; i < weightsListCount; i++) {
                    tempWeightObj = weightsList.get(i);
                    switch (i) {
                        case 0:
                            weight1.setText(tempWeightObj.getWeight());
                            date1.setText(tempWeightObj.getDate());
                            break;
                        case 1:
                            weight2.setText(tempWeightObj.getWeight());
                            date2.setText(tempWeightObj.getDate());
                            break;
                        case 2:
                            weight3.setText(tempWeightObj.getWeight());
                            date3.setText(tempWeightObj.getDate());
                            break;
                        case 3:
                            weight4.setText(tempWeightObj.getWeight());
                            date4.setText(tempWeightObj.getDate());
                            break;
                    }
                }
                // these if statements remove old entries from the chart after they are deleted
                if (weightsListCount <= 0) {
                    weight1.setText("");
                    date1.setText("");
                }
                else if (weightsListCount == 1) {
                    weight2.setText("");
                    date2.setText("");
                }
                else if (weightsListCount == 2) {
                    weight3.setText("");
                    date3.setText("");
                }
                else if (weightsListCount == 3) {
                    weight4.setText("");
                    date4.setText("");
                }
                else if (weightsListCount == 4) {
                    weight5.setText("");
                    date5.setText("");
                }
            }
            // update interval
            mHandler.postDelayed(this, 200);
        }
    };
//------------------------end of the runnable------------------------


    // clicking the menu button
    public void onMenuButtonClick(View view) {
        Intent intent = new Intent(this, user_settings.class);
        startActivity(intent);
    }

    // method called when the new weight button is clicked
    public void onNewWeightClick(View view) {
        // create a new AlertDialog builder
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Enter a new Weight");
        final EditText newWeight = new EditText(this);
        newWeight.setInputType(InputType.TYPE_CLASS_NUMBER);
        alert.setView(newWeight);

        // set the ok and cancel button
        alert.setPositiveButton("Add New Weight", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // add the weight to the database
                weightInput = newWeight.getText().toString();
                mAppDatabase.addWeight(weightInput);
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        alert.show();
    }

    //-------------- delete button callbacks----------------
    public void onDeleteButton1Click(View view) {
        if (weightsListCount >= 1) {
            tempDeleteWeightObj = weightsList.get(0);
            weightsList.remove(0);
            mAppDatabase.deleteWeightEntry(tempDeleteWeightObj);
        }
    }
    public void onDeleteButton2Click(View view) {
        if (weightsListCount >= 2) {
            tempDeleteWeightObj = weightsList.get(1);
            weightsList.remove(1);
            mAppDatabase.deleteWeightEntry(tempDeleteWeightObj);
        }
    }
    public void onDeleteButton3Click(View view) {
        if (weightsListCount >= 3) {
            tempDeleteWeightObj = weightsList.get(2);
            weightsList.remove(2);
            mAppDatabase.deleteWeightEntry(tempDeleteWeightObj);
        }
    }
    public void onDeleteButton4Click(View view) {
        if (weightsListCount >= 4) {
            tempDeleteWeightObj = weightsList.get(3);
            weightsList.remove(3);
            mAppDatabase.deleteWeightEntry(tempDeleteWeightObj);
        }
    }
    public void onDeleteButton5Click(View view) {
        if (weightsListCount >= 5) {
            tempDeleteWeightObj = weightsList.get(4);
            weightsList.remove(4);
            mAppDatabase.deleteWeightEntry(tempDeleteWeightObj);
        }
    }
    //-----------------------end delete button callbacks-------------------

    //---------------------start edit button callbacks-------------------------
    public void onEditButton1Click(View view) {
        if (weightsListCount >= 1) {
            tempEditWeightObj = weightsList.get(0);
            // create a new AlertDialog builder
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Edit Weight");
            final EditText newWeight = new EditText(this);
            newWeight.setInputType(InputType.TYPE_CLASS_NUMBER);
            alert.setView(newWeight);


            // set the ok and cancel button
            alert.setPositiveButton("Enter New Edited Weight", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    weightInput = newWeight.getText().toString();
                    mAppDatabase.updateWeight(tempEditWeightObj, weightInput);
                    ;
                }
            });
            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            alert.show();
        }
    }

    public void onEditButton2Click(View view) {
        if (weightsListCount >= 2) {
            tempEditWeightObj = weightsList.get(1);
            // create a new AlertDialog builder
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Edit Weight");
            final EditText newWeight = new EditText(this);
            newWeight.setInputType(InputType.TYPE_CLASS_NUMBER);
            alert.setView(newWeight);

            // set the ok and cancel button
            alert.setPositiveButton("Enter New Edited Weight", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    weightInput = newWeight.getText().toString();
                    mAppDatabase.updateWeight(tempEditWeightObj, weightInput);
                    ;
                }
            });
            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            alert.show();
        }
    }

    public void onEditButton3Click(View view) {
        if (weightsListCount >= 3) {
            tempEditWeightObj = weightsList.get(2);
            // create a new AlertDialog builder
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Edit Weight");
            final EditText newWeight = new EditText(this);
            newWeight.setInputType(InputType.TYPE_CLASS_NUMBER);
            alert.setView(newWeight);

            // set the ok and cancel button
            alert.setPositiveButton("Enter New Edited Weight", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    weightInput = newWeight.getText().toString();
                    mAppDatabase.updateWeight(tempEditWeightObj, weightInput);
                    ;
                }
            });
            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            alert.show();
        }
    }

    public void onEditButton4Click(View view) {
        if (weightsListCount >= 4) {
            tempEditWeightObj = weightsList.get(3);
            // create a new AlertDialog builder
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Edit Weight");
            final EditText newWeight = new EditText(this);
            newWeight.setInputType(InputType.TYPE_CLASS_NUMBER);
            alert.setView(newWeight);

            // set the ok and cancel button
            alert.setPositiveButton("Enter New Edited Weight", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    weightInput = newWeight.getText().toString();
                    mAppDatabase.updateWeight(tempEditWeightObj, weightInput);
                    ;
                }
            });
            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            alert.show();
        }
    }

    public void onEditButton5Click(View view) {
        if (weightsListCount >= 5) {
            tempEditWeightObj = weightsList.get(4);
            // create a new AlertDialog builder
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Edit Weight");
            final EditText newWeight = new EditText(this);
            newWeight.setInputType(InputType.TYPE_CLASS_NUMBER);
            alert.setView(newWeight);

            // set the ok and cancel button
            alert.setPositiveButton("Enter New Edited Weight", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    weightInput = newWeight.getText().toString();
                    mAppDatabase.updateWeight(tempEditWeightObj, weightInput);
                    ;
                }
            });
            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            alert.show();
        }
    }
    //-----------------------------end edit button callbacks-----------------------

    // send a text message that the goal was reached
    public void smsNotify() {
        //TelephonyManager tMgr =(TelephonyManager)getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        //String number = tMgr.getLine1Number();

        SmsManager smsManager = SmsManager.getDefault();
        // I am sending it to the number that my emulated device has
        // getting the number from the device like above requires a lot of permissions
        smsManager.sendTextMessage("5555215554", null,
               "Weight goal reached!", null, null);

    }
    public void ShowNotif (Context context, Intent intent, int requestId) {
        // contents of the notification
        String title = "GOAL REACHED!";
        String body = "Congratulations you have reached your weight goal!";
        String CHANNEL_ID = "weightApp";

        PendingIntent pendingIntent = PendingIntent.getActivity(context, requestId, intent
                , PendingIntent.FLAG_ONE_SHOT);
        // create a notification builder with the information
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent);

        // create a notification manager
        NotificationManager NM = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // name user sees
            CharSequence name = "Weight tracking App";
            // set the importance
            int importance = NotificationManager.IMPORTANCE_HIGH;

            // new notification channel to send notification
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            NM.createNotificationChannel(channel);
        }
        // send notification
        NM.notify(requestId, builder.build());
    }
}