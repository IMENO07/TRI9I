package com.example.tri9itest;

import com.google.gson.annotations.SerializedName;

public class ReservationRequest {
    @SerializedName("idUser")
    private String idUser;
    @SerializedName("idTrip")
    private String idTrip;
    @SerializedName("places")
    private int places;

    public ReservationRequest(String idUser,String idTrip ,int places) {
        this.idUser = idUser;
        this.idTrip=idTrip;
        this.places=places;

    }
}
