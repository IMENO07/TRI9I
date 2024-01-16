package com.example.tri9itest;

import com.google.gson.annotations.SerializedName;

public class CarResult {
    public CarResult( String owner, String brand, String model, int places, int plaque) {
        this.brand = brand;
        this.model = model;
        this.places = places;
        this.plaque = plaque;
        this.owner=owner;
    }

    @SerializedName("id")
    String id;
    @SerializedName("owner")
    String owner;
    @SerializedName("brand")
    String brand;
    @SerializedName("model")
    String model;
    @SerializedName("places")
    int places;
    @SerializedName("plaque")
    int plaque;

    public String getId() {return id;}
    public String getOwner() {return owner;}

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public int getPlaces() {
        return places;
    }

    public int getPlaque() {
        return plaque;
    }

}