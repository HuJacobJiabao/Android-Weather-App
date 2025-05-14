package com.example.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import me.relex.circleindicator.CircleIndicator3;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity implements OnFavoriteChangedListener{

    private DBApi dbApi;
    private List<FavoriteCity> favoriteCities;
    private WeatherPagerAdapter adapter;

    public Map<String, WeatherResponse> weatherDataMap;

    private ActivityResultLauncher<Intent> weatherSearchLauncher;
    private MenuItem searchMenuItem;

    private View progressBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("DEBUG", "MainActivity started");
        // Safely retrieve weather data
        WeatherResponse ipLocationWeatherData;
        ipLocationWeatherData = getIntent().getSerializableExtra("weatherData", WeatherResponse.class);

        String ipLocation;
        ipLocation = getIntent().getSerializableExtra("ipLocation", String.class);
        weatherDataMap = new LinkedHashMap<>();


        if (ipLocationWeatherData != null) {
            // Initialize Retrofit
            dbApi = RetrofitClient.getInstance().create(DBApi.class);
            weatherDataMap.put(ipLocation, ipLocationWeatherData);
            adapter = new WeatherPagerAdapter(MainActivity.this, weatherDataMap, MainActivity.this);
            setupViewPager();
            fetchWeatherDataMap();
        }

        // Initialize the ActivityResultLauncher
        weatherSearchLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        boolean isFavorite = data.getBooleanExtra("isFavorite", false);
                        if (isFavorite) {
                            String city = data.getStringExtra("city");
                            String state = data.getStringExtra("state");
                            double lat = data.getDoubleExtra("lat", 0.0);
                            double lng = data.getDoubleExtra("lng", 0.0);
                            String id = data.getStringExtra("id");
                            WeatherResponse weatherData = data.getSerializableExtra("weatherResponse", WeatherResponse.class);

                            // Find and remove the city from favoriteCities
                            boolean exist = false;
                            for (FavoriteCity favoriteCity : favoriteCities) {
                                if (favoriteCity.getCity().equals(city) &&
                                        favoriteCity.getState().equals(state) &&
                                        Double.compare(favoriteCity.getLat(), lat) == 0 &&
                                        Double.compare(favoriteCity.getLng(), lng) == 0) {
                                    exist = true;
                                    break;
                                }
                            }

                            if (!exist){
                                // Add the city to favoriteCities and update weatherDataMap
                                FavoriteCity favoriteCity = new FavoriteCity(id, city, state, lat, lng);
                                favoriteCities.add(favoriteCity);

                                String cityFormatted = city + ", " + state;
                                weatherDataMap.put(cityFormatted, weatherData);

                                // Notify the adapter to update
                                adapter.updateData(weatherDataMap);

                                ViewPager2 viewPager = findViewById(R.id.viewPager);
                                CircleIndicator3 indicator = findViewById(R.id.indicator);
                                indicator.setViewPager(viewPager);
                            }
                        } else {
                            int index = 1;
                            String city = data.getStringExtra("city");
                            String state = data.getStringExtra("state");
                            double lat = data.getDoubleExtra("lat", 0.0);
                            double lng = data.getDoubleExtra("lng", 0.0);

                            // Construct formatted address for comparison
                            String cityFormatted = city + ", " + state;

                            // Find and remove the city from favoriteCities
                            FavoriteCity cityToRemove = null;
                            for (FavoriteCity favoriteCity : favoriteCities) {
                                if (favoriteCity.getCity().equals(city) &&
                                        favoriteCity.getState().equals(state) &&
                                        Double.compare(favoriteCity.getLat(), lat) == 0 &&
                                        Double.compare(favoriteCity.getLng(), lng) == 0) {
                                    cityToRemove = favoriteCity;
                                    index = favoriteCities.indexOf(favoriteCity);
                                    break;
                                }
                            }

                            if (cityToRemove != null) {
                                favoriteCities.remove(cityToRemove);
                            }

                            weatherDataMap.remove(cityFormatted);

                            // Notify the adapter to update
                            adapter.updateData(weatherDataMap);
                            adapter.notifyItemRemoved(index+1);

                            ViewPager2 viewPager = findViewById(R.id.viewPager);
                            CircleIndicator3 indicator = findViewById(R.id.indicator);
                            indicator.setViewPager(viewPager);
                        }
                    }
                }
        );

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Inflate the progress_bar layout dynamically
        ViewGroup rootLayout = findViewById(android.R.id.content); // Root layout of the activity
        progressBarLayout = getLayoutInflater().inflate(R.layout.progress_bar, rootLayout, false);

        // Add progressBarLayout to the root layout
        rootLayout.addView(progressBarLayout);

        // Hide it initially
        progressBarLayout.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideProgressBar();
        // Check if searchMenuItem is initialized and collapse it
        if (searchMenuItem != null && searchMenuItem.isActionViewExpanded()) {
            searchMenuItem.collapseActionView();
        }
    }

    private void fetchWeatherDataMap() {
        // Step 1: Fetch favorite cities
        dbApi.getFavorites().enqueue(new Callback<List<FavoriteCity>>() {
            @Override
            public void onResponse(Call<List<FavoriteCity>> call, Response<List<FavoriteCity>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    favoriteCities = response.body();
                    Log.d("DBAPI", "Fetched favorites: " + favoriteCities);


                    // Fetch weather data for each favorite city
                    for (FavoriteCity city : favoriteCities) {
                        String cityFormatted = city.getCity() + ", " + city.getState();
                        fetchCityWeatherData(city.getLat(), city.getLng(), cityFormatted, weatherDataMap);
                    }

                    // After all requests complete, set up the UI
                    // Since Retrofit is asynchronous, we delay the ViewPager setup slightly
                    setupViewPager();

                } else {
                    Log.e("DBAPI", "Failed to fetch favorites. Response: " + response.message());
                    Toast.makeText(MainActivity.this, "Failed to load favorites", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<FavoriteCity>> call, Throwable t) {
                Log.e("DBAPI", "Error fetching favorites", t);
                Toast.makeText(MainActivity.this, "Failed to fetch favorites", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchCityWeatherData(double latitude, double longitude, String formattedAddress, Map<String, WeatherResponse> data) {
        WeatherApi weatherApi = RetrofitClient.getInstance().create(WeatherApi.class);

        weatherApi.getWeather(latitude, longitude).enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weatherResponse = response.body();

                    Log.d("WeatherAPI", "Current Temperature: " + weatherResponse.current.temperature);
                    Log.d("WeatherAPI", "Weather Summary: " + weatherResponse.current.weatherCode);
                    data.put(formattedAddress, weatherResponse);
                    Log.d("MAP ADDED", formattedAddress);
                    adapter.updateData(data);

                    ViewPager2 viewPager = findViewById(R.id.viewPager);
                    CircleIndicator3 indicator = findViewById(R.id.indicator);
                    indicator.setViewPager(viewPager);
                } else {
                    Log.w("WeatherAPI", "Failed to fetch weather: " + response.message());
                    // Handle the failure (e.g., show an error message to the user)
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Log.e("WeatherAPI", "Error fetching weather: " + t.getMessage());
                // Handle the failure (e.g., show an error message to the user)
            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        // Configure the SearchView
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        // Set the query hint
        searchView.setQueryHint("Search for a location");

        // Add query text listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Handle search query submission
                // Optional: You can implement logic for final query submission here
                showProgressBar();
                fetchSubmittedWeather(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Handle real-time query text changes
                fetchCitySuggestions(newText, searchView);
                return false;
            }
        });
        this.searchMenuItem = searchItem;

        return true;
    }


    private void fetchSubmittedWeather(String query) {
        String[] parts = query.split(",");
        String city = parts[0].trim();
        String state = parts[1].trim();

        LocationApi locationApi = RetrofitClient.getInstance().create(LocationApi.class);

        // Use Location API to get latitude and longitude
        locationApi.getLocation(" ", city, state).enqueue(new Callback<LocationResponse>() {
            @Override
            public void onResponse(Call<LocationResponse> call, Response<LocationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Extract latitude, longitude, and formatted address
                    double latitude = response.body().latitude;
                    double longitude = response.body().longitude;
                    String formattedAddress = query;

                    Log.d("LocationAPI", "Latitude: " + latitude + ", Longitude: " + longitude);
                    Log.d("LocationAPI", "Formatted Address: " + formattedAddress);

                    // Use Weather API to fetch weather data
                    fetchWeatherData(latitude, longitude, formattedAddress);
                } else {
                    Log.w("LocationAPI", "Failed to fetch location: " + response.message());
                    // Handle the failure (e.g., show an error message to the user)
                }
            }

            @Override
            public void onFailure(Call<LocationResponse> call, Throwable t) {
                Log.e("LocationAPI", "Error fetching location: " + t.getMessage());
                // Handle the failure (e.g., show an error message to the user)
            }
        });
    }

    private void fetchWeatherData(double latitude, double longitude, String formattedAddress) {
        WeatherApi weatherApi = RetrofitClient.getInstance().create(WeatherApi.class);

        weatherApi.getWeather(latitude, longitude).enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weatherResponse = response.body();

                    Log.d("WeatherAPI", "Current Temperature: " + weatherResponse.current.temperature);
                    Log.d("WeatherAPI", "Weather Summary: " + weatherResponse.current.weatherCode);

                    // You can update your UI with the weather data here
                    updateUIWithWeatherData(weatherResponse, formattedAddress, latitude, longitude);
                } else {
                    Log.w("WeatherAPI", "Failed to fetch weather: " + response.message());
                    // Handle the failure (e.g., show an error message to the user)
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Log.e("WeatherAPI", "Error fetching weather: " + t.getMessage());
                // Handle the failure (e.g., show an error message to the user)
            }
        });
    }

    private void updateUIWithWeatherData(WeatherResponse weatherResponse, String formattedAddress, double lat, double lng) {
        boolean isFavorite = false;
        String id = "";
        for (FavoriteCity favoriteCity : favoriteCities) {
            if (Double.compare(favoriteCity.getLat(), lat) == 0 && Double.compare(favoriteCity.getLng(), lng) == 0) {
                isFavorite = true;
                id = favoriteCity.getId();
                break;
            }
        }

        Intent intent = new Intent(this, SearchWeatherResultActivity.class);

        // Pass the location and weather data to the new activity
        intent.putExtra("formattedAddress", formattedAddress);
        intent.putExtra("weatherData", weatherResponse);
        intent.putExtra("lat", lat);
        intent.putExtra("lng", lng);
        intent.putExtra("isFavorite", isFavorite);
        if (isFavorite) {
            intent.putExtra("id", id);
        }
        weatherSearchLauncher.launch(intent);
    }


    private void fetchCitySuggestions(String query, SearchView searchView) {
        CityApi cityApi = RetrofitClient.getInstance().create(CityApi.class);
        cityApi.getCitySuggestions(query).enqueue(new Callback<CitySuggestionResponse>() {
            @Override
            public void onResponse(@NonNull Call<CitySuggestionResponse> call, @NonNull Response<CitySuggestionResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<CitySuggestionResponse.Prediction> predictions = response.body().predictions;

                    // Extract (city, state) pairs
                    List<String> suggestions = new ArrayList<>();
                    for (CitySuggestionResponse.Prediction prediction : predictions) {
                        if (prediction.structuredFormatting != null) {
                            String city = prediction.structuredFormatting.city;
                            String secondaryText = prediction.structuredFormatting.state;

                            // Extract state by splitting secondaryText
                            String[] parts = secondaryText.split(","); // Split by comma
                            String state = parts[0].trim(); // Get the first part and trim spaces

                            suggestions.add(city + ", " + state); // Combine city and state
                            Log.d("CITY", city);
                            Log.d("State", state);
                        }
                    }
                    // Update SearchView suggestions
                    updateSearchSuggestions(suggestions, searchView);
                } else {
                    Log.w("CitySuggestions", "Failed to fetch suggestions: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<CitySuggestionResponse> call, Throwable t) {
                Log.e("CitySuggestions", "Error fetching suggestions: " + t.getMessage());
            }
        });

    }

    private void updateSearchSuggestions(List<String> suggestions, SearchView searchView) {
        // Access the SearchView's AutoComplete component
        SearchView.SearchAutoComplete searchAutoComplete = searchView.findViewById(androidx.appcompat.R.id.search_src_text);

        // Set text colors
        searchAutoComplete.setTextColor(Color.parseColor("#ffffff"));
        searchAutoComplete.setHintTextColor(Color.parseColor("#929193"));

        // Set the dropdown background color
        searchAutoComplete.setDropDownBackgroundResource(android.R.color.black);

        // Create an ArrayAdapter with the custom layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.custom_dropdown_item, suggestions);

        // Set the adapter to the SearchAutoComplete
        searchAutoComplete.setAdapter(adapter);

        // Handle item click events for the suggestions
        searchAutoComplete.setOnItemClickListener((parent, view, position, id) -> {
            String selectedSuggestion = (String) parent.getItemAtPosition(position);
            searchView.setQuery(selectedSuggestion, true); // Submit the selected suggestion
        });
    }

    private void setupViewPager() {
        // Set up ViewPager2
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);

        // Set up CircleIndicator
        CircleIndicator3 indicator = findViewById(R.id.indicator);
        indicator.setViewPager(viewPager);
    }

    @Override
    public void onFavoriteDeleted(String formattedAddress, int position) {
        // Perform the backend delete operation
        dbApi.deleteFavorite(favoriteCities.get(position - 1).getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("DELETE FAVORITE", "Deleted: " + formattedAddress);

                    // Remove from the map and the list
                    weatherDataMap.remove(formattedAddress);
                    favoriteCities.remove(position - 1);

                    // Notify the adapter
                    adapter.updateData(weatherDataMap);
                    adapter.notifyItemRemoved(position);

                    ViewPager2 viewPager = findViewById(R.id.viewPager);
                    int newPosition = Math.max(position - 1, 0); // Ensure it's not negative
                    viewPager.setCurrentItem(newPosition, false);

                    CircleIndicator3 indicator = findViewById(R.id.indicator);
                    indicator.setViewPager(viewPager);
                } else {
                    Log.e("DELETE FAVORITE", "Failed to delete: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("DELETE FAVORITE", "Error deleting city", t);
            }
        });
    }

    private void showProgressBar() {
        if (progressBarLayout != null) {
            progressBarLayout.setVisibility(View.VISIBLE);
        }
    }

    private void hideProgressBar() {
        if (progressBarLayout != null) {
            progressBarLayout.setVisibility(View.GONE);
        }
    }
}
