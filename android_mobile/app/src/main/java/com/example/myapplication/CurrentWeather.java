package com.example.myapplication;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CurrentWeather implements Serializable {
    @SerializedName("cloudCover")
    public double cloudCover;

    @SerializedName("humidity")
    public double humidity;

    @SerializedName("moonPhase")
    public int moonPhase;

    @SerializedName("precipitationProbability")
    public int precipitationProbability;

    @SerializedName("pressureSeaLevel")
    public double pressureSeaLevel;

    @SerializedName("sunriseTime")
    public String sunriseTime;

    @SerializedName("sunsetTime")
    public String sunsetTime;

    @SerializedName("temperature")
    public double temperature;

    @SerializedName("temperatureMin")
    public double temperatureMin;

    @SerializedName("temperatureMax")
    public double temperatureMax;

    @SerializedName("uvIndex")
    public int uvIndex;

    @SerializedName("visibility")
    public double visibility;

    @SerializedName("weatherCode")
    public int weatherCode;

    @SerializedName("windDirection")
    public double windDirection;

    @SerializedName("windSpeed")
    public double windSpeed;
}
