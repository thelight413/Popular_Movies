/*
 * Copyright (C) 2016 The Android Open Source Project
 * Created by ohrtsadok on 1/26/16.
 */
package com.example.ohrtsadok.popularmovies;


import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ohrtsadok.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;

/*
 * Displays movie details in a view.
 */
public class MovieDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    static final int COL_TRAILER_ID = 0;
    static final int COL_TRAILER_MOVIEID = 1;
    static final int COL_TRAILER_TRAILERID = 2;
    static final int COL_TRAILER_TRAILERTITLE = 3;
    static final int COL_REVIEW_MOVIEID = 1;
    static final int COL_REVIEW_REVIEWID = 2;
    static final int COL_REVIEW_AUTHOR = 3;
    static final int COL_REVIEW_CONTENT = 4;
    private static final int TRAILERLOADER_ID = 1;
    private static final int REVIEWLOADER_ID = 2;
    private static final String[] TRAILER_COLUMNS = {

            MovieContract.TrailerEntry.TABLE_NAME + "." + MovieContract.TrailerEntry._ID,
            MovieContract.TrailerEntry.COLUMN_MOVIE_ID,
            MovieContract.TrailerEntry.COLUMN_TRAILER_ID,
            MovieContract.TrailerEntry.COLUMN_TRAILER_TITLE,

    };
    private static final String[] REVIEW_COLUMNS = {

            MovieContract.ReviewEntry.TABLE_NAME + "." + MovieContract.TrailerEntry._ID,
            MovieContract.ReviewEntry.COLUMN_MOVIE_ID,
            MovieContract.ReviewEntry.COLUMN_REVIEW_ID,
            MovieContract.ReviewEntry.COLUMN_REVIEW_AUTHOR,
            MovieContract.ReviewEntry.COLUMN_REVIEW_CONTENT,

    };
    TextView titleTV;
    TextView summaryTV;
    TextView releasedateTV;
    TextView runtimeTV;
    ImageView posterIV;
    TextView ratingTV;
    Button favoriteB;
    ScrollView scrollView;
    Movie movie;
    LinearLayout ll;


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
        favoriteB = (Button) rootView.findViewById(R.id.bFavorite);
        Bundle arguments = getArguments();
        if (arguments != null) {
            movie = arguments.getParcelable("Movie");
        } else {
            Intent intent = getActivity().getIntent();
            movie = intent.getParcelableExtra("Movie");
        }
        ll = (LinearLayout) rootView.findViewById(R.id.ll);

        titleTV.setText(movie.getTitle());
        Toast.makeText(getContext(), movie.getVote(), Toast.LENGTH_SHORT).show();
        ratingTV.setText(movie.getVote());
        summaryTV.setText(movie.getReview());
        runtimeTV.setText(movie.getRuntime());
        releasedateTV.setText(movie.getReleasedate());
        favoriteB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues cv = new ContentValues();
                cv.put(MovieContract.FavoriteEntry.COLUMN_MOVIE_ID, movie.getId());
                cv.put(MovieContract.FavoriteEntry.COLUMN_POSTER_PATH, movie.getPosterpath());
                cv.put(MovieContract.FavoriteEntry.COLUMN_RELEASE_DATE, movie.getReleasedate());
                cv.put(MovieContract.FavoriteEntry.COLUMN_REVIEW, movie.getReview());
                cv.put(MovieContract.FavoriteEntry.COLUMN_RUNTIME, movie.getRuntime());
                cv.put(MovieContract.FavoriteEntry.COLUMN_TITLE, movie.getTitle());
                cv.put(MovieContract.FavoriteEntry.COLUMN_VOTE, movie.getVote());
                Uri uri = MovieContract.FavoriteEntry.buildFavoriteUri();
                getActivity().getContentResolver().insert(uri, cv);
                Toast.makeText(getContext(), "Added to Favorites", Toast.LENGTH_SHORT).show();
            }
        });

        getLoaderManager().initLoader(TRAILERLOADER_ID, null, this);



        final String baseUrl = "http://image.tmdb.org/t/p/w185";
        Uri uri = Uri.parse(baseUrl).buildUpon().appendEncodedPath(movie.getPosterpath()).build();
        Picasso.with(getContext()).load(uri.toString()).into(posterIV);
        return rootView;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        switch (id) {
            case TRAILERLOADER_ID:
                Uri trailerUri = MovieContract.TrailerEntry.buildTrailerWithMovieId(String.valueOf(movie.getId()));
                return new CursorLoader(getActivity(), trailerUri, TRAILER_COLUMNS, null, null, null);
            case REVIEWLOADER_ID:
                Uri reviewUri = MovieContract.ReviewEntry.buildReviewWithMovieId(String.valueOf(movie.getId()));
                return new CursorLoader(getActivity(), reviewUri, REVIEW_COLUMNS, null, null, null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case TRAILERLOADER_ID:
                getLoaderManager().initLoader(REVIEWLOADER_ID, null, this);

                try {
                    while (data.moveToNext()) {
                        View r = LayoutInflater.from(getContext()).inflate(R.layout.trailer_list_item, ll, false);
                        TextView tv = ((TextView) r.findViewById(R.id.trailerTitle));
                        tv.setText(data.getString(MovieDetailFragment.COL_TRAILER_TRAILERTITLE));
                        final String trailerUrl = "https://www.youtube.com/watch?v=" + data.getString(MovieDetailFragment.COL_TRAILER_TRAILERID);
                        r.setClickable(true);
                        r.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(trailerUrl));
                                startActivity(intent);
                            }
                        });
                        ll.addView(r);
                    }
                } finally {
                    data.close();
                }
                break;
            case REVIEWLOADER_ID:
                try {
                    while (data.moveToNext()) {
                        View r = LayoutInflater.from(getContext()).inflate(R.layout.review_list_item, ll, false);
                        ((TextView) r.findViewById(R.id.tvAuthor)).setText(data.getString(MovieDetailFragment.COL_REVIEW_AUTHOR));
                        ((TextView) r.findViewById(R.id.tvContent)).setText(data.getString(MovieDetailFragment.COL_REVIEW_CONTENT));
                        ll.addView(r);
                    }
                } finally {
                    data.close();
                }
                break;
        }


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}