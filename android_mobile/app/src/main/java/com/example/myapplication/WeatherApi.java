package com.example.myapplication;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApi {
    @GET("/get_weather")
    Call<WeatherResponse> getWeather(@Query("lat") double lat, @Query("lng") double lng);
}
