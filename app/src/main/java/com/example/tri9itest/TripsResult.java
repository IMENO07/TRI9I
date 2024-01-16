package com.example.tri9itest;
import com.google.gson.annotations.SerializedName;

public class TripsResult {
    @SerializedName("id")
    String id;
    @SerializedName("idCar")
    String idCar;
    @SerializedName("departure")
    String departure;
    @SerializedName("destination")
    String destination;
    @SerializedName("time")
    String time;
    @SerializedName("condition")
    String condition;
    @SerializedName("places")
    int places;
    @SerializedName("idCreator")
    String idCreator;


    public String getId() {
        return id;
    }

    public int getPlaces() {
        return places;
    }
    public String getIdCar() {
        return idCar;
    }

    public String getDeparture() {
        return departure;
    }

    public String getDestination() {
        return destination;
    }

    public String getTime() {
        return time;
    }
    public String getCondition() {
        return condition;
    }
    public String getIdCreator() {
        return idCreator;
    }

    public TripsResult(String departure, String destination, String time, String condition,String idCar,int places,String idCreator) {
        this.departure = departure;
        this.destination = destination;
        this.time = time;
        this.condition = condition;
        this.idCar = idCar;
        this.places=places;
        this.idCreator=idCreator;
    }
}