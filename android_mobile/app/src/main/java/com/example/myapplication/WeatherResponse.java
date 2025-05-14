package com.example.myapplication;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class WeatherResponse implements Serializable {
    @SerializedName("current")
    public CurrentWeather current;

    @SerializedName("1d")
    public List<DailyWeather> daily;
}

