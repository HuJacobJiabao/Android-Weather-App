package com.example.myapplication;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IPinfoApi {
    @GET("/")
    Call<IPinfoResponse> getIPinfo(@Query("token") String token);
}

