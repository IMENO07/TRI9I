package com.example.tri9itest;

import com.google.gson.annotations.SerializedName;

public class TripPlaces {
    @SerializedName("id")
    private String id;
    @SerializedName("places")
    private int places;

    public TripPlaces(String id,int places) {
        this.id = id;
        this.places=places;
    }
}
