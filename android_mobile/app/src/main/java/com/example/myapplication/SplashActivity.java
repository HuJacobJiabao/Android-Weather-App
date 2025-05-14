package com.example.myapplication;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SplashActivity extends AppCompatActivity {
    String ipLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the custom layout for the splash screen
        setContentView(R.layout.activity_splash);
        // Initialize Retrofit
        fetchLocationFromIPinfo();
    }


    private void fetchLocationFromIPinfo() {
        // Base URL for the IPinfo API
        String BASE_URL = "https://ipinfo.io/";
        String TOKEN = "f57139cd172789";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        IPinfoApi ipinfoApi = retrofit.create(IPinfoApi.class);

        // Make the API call
        ipinfoApi.getIPinfo(TOKEN).enqueue(new retrofit2.Callback<IPinfoResponse>() {
            @Override
            public void onResponse(Call<IPinfoResponse> call, retrofit2.Response<IPinfoResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    IPinfoResponse ipinfo = response.body();

                    // Parse the location (lat, lng)
                    String[] locParts = ipinfo.loc.split(",");
                    double lat = Double.parseDouble(locParts[0]);
                    double lng = Double.parseDouble(locParts[1]);

                    ipLocation = ipinfo.city + ", " + ipinfo.state;

                    // Log the location information
                    Log.d("IPinfo", "City: " + ipinfo.city + ", State: " + ipinfo.state);
                    Log.d("IPinfo", "Latitude: " + lat + ", Longitude: " + lng);

                    // Use the lat and lng to fetch weather data
                    fetchWeatherData(lat, lng);
                } else {
                    Log.e("IPinfo", "Failed to fetch IPinfo: " + response.message());
                    navigateToMainActivity(null); // Handle failure
                }
            }

            @Override
            public void onFailure(Call<IPinfoResponse> call, Throwable t) {
                Log.e("IPinfo", "Error: " + t.getMessage());
                navigateToMainActivity(null); // Handle failure
            }
        });
    }




    private void fetchWeatherData(double lat, double lng) {
        WeatherApi weatherApi = RetrofitClient.getInstance().create(WeatherApi.class);
        weatherApi.getWeather(lat, lng).enqueue(new retrofit2.Callback<WeatherResponse>() {
            @Override
            public void onResponse(retrofit2.Call<WeatherResponse> call, retrofit2.Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weatherData = response.body();
                    Log.d("Weather API", "onResponse: success");
                    navigateToMainActivity(weatherData); // Pass data to MainActivity
                } else {
                    Log.d("Weather API", "onResponse: fail");
                    navigateToMainActivity(null); // Handle error
                }
            }

            @Override
            public void onFailure(retrofit2.Call<WeatherResponse> call, Throwable t) {
                Log.d("Weather API", "onResponse: gg");
                navigateToMainActivity(null); // Handle failure
            }
        });
    }


    private void navigateToMainActivity(WeatherResponse weatherData) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            if (weatherData != null) {
                // Pass the weather data safely
                intent.putExtra("weatherData", weatherData);
                intent.putExtra("ipLocation", ipLocation);
            }

            startActivity(intent);
            finish();
        }, 3000); // 3-second delay
    }

}
