package com.example.tri9itest;

import com.google.gson.annotations.SerializedName;

public class Reservation {


    public String getIdUser() {return idUser;}
    public String getIdTrip() {
        return idTrip;
    }
    public int getPlacesReservation() {
        return places;
    }

    public String getIdReservation() {return id;}



        @SerializedName("idTrip")
        public String idTrip;

        @SerializedName("places")
        public int places;
        @SerializedName("idUser")
         public String idUser;
        @SerializedName("id")
        public String id;

        public Reservation(String idTrip,int places,String idUser,String id) {
            this.idUser = idUser;
            this.idTrip=idTrip;
            this.places=places;
            this.id=id;

        }
}
