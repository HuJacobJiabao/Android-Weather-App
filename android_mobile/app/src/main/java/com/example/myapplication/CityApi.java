package com.example.myapplication;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CityApi {

    /**
     * API endpoint to get city suggestions for autocomplete
     *
     * @param query User's search query
     * @return A list of city suggestions
     */
    @GET("api/place/autocomplete") // Replace "autocomplete" with your actual endpoint
    Call<CitySuggestionResponse> getCitySuggestions(@Query("input") String query);

}
