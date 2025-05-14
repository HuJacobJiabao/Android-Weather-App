package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder> {
    private List<DailyWeather> dailyWeatherList;
    private Context context;

    public WeatherAdapter(Context context, List<DailyWeather> dailyWeatherList) {
        this.context = context;
        this.dailyWeatherList = dailyWeatherList;
    }

    @NonNull
    @Override
    public WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_weather_row, parent, false);
        return new WeatherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherViewHolder holder, int position) {
        DailyWeather dailyWeather = dailyWeatherList.get(position);

        holder.dateTextView.setText(dailyWeather.startTime.split("T")[0]); // Format the date
        holder.minTempTextView.setText(Math.round(dailyWeather.values.temperatureMin) + "");
        holder.maxTempTextView.setText(Math.round(dailyWeather.values.temperatureMax) + "");

        WeatherCodeMapping.WeatherDetails details = WeatherCodeMapping.weatherCodeMapping.get(dailyWeather.values.weatherCode);
        int iconResId;
        if (details != null) {
            iconResId = details.iconResId;
        } else {
            iconResId = R.drawable.ic_launcher_background; // Add a default icon for unknown cases
        }

        holder.weatherIconImageView.setImageResource(iconResId);
    }

    @Override
    public int getItemCount() {
        return dailyWeatherList.size();
    }

    static class WeatherViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView, minTempTextView, maxTempTextView;
        ImageView weatherIconImageView;

        public WeatherViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.tv_date);
            minTempTextView = itemView.findViewById(R.id.tv_min_temp);
            maxTempTextView = itemView.findViewById(R.id.tv_max_temp);
            weatherIconImageView = itemView.findViewById(R.id.img_weather_icon);
        }
    }
}
