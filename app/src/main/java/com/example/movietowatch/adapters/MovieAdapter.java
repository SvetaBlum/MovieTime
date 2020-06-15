package com.example.movietowatch.adapters;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.movietowatch.Movie;
import com.example.movietowatch.R;
;

import java.util.List;

import static androidx.recyclerview.widget.RecyclerView.*;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.HeroViewHolder> {

    Context mCtx;
    List<Movie> movieList;

    final String MOVIE_IMAGE_URL = "https://image.tmdb.org/t/p/w300/";

    public MovieAdapter(Context mCtx, List<Movie> movieList) {
        this.mCtx = mCtx;
        this.movieList = movieList;
    }

    @NonNull
    @Override
    public HeroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.recyclerview_layout,parent,false);
        return new HeroViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HeroViewHolder holder, int position) {
        Movie movie = movieList.get(position);

        Glide.with(mCtx)
                .load(MOVIE_IMAGE_URL + movie.getImageurl())
                .into(holder.imageView);

        holder.textView.setText(movie.getName());
        holder.desc_textview.setText(movie.getDescription());
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

     class HeroViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView textView;
        TextView desc_textview;

        public HeroViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            textView = itemView.findViewById(R.id.textView);
            desc_textview = itemView.findViewById(R.id.description_textview);
        }
    }
}
