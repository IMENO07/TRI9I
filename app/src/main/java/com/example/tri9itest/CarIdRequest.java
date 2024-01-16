package com.example.tri9itest;

import com.google.gson.annotations.SerializedName;

public class CarIdRequest {
    @SerializedName("id")
    private String id;

    public CarIdRequest(String id) {
        this.id = id;
    }
}
