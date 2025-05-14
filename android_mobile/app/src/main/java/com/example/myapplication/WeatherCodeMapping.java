package com.example.myapplication;

import java.util.HashMap;
import java.util.Map;

public class WeatherCodeMapping {
    public static class WeatherDetails {
        public String description;
        public int iconResId;

        public WeatherDetails(String description, int iconResId) {
            this.description = description;
            this.iconResId = iconResId;
        }
    }

    public static final Map<Integer, WeatherDetails> weatherCodeMapping = new HashMap<>();

    static {
        weatherCodeMapping.put(4201, new WeatherDetails("Heavy Rain", R.drawable.rain_heavy));
        weatherCodeMapping.put(4001, new WeatherDetails("Rain", R.drawable.rain));
        weatherCodeMapping.put(4200, new WeatherDetails("Light Rain", R.drawable.rain_light));
        weatherCodeMapping.put(6201, new WeatherDetails("Heavy Freezing Rain", R.drawable.freezing_rain_heavy));
        weatherCodeMapping.put(6001, new WeatherDetails("Freezing Rain", R.drawable.freezing_rain));
        weatherCodeMapping.put(6200, new WeatherDetails("Light Freezing Rain", R.drawable.freezing_rain_light));
        weatherCodeMapping.put(6000, new WeatherDetails("Freezing Drizzle", R.drawable.freezing_drizzle));
        weatherCodeMapping.put(4000, new WeatherDetails("Drizzle", R.drawable.drizzle));
        weatherCodeMapping.put(7101, new WeatherDetails("Heavy Ice Pellets", R.drawable.ice_pellets_heavy));
        weatherCodeMapping.put(7000, new WeatherDetails("Ice Pellets", R.drawable.ice_pellets));
        weatherCodeMapping.put(7102, new WeatherDetails("Light Ice Pellets", R.drawable.ice_pellets_light));
        weatherCodeMapping.put(5101, new WeatherDetails("Heavy Snow", R.drawable.snow_heavy));
        weatherCodeMapping.put(5000, new WeatherDetails("Snow", R.drawable.snow));
        weatherCodeMapping.put(5100, new WeatherDetails("Light Snow", R.drawable.snow_light));
        weatherCodeMapping.put(5001, new WeatherDetails("Flurries", R.drawable.flurries));
        weatherCodeMapping.put(8000, new WeatherDetails("Thunderstorm", R.drawable.tstorm));
        weatherCodeMapping.put(2100, new WeatherDetails("Light Fog", R.drawable.fog_light));
        weatherCodeMapping.put(2000, new WeatherDetails("Fog", R.drawable.fog));
        weatherCodeMapping.put(1001, new WeatherDetails("Cloudy", R.drawable.cloudy));
        weatherCodeMapping.put(1102, new WeatherDetails("Mostly Cloudy", R.drawable.mostly_cloudy));
        weatherCodeMapping.put(1101, new WeatherDetails("Partly Cloudy", R.drawable.partly_cloudy_day));
        weatherCodeMapping.put(1100, new WeatherDetails("Mostly Clear", R.drawable.mostly_clear_day));
        weatherCodeMapping.put(1000, new WeatherDetails("Clear, Sunny", R.drawable.clear_day));
    }
}
