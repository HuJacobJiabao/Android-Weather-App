package com.example.myapplication;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class CitySuggestionResponse implements Serializable {

    @SerializedName("predictions")
    public List<Prediction> predictions;

    public static class Prediction implements Serializable{
        @SerializedName("structured_formatting")
        public StructuredFormatting structuredFormatting;
    }

    public static class StructuredFormatting implements Serializable{
        @SerializedName("main_text")
        public String city;

        @SerializedName("secondary_text")
        public String state;
    }
}
