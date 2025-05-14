package com.example.myapplication;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DailyWeather implements Serializable {
    @SerializedName("startTime")
    public String startTime;

    @SerializedName("values")
    public CurrentWeather values;
}
