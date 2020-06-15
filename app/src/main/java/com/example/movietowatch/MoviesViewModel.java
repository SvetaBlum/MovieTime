package com.example.movietowatch;


import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import com.example.movietowatch.network.Api;
import com.example.movietowatch.network.ApiUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MoviesViewModel extends ViewModel {

    //this is the data that we will fetch asynchronously
    private MutableLiveData<List<Movie>> movieList;

    //we will call this method to get the data
    public LiveData<List<Movie>> getMovies() {
        //if the list is null
        if (movieList == null) {
            movieList = new MutableLiveData<>();
            //we will load it asynchronously from server in this method
            loadMovies();
        }
        //finally we will return the list
        return movieList;
    }


    //This method is using Retrofit to get the JSON data from URL
    private void loadMovies() {

        Api api = ApiUtil.getRetrofitApi();

        Call<ArrayList<Movie>> call = api.getMovies("04f2f288263683f12131ae2ae1d348c6");

        call.enqueue(new Callback<ArrayList<Movie>>() {
            @Override
            public void onResponse(Call<ArrayList<Movie>> call, Response<ArrayList<Movie>> response) {

                //finally we are setting the list to our MutableLiveData
                movieList.setValue(response.body());
            }

            @Override
            public void onFailure(Call<ArrayList<Movie>> call, Throwable t) {

                Log.d("Error","");
            }
        });
    }
}
