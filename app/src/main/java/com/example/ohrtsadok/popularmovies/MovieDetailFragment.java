/**
 * Copyright (C) 2016 The Android Open Source Project
 * Created by ohrtsadok on 1/26/16.
 */
package com.example.ohrtsadok.popularmovies;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/*
 * Displays movie details in a view.
 */
public class MovieDetailFragment extends Fragment {
    TextView titleTV;
    TextView summaryTV;
    TextView releasedateTV;
    TextView runtimeTV;
    ImageView posterIV;
    TextView ratingTV;
    ScrollView scrollView;
    Movie movie;

    public MovieDetailFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.moviedetailfragment, container);
        scrollView = (ScrollView) rootView.findViewById(R.id.scrollView);
        titleTV = (TextView) rootView.findViewById(R.id.tvTitle);
        ratingTV = (TextView) rootView.findViewById(R.id.tvRating);
        runtimeTV = (TextView) rootView.findViewById(R.id.tvRuntime);
        summaryTV = (TextView) rootView.findViewById(R.id.tvSummary);
        releasedateTV = (TextView) rootView.findViewById(R.id.tvReleaseDate);
        posterIV = (ImageView) rootView.findViewById(R.id.ivThumbnail);

        Intent intent = getActivity().getIntent();
        movie = intent.getParcelableExtra("Movie");

        titleTV.setText(movie.getTitle());
        ratingTV.setText(movie.getVote());
        summaryTV.setText(movie.getReview());
        runtimeTV.setText(movie.getRuntime());
        releasedateTV.setText(movie.getReleasedate());

        final String baseUrl = "http://image.tmdb.org/t/p/w185";
        Uri uri = Uri.parse(baseUrl).buildUpon().appendEncodedPath(movie.getPosterpath()).build();
        Picasso.with(getContext()).load(uri.toString()).into(posterIV);
        return rootView;
    }

}
