package com.example.tri9itest;

import com.google.gson.annotations.SerializedName;

public class ReservationIdUser {
    @SerializedName("idUser")
    private String idUser;
    public ReservationIdUser(String idUser) {
        this.idUser=idUser;
    }
}
