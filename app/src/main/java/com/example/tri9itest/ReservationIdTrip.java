package com.example.tri9itest;

import com.google.gson.annotations.SerializedName;

public class ReservationIdTrip {
        @SerializedName("idTrip")
        private String idTrip;
        public ReservationIdTrip(String idTrip) {
            this.idTrip=idTrip;
        }
    }

