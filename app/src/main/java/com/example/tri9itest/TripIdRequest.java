package com.example.tri9itest;

import com.google.gson.annotations.SerializedName;

public class TripIdRequest {
    @SerializedName("id")
    private String id;

    public TripIdRequest(String id) {
        this.id = id;
    }
}
