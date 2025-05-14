package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WeatherDetailsActivity extends AppCompatActivity {
    Object[][] seriesData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_details);

        String formattedAddress = getIntent().getStringExtra("formattedAddress");
        WeatherResponse weatherData = getIntent().getSerializableExtra("weatherData", WeatherResponse.class);

        if (weatherData != null) {
            List<DailyWeather> dailyWeatherList = weatherData.daily;
            seriesData = prepareSeriesData(dailyWeatherList);
            // Pass the data to fragments
            setupViewPager(weatherData, formattedAddress);
        }
        // Back Arrow Button
        ImageView backArrow = findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(v -> {
            // Go back to the previous activity
            getOnBackPressedDispatcher().onBackPressed();
        });

        // Twitter Button
        ImageView twitterButton = findViewById(R.id.twitter_button);
        twitterButton.setOnClickListener(v -> {
            if (weatherData != null) {
                String temperature = String.valueOf(weatherData.current.temperature); // Replace with the correct method to retrieve temperature
                String tweetText = String.format("Check Out %s’s Weather! It is %s°F! #CSCI571WeatherSearch", formattedAddress, temperature);

                // Open the browser with the Twitter intent
                String tweetUrl = "https://twitter.com/intent/tweet?text=" + Uri.encode(tweetText);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(tweetUrl));
                startActivity(intent);
            } else {
                Toast.makeText(this, "Weather data unavailable", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public static Object[][] prepareSeriesData(List<DailyWeather> dailyWeatherList) {
        // Initialize the Object[][] array with the size of the dailyWeatherList
        Object[][] seriesData = new Object[dailyWeatherList.size()][];

        // Populate the seriesData array
        for (int i = 0; i < dailyWeatherList.size(); i++) {
            DailyWeather dailyWeather = dailyWeatherList.get(i);

            // Convert startTime to timestamp
            long timestamp = convertToTimestamp(dailyWeather.startTime);

            // Get temperatureMin and temperatureMax
            double temperatureMin = dailyWeather.values.temperatureMin;
            double temperatureMax = dailyWeather.values.temperatureMax;

            // Add the data to the seriesData array
            seriesData[i] = new Object[]{timestamp, temperatureMin, temperatureMax};
        }

        return seriesData;
    }

    private void setupViewPager(WeatherResponse weatherData, String formattedAddress) {
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(formattedAddress);
        ViewPager2 viewPager = findViewById(R.id.view_pager);
        TabLayout tabLayout = findViewById(R.id.tab_layout);

        // Create TodayFragment
        TodayFragment todayFragment = new TodayFragment();
        Bundle todayBundle = new Bundle();
        todayBundle.putSerializable("currentData", weatherData.current);
        todayFragment.setArguments(todayBundle);

        // Create WeeklyFragment
        WeeklyFragment weeklyFragment = new WeeklyFragment();
        Bundle weeklyBundle = new Bundle();
        ArrayList<Object[]> serializableSeriesData = new ArrayList<>(Arrays.asList(seriesData));
        weeklyBundle.putSerializable("seriesData", serializableSeriesData);
        weeklyFragment.setArguments(weeklyBundle);

        WeatherDataFragment weatherDataFragment = new WeatherDataFragment();
        Bundle wdBundle = new Bundle();
        wdBundle.putDouble("cloudCover", weatherData.current.cloudCover);
        wdBundle.putInt("precipitation", weatherData.current.precipitationProbability);
        wdBundle.putDouble("humidity", weatherData.current.humidity);
        weatherDataFragment.setArguments(wdBundle);

        // Add other fragments (WeatherDataFragment) similarly
        FragmentStateAdapter adapter = new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                switch (position) {
                    case 0: return todayFragment;
                    case 1: return weeklyFragment;
                    case 2: return weatherDataFragment;
                    default: return todayFragment;
                }
            }

            @Override
            public int getItemCount() {
                return 3; // Number of tabs
            }
        };

        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0: tab.setText("Today"); tab.setIcon(R.drawable.today); break;
                case 1: tab.setText("Weekly"); tab.setIcon(R.drawable.weekly_tab); break;
                case 2: tab.setText("Weather Data"); tab.setIcon(R.drawable.thermometer); break;
            }
        }).attach();
    }

    public static long convertToTimestamp(String startTime) {
        ZonedDateTime zdt = ZonedDateTime.parse(startTime, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        return zdt.toInstant().toEpochMilli(); // Convert to milliseconds
    }


}
