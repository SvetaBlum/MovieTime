package com.example.movietowatch.network;



import com.example.movietowatch.Movie;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Api {

    @GET(".")
    Call<ArrayList<Movie>> getMovies(@Query("api_key") String apiKey);

}
