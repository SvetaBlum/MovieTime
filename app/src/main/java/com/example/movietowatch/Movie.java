package com.example.movietowatch;

import com.google.gson.annotations.SerializedName;

public class Movie {

    @SerializedName(value="title")
    private String name;

    @SerializedName(value="poster_path")
    private String imageurl;

    @SerializedName(value = "overview")
    private String description;


    public Movie(String name, String imageurl, String description) {
        this.name = name;
        this.imageurl = imageurl;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getImageurl() {
        return imageurl;
    }

    public String getDescription() {
        return description;
    }
}