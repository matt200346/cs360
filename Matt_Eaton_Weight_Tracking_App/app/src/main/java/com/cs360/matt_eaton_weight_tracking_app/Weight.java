// Matt Eaton
// CS-360
// 10/16/22
// This App tracks the users weight using SQLite


package com.cs360.matt_eaton_weight_tracking_app;

// this object will store the weight and date of an entry in the database
// I am using this to display it easier
public class Weight {
    // not sure if I will need the id or not
    // *future Matt here* I did not need the id, but I will keep it in in case it has a future use
    // when I decide to come back to this project and mess with it more
    private long id;
    private String date;
    private String weight;

    public Weight() {
        id = 0;
    }

    public Weight(String weight) {
        this.weight = weight;
        id = 0;
    }
    // -----------------setters-----------------
    public void setId(long id) {
        this.id = id;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public void setDate(String date) {
        this.date = date;
    }

    // ----------------getters---------------------

    public String getDate() {
        return date;
    }

    public String getWeight() {
        return weight;
    }

    public Long getId() {
        return id;
    }
}
