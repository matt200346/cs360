// Matt Eaton
// CS-360
// 10/16/22
// This App tracks the users weight using SQLite


package com.cs360.matt_eaton_weight_tracking_app;

public class User {
    private String userName;
    private long id;
    private String password;

    // username and password variables
    public User (String name, String pass) {
        userName = name;
        password = pass;
    }

    // ---------getters----------------

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public long getId() {
        return id;
    }

    //---------setters------------------

    public void setId(long id) {
        this.id = id;
    }
}
