package com.example.myapplication;

import com.google.gson.annotations.SerializedName;

public class AddFavorResponse {
    @SerializedName("id")
    private String id;
    @SerializedName("message")
    private String message;

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
