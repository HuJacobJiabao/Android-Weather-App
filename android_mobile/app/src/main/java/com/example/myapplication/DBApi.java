package com.example.myapplication;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface DBApi {
    @POST("/api/favorites")
    Call<AddFavorResponse> addFavorite(@Body FavoriteCity favoriteCity);

    @DELETE("/api/favorites/{id}")
    Call<Void> deleteFavorite(@Path("id") String id);

    @GET("/api/get_favorites")
    Call<List<FavoriteCity>> getFavorites();
}
