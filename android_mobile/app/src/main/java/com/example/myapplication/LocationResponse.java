package com.example.myapplication;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class LocationResponse implements Serializable {
    @SerializedName("latitude")
    public double latitude;
    @SerializedName("longitude")
    public double longitude;
    @SerializedName("formattedAddress")
    public String formattedAddress;
}

