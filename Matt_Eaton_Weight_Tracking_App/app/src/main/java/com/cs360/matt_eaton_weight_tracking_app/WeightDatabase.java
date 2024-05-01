// Matt Eaton
// CS-360
// 10/16/22
// This App tracks the users weight using SQLite


package com.cs360.matt_eaton_weight_tracking_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WeightDatabase extends SQLiteOpenHelper {

    // database name and version number
    private static final String DATABASE_NAME = "weights.db";
    private static final int VERSION = 1;
    private static WeightDatabase appDatabase;

    // user table that contains usernames and passwords
    public static final class UserTable{
        private static final String TABLE = "Logins";
        private static final String COL_ID = "_id";
        private static final String COL_USERNAME = "Username";
        private static final String COL_PASSWORD = "Password";
    }

    // weight table that contains logged weights
    public static final class weightsTable {
        private static final String TABLE = "WeightLog";
        private static final String COL_ID = "_id";
        private static final String COL_WEIGHT = "Weight";
        private static final String COL_DATE = "Date";
    }

    // goal table that contains the goal set by the user
    public static final class GoalTable{
        private static final String TABLE = "WeightGoal";
        private static final String COL_ID = "_id";
        private static final String COL_GOAL = "Goal";
    }

    // return database if the database is null create a new one with context
    public static WeightDatabase getDatabase(Context context) {
        if (appDatabase == null) {
            appDatabase = new WeightDatabase(context);
        }
        return appDatabase;
    }

    public WeightDatabase(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    // create the tables inside the database
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + UserTable.TABLE + " (" +
                UserTable.COL_ID + " integer primary key autoincrement, " +
                UserTable.COL_USERNAME + " text," +
                UserTable.COL_PASSWORD + " text)");

        db.execSQL("create table " + weightsTable.TABLE + " (" +
                weightsTable.COL_ID + " integer primary key autoincrement, " +
                weightsTable.COL_WEIGHT + " text," +
                weightsTable.COL_DATE + " text)");

        db.execSQL("create table " + GoalTable.TABLE + " (" +
                GoalTable.COL_ID + " integer primary key autoincrement, " +
                GoalTable.COL_GOAL + " text)");
    }

    // if the database is updated then delete the old ones so it can be replaced with the new ones
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + UserTable.TABLE);
        db.execSQL("drop table if exists " + weightsTable.TABLE);
        db.execSQL("drop table if exists " + GoalTable.TABLE);
        onCreate(db);
    }

    public void onOpen(SQLiteDatabase db) {
        // not sure what to put here to be honest
    }

    // this adds a user to the database returns true if successful and false if not
    public boolean addUser (User user) {
        // get the database and the values
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        // try to add the user and password return true if it throws no errors
        try {
            values.put(UserTable.COL_USERNAME, user.getUserName());
            values.put(UserTable.COL_PASSWORD, user.getPassword());
            long id = db.insert(UserTable.TABLE, null, values);
            user.setId(id);
            return true;
        }
        // return false if an error occurred
        catch (Exception e){
            return false;
        }
    }

    // check the database for the user
    public boolean checkForUser (String username, String password) {
        SQLiteDatabase db = getWritableDatabase();
        int nameCount = 0;
        // get the user name and password then use a cursor object to count the occurrence if it is
        // not zero then it is in the database
        String userCombo = "select * from " + UserTable.TABLE + " where " + UserTable.COL_USERNAME +
                " = ? and " + UserTable.COL_PASSWORD + " = ?";
        Cursor c = db.rawQuery(userCombo, new String[]{username, password});
        nameCount = c.getCount();
        if (nameCount != 0) {
            c.close();
            return true;
        }
        else {
            c.close();
            return false;
        }
    }

    // update username and password by getting it from the user object
    public void updateUser(User user) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UserTable.COL_USERNAME, user.getUserName());
        values.put(UserTable.COL_PASSWORD, user.getPassword());
        db.update(UserTable.TABLE, values, UserTable.COL_USERNAME + " = ?"
                , new String[]{user.getUserName()});
        db.update(UserTable.TABLE, values, UserTable.COL_PASSWORD + " = ?"
                , new String[]{user.getPassword()});
    }

    // update goal weight, this will also be used to initially set the goal weight
    public boolean updateGoal (String goal) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        try {
            values.put(GoalTable.COL_GOAL, goal);
            db.update(GoalTable.TABLE, values, GoalTable.COL_ID + " = ?"
                    , new String[]{"1"});
            db.insert(GoalTable.TABLE, null,values);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    // get the goal and return it
    public String getGoal() {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        String goal = "0";
        String goalValue = "select * from " + GoalTable.TABLE;
        Cursor c = db.rawQuery(goalValue, null);
        if (c.moveToFirst()) {
            goal = c.getString(1);
        }
        return goal;
    }

    // get the current date and return it as a string
    // this is used when a user adds a new weight
    public String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yy\nHH:mm:ss", Locale.getDefault());
        String date = sdf.format(new Date());
        return date;
    }

    // add a new weight to the database return true if no errors else return false
    public boolean addWeight (String weight) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        try {
            values.put(weightsTable.COL_WEIGHT, weight);
            values.put(weightsTable.COL_DATE, getCurrentDate());
            db.insert(weightsTable.TABLE, null, values);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    // update the weight in the table to the corresponding entry
    // the dates are used as the whereClause since they are unique
    public boolean updateWeight(Weight entry, String newWeight) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        try {
            if (!newWeight.isEmpty()) {
                values.put(weightsTable.COL_WEIGHT, newWeight);
                db.update(weightsTable.TABLE, values, weightsTable.COL_DATE + " = ?", new String[]{entry.getDate()});
            }
            return true;
        }
        catch (Exception e) {
            return false;
        }

    }
    // get all weights as a list and return the list
    public List<Weight> getAllWeights() {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        List<Weight> allWeights = new ArrayList<>();

        String DBWeight = "select * from " + weightsTable.TABLE + " order by "
                + weightsTable.COL_DATE + " DESC";
        Cursor c = db.rawQuery(DBWeight, null);
        // if there is an item get it and add it to the list
        if (c.moveToFirst()) {
            do {
                Weight weightEntry = new Weight();
                weightEntry.setWeight(c.getString(1));
                weightEntry.setDate(c.getString(2));
                allWeights.add(weightEntry);
            }
            while (c.moveToNext());
            }
        // close cursor and return the list
        c.close();
        return allWeights;
    }

    // delete a weight using the date as a reference
    public void deleteWeightEntry( Weight entry) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        db.delete(weightsTable.TABLE, weightsTable.COL_DATE + " = ?"
                , new String[]{entry.getDate()});

        //I was messing with deleting the entire database
        //db.delete("delete * from " + WeightTable.TABLE, null , null);
    }
}
