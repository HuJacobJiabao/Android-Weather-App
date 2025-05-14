package com.example.myapplication;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class FavoriteCity implements Serializable {
    @SerializedName("_id")
    private String id;
    @SerializedName("city")
    public String city;
    @SerializedName("state")
    public String state;
    @SerializedName("lat")
    public double lat;
    @SerializedName("lng")
    public double lng;

    public FavoriteCity(){}

    public FavoriteCity(String id, String city, String state, double lat, double lng) {
        this.id = id;
        this.city = city;
        this.state = state;
        this.lat = lat;
        this.lng = lng;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

}
