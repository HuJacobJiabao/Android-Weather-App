package com.example.myapplication;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface LocationApi {
    @GET("get_location")
    Call<LocationResponse> getLocation(
            @Query("street") String street,
            @Query("city") String city,
            @Query("state") String state
    );
}
