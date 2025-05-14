package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import me.relex.circleindicator.CircleIndicator3;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherPagerAdapter extends RecyclerView.Adapter<WeatherPagerAdapter.WeatherViewHolder> {

    private List<Map.Entry<String, WeatherResponse>> weatherEntries;
    private final Context context;
    private int previousSize;
    private List<FavoriteCity> favoriteCities;
    private DBApi dbApi;
    private final OnFavoriteChangedListener listener;

    public WeatherPagerAdapter(Context context, Map<String, WeatherResponse> weatherDataMap,
                               OnFavoriteChangedListener listener) {
        this.context = context;
        this.weatherEntries = new ArrayList<>(weatherDataMap.entrySet()); // Convert map entries to a list
        this.previousSize = weatherEntries.size();
        dbApi = RetrofitClient.getInstance().create(DBApi.class);
        this.listener = listener;
    }
    public void updateData(Map<String, WeatherResponse> updatedData) {
        int newSize = updatedData.size();
        if (newSize != previousSize) {
            this.weatherEntries = new ArrayList<>(updatedData.entrySet());
            previousSize = newSize;
            notifyDataSetChanged(); // Notify adapter when size changes
        }
    }

    @NonNull
    @Override
    public WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_card, parent, false);
        return new WeatherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherViewHolder holder, int position) {
        Map.Entry<String, WeatherResponse> entry = weatherEntries.get(position);
        String formattedAddress = entry.getKey();
        WeatherResponse weather = entry.getValue();

        // Bind data using the utility method
        bindWeatherData(holder, weather, formattedAddress);

        // Set click listener for the card1
        CardView card1 = holder.itemView.findViewById(R.id.card1); // Get reference to the card1
        card1.setOnClickListener(v -> {
            Intent intent = new Intent(context, WeatherDetailsActivity.class);
            Log.d("WHAT HAPPENED", "Card1 clicked: " + formattedAddress);
            intent.putExtra("formattedAddress", formattedAddress);
            intent.putExtra("weatherData", weather);
            context.startActivity(intent);
        });

        FloatingActionButton deleteButton = holder.itemView.findViewById(R.id.delete_fab);

        if (position == 0) {
            // Hide the delete button for the first position (current weather page)
            deleteButton.setVisibility(View.GONE);
        } else {
            // Show the delete button for other positions
            deleteButton.setVisibility(View.VISIBLE);

            // Set click listener to delete the favorite city
            deleteButton.setOnClickListener(v -> {
                // Perform the delete operation
                Log.d("DELETE BUTTON", "Clicked for: " + formattedAddress);
                listener.onFavoriteDeleted(formattedAddress, position);
            });
        }

    }

    @Override
    public int getItemCount() {
        return weatherEntries.size();
    }

    private void bindWeatherData(WeatherViewHolder holder, WeatherResponse weather, String formattedAddress) {
        // Update temperature
        holder.temperature.setText(Math.round(weather.current.temperature) + "°F");

        // Update weather description using WeatherCodeMapping
        WeatherCodeMapping.WeatherDetails details = WeatherCodeMapping.weatherCodeMapping.get(weather.current.weatherCode);
        if (details != null) {
            holder.summary.setText(details.description);
        } else {
            holder.summary.setText(holder.itemView.getContext().getString(R.string.unknown_weather));
        }

        // Update location text
        holder.location.setText(formattedAddress);

        // Update weather icon
        if (details != null) {
            holder.weatherIcon.setImageResource(details.iconResId);
        } else {
            holder.weatherIcon.setImageResource(R.drawable.ic_launcher_background); // Add a default icon for unknown cases
        }

        // Update card 2 details (if needed, modify card2 views)
        // Example: if you need to update humidity, wind speed, visibility, and pressure

        TextView humidityData = holder.itemView.findViewById(R.id.humidity_data);
        humidityData.setText(Math.round(weather.current.humidity) + "%");

        TextView windSpeedData = holder.itemView.findViewById(R.id.wind_speed_data);
        windSpeedData.setText(String.format("%.2f", weather.current.windSpeed) + "mph");

        TextView visibilityData = holder.itemView.findViewById(R.id.visibility_data);
        visibilityData.setText(String.format("%.2f", weather.current.visibility) + "mi");

        TextView pressureData = holder.itemView.findViewById(R.id.pressure_data);
        pressureData.setText(String.format("%.2f", weather.current.pressureSeaLevel) + "inHg");

        // Update card 3: RecyclerView for daily weather
        RecyclerView recyclerView = holder.itemView.findViewById(R.id.weather_recycler_view);
        WeatherAdapter adapter = new WeatherAdapter(context, weather.daily);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        int verticalSpacing = holder.itemView.getContext().getResources().getDimensionPixelSize(R.dimen.recycler_item_spacing);
        recyclerView.addItemDecoration(new SpacingItemDecoration(verticalSpacing));
    }


    public static class WeatherViewHolder extends RecyclerView.ViewHolder {
        ImageView weatherIcon;
        TextView temperature, summary, location;

        public WeatherViewHolder(@NonNull View itemView) {
            super(itemView);
            weatherIcon = itemView.findViewById(R.id.weather_icon);
            temperature = itemView.findViewById(R.id.tv_temperature);
            summary = itemView.findViewById(R.id.tv_summary);
            location = itemView.findViewById(R.id.weather_card_address);
        }
    }
}
