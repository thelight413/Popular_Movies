/*
 * Copyright (C) 2016 The Android Open Source Project
 * Created by ohrtsadok on 1/24/16.
 */
package com.example.ohrtsadok.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/*
 * Gets Movie poster images and displays them in a ImageView
 */
public class ImageAdapter extends ArrayAdapter<Movie> {
    Context mContext;
    int mResource;
    private ArrayList<Movie> mMovieArrayList;


    public ImageAdapter(Context context, int resource, ArrayList<Movie> objects) {
        super(context,resource,objects);
        mContext = context;
        mResource = resource;
        mMovieArrayList = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final String baseUrl = "http://image.tmdb.org/t/p/w185";
        ImageView view;
        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = (ImageView) inflater.inflate(mResource, null);
        } else {

            view = (ImageView) convertView;
        }

        Uri uri = Uri.parse(baseUrl).buildUpon().appendEncodedPath(mMovieArrayList.get(position).getPosterpath()).build();
        Picasso.with(mContext).load(uri.toString()).into(view);
        return view;
    }
}
