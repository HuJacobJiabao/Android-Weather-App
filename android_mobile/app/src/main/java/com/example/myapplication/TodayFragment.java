package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class TodayFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_today, container, false);

        // Initialize UI components
        TextView temperatureTextView = rootView.findViewById(R.id.temperature_data);
        TextView summaryTextView = rootView.findViewById(R.id.condition_data);
        TextView humidityTextView = rootView.findViewById(R.id.humidity_data);
        TextView windSpeedTextView = rootView.findViewById(R.id.wind_speed_data);
        TextView visibilityTextView = rootView.findViewById(R.id.visibility_data);
        TextView pressureTextView = rootView.findViewById(R.id.pressure_data);
        TextView cloudCoverTextView = rootView.findViewById(R.id.cloud_cover_data);
        TextView ozoneTextView = rootView.findViewById(R.id.ozone_data);
        TextView precipitationTextView = rootView.findViewById(R.id.precipitation_data);
        ImageView weatherIconImageView = rootView.findViewById(R.id.condition_icon);


        CurrentWeather currentData = getArguments().getSerializable("currentData", CurrentWeather.class);

        if (currentData != null) {
            // Update the UI
            temperatureTextView.setText(Math.round(currentData.temperature) + "°F");
            humidityTextView.setText(Math.round(currentData.humidity) + "%");
            windSpeedTextView.setText(String.format("%.2f", currentData.windSpeed) + " mph");
            visibilityTextView.setText(String.format("%.2f", currentData.visibility) + " mi");
            pressureTextView.setText(String.format("%.2f", currentData.pressureSeaLevel) + " inHg");
            cloudCoverTextView.setText(Math.round(currentData.cloudCover) + "%");
            ozoneTextView.setText(String.valueOf(currentData.uvIndex));
            precipitationTextView.setText(currentData.precipitationProbability + "%");

            WeatherCodeMapping.WeatherDetails details = WeatherCodeMapping.weatherCodeMapping.get(currentData.weatherCode);
            if (details != null) {
                summaryTextView.setText(details.description);
            } else {
                summaryTextView.setText(getString(R.string.unknown_weather));
            }

            if (details != null) {
                weatherIconImageView.setImageResource(details.iconResId);
            } else {
                weatherIconImageView.setImageResource(R.drawable.ic_launcher_background); // Add a default icon for unknown cases
            }
        }

        return rootView;
    }

}
