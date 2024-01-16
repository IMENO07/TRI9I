package com.example.tri9itest;

import com.google.gson.annotations.SerializedName;

public class TripCreator {
    @SerializedName("idCreator")
    public String idCreator;
    public TripCreator(String idCreator) {
        this.idCreator = idCreator;


    }
}
