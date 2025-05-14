package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchWeatherResultActivity extends AppCompatActivity {
    private DBApi dbApi;
    private boolean isFavorite; // Track favorite status
    private String favoriteCityId;      // Store the city ID



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_search);

        // Retrieve data from the intent
        Intent intent = getIntent();
        String formattedAddress = intent.getStringExtra("formattedAddress");
        WeatherResponse weatherData = getIntent().getSerializableExtra("weatherData", WeatherResponse.class);
        double lat = intent.getDoubleExtra("lat", 0);
        double lng = intent.getDoubleExtra("lng", 0);
        isFavorite = intent.getBooleanExtra("isFavorite", false);

        if (weatherData != null) {
            updateUI(weatherData.current, weatherData.daily, formattedAddress);
        } else {
            showErrorUI();
        }

        // Set up the toolbar
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(formattedAddress);
        // Go back to the previous activity

        if (weatherData != null) {
            updateUI(weatherData.current, weatherData.daily, formattedAddress);
        } else {
            showErrorUI();
        }

        CardView card1 = findViewById(R.id.card1);
        card1.setOnClickListener(v -> {
            Intent intent2 = new Intent(SearchWeatherResultActivity.this, WeatherDetailsActivity.class);
            intent2.putExtra("formattedAddress", formattedAddress);
            intent2.putExtra("weatherData", weatherData);
            startActivity(intent2);
        });

        dbApi = RetrofitClient.getInstance().create(DBApi.class);
        String[] parts = formattedAddress.split(",");
        String city = parts[0].trim();
        String state = parts[1].trim();

        FloatingActionButton fab = findViewById(R.id.fab);
        if (isFavorite) {
            fab.setImageResource(R.drawable.map_marker_minus);
            favoriteCityId = intent.getStringExtra("id");
        } else {
            fab.setImageResource(R.drawable.map_marker_plus);
        }
        fab.setOnClickListener(v -> {
            if (isFavorite) {
                // If already favorite, delete it
                deleteFavoriteCity(city, favoriteCityId);
            } else {
                // If not a favorite, add it
                addFavoriteCity(city, state, lat, lng); // Replace with actual data
            }
        });

        ImageView backButton = findViewById(R.id.back_arrow);
        backButton.setOnClickListener(view -> {
            if (isFavorite) { // Check if the city is favorited
                Intent resultIntent = new Intent();
                resultIntent.putExtra("isFavorite", isFavorite);
                resultIntent.putExtra("city", city); // Pass the city name
                resultIntent.putExtra("state", state); // Pass the state
                resultIntent.putExtra("lat", lat); // Pass latitude
                resultIntent.putExtra("lng", lng); // Pass longitude
                resultIntent.putExtra("id", favoriteCityId); // Pass the favorite city ID
                resultIntent.putExtra("weatherResponse", weatherData);
                setResult(RESULT_OK, resultIntent); // Set the result to OK
            } else {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("isFavorite", isFavorite);
                resultIntent.putExtra("city", city); // Pass the city name
                resultIntent.putExtra("state", state); // Pass the state
                resultIntent.putExtra("lat", lat); // Pass latitude
                resultIntent.putExtra("lng", lng); // Pass longitude
                setResult(RESULT_OK, resultIntent); // Set the result to OK
            }
            finish();
        });

    }

    private void addFavoriteCity(String city, String state, double lat, double lng) {
        // Create a FavoriteCity object
        FavoriteCity favoriteCity = new FavoriteCity();
        favoriteCity.setCity(city);
        favoriteCity.setState(state);
        favoriteCity.setLat(lat);
        favoriteCity.setLng(lng);
        // Make the POST request
        dbApi.addFavorite(favoriteCity).enqueue(new retrofit2.Callback<AddFavorResponse>() {
            @Override
            public void onResponse(Call<AddFavorResponse> call, retrofit2.Response<AddFavorResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String id = response.body().getId(); // Retrieve the ID from the response
                    favoriteCityId = id;
                    Log.d("FAVORITE CITY", "City added with ID: " + id);
                    Toast.makeText(SearchWeatherResultActivity.this, city + " was added to favorites", Toast.LENGTH_SHORT).show();

                    isFavorite = true;
                    FloatingActionButton button = findViewById(R.id.fab);
                    button.setImageResource(R.drawable.map_marker_minus);
                } else {
                    Toast.makeText(SearchWeatherResultActivity.this, "Failed to add city", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AddFavorResponse> call, Throwable t) {
                Toast.makeText(SearchWeatherResultActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteFavoriteCity(String city, String cityId) {
        dbApi.deleteFavorite(cityId).enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(SearchWeatherResultActivity.this, city + " was removed from favorites", Toast.LENGTH_SHORT).show();

                    FloatingActionButton button = findViewById(R.id.fab);
                    button.setImageResource(R.drawable.map_marker_plus);
                    isFavorite = false;
                } else {
                    Toast.makeText(SearchWeatherResultActivity.this, "Failed to remove city", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(SearchWeatherResultActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void updateUI(CurrentWeather current, List<DailyWeather> dailyWeatherList, String formattedAddress) {
        // Example: Update a TextView with the temperature
        TextView currentTemperatureTextView = findViewById(R.id.tv_temperature); // Ensure this ID exists in your layout
        currentTemperatureTextView.setText(Math.round(current.temperature) + "°F");

        // Update the weather description
        TextView currentTemperatureSummary = findViewById(R.id.tv_summary);
        WeatherCodeMapping.WeatherDetails details = WeatherCodeMapping.weatherCodeMapping.get(current.weatherCode);
        if (details != null) {
            currentTemperatureSummary.setText(details.description);
        } else {
            currentTemperatureSummary.setText(getString(R.string.unknown_weather));
        }

        TextView userWeatherCardLocation = findViewById(R.id.weather_card_address);
        userWeatherCardLocation.setText(formattedAddress);

        // Update the weather icon
        ImageView currentWeatherIcon = findViewById(R.id.weather_icon);
        if (details != null) {
            currentWeatherIcon.setImageResource(details.iconResId);
        } else {
            currentWeatherIcon.setImageResource(R.drawable.ic_launcher_background); // Add a default icon for unknown cases
        }

        // Update the card 2
        TextView currentHumidityData = findViewById(R.id.humidity_data);
        currentHumidityData.setText(Math.round(current.humidity)+"%");

        TextView currentWindSpeedData = findViewById(R.id.wind_speed_data);
        currentWindSpeedData.setText(String.format("%.2f", current.windSpeed)+"mph");

        TextView currentVisibilityData = findViewById(R.id.visibility_data);
        currentVisibilityData.setText(String.format("%.2f", current.visibility)+"mi");

        TextView currentPressureData = findViewById(R.id.pressure_data);
        currentPressureData.setText(String.format("%.2f", current.pressureSeaLevel)+"inHg");

        // Update the card 3, populate the table
        RecyclerView recyclerView = findViewById(R.id.weather_recycler_view);
        WeatherAdapter adapter = new WeatherAdapter(this, dailyWeatherList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        int verticalSpacing = getResources().getDimensionPixelSize(R.dimen.recycler_item_spacing);
        recyclerView.addItemDecoration(new SpacingItemDecoration(verticalSpacing));
    }

    private void showErrorUI() {
        TextView currentTemperatureTextView = findViewById(R.id.tv_temperature);
        TextView currentTemperatureSummary = findViewById(R.id.tv_summary);
        currentTemperatureTextView.setText("N/A");
        currentTemperatureSummary.setText("Unable to fetch data");
    }

}
