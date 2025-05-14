package com.example.myapplication;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class IPinfoResponse implements Serializable {
    @SerializedName("city")
    public String city;

    @SerializedName("region")
    public String state;

    @SerializedName("loc")
    public String loc; // Latitude and Longitude as a single string
}

