/*
 * Copyright (C) 2016 The Android Open Source Project
 * Created by ohrtsadok on 1/24/16.
 */

package com.example.ohrtsadok.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/*
 * Obtains Movie data and displays it in a GridView
 */
public class PopularMovieFragment extends Fragment {
    final String baseUrl = "http://api.themoviedb.org/3/movie";
    final String apikey = "api_key";
    public GridView movieGrid;
    ImageAdapter mImageAdapter;
    RequestQueue queue;
    ArrayList<Movie> mMovieArrayList;
    String type;

    @Override
    public void onStart() {
        super.onStart();
        mImageAdapter.clear();
        queue = Volley.newRequestQueue(getActivity());
        getMovieInfo();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.pmfragment, container);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        type = preferences.getString("SortBy", "popular");

        movieGrid = (GridView) rootView.findViewById(R.id.gridView);
        mMovieArrayList = new ArrayList<>();
        mImageAdapter = new ImageAdapter(getActivity(), R.layout.imagelayout, mMovieArrayList);
        movieGrid.setAdapter(mImageAdapter);
        movieGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), MovieDetailActivity.class);
                intent.putExtra("Movie", mMovieArrayList.get(position));
                startActivity(intent);

            }
        });
        return rootView;
    }

    /*
     * This will get the movies ids and calls the getIdInformation method
     * to create the Movie objects to add to the MovieList.
     */
    public void getMovieInfo() {

        Uri uri = Uri.parse(baseUrl).buildUpon().appendEncodedPath(type).appendQueryParameter(apikey, BuildConfig.MovieDB_API_KEY).build();
        String url = uri.toString();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray resultsarray = response.getJSONArray("results");
                    for (int i = 0; i < resultsarray.length(); i++) {
                        JSONObject jsonObject = resultsarray.getJSONObject(i);
                        getIdInformation(jsonObject.getInt("id"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }

        });

        queue.add(jsonObjectRequest);


    }

    /*
     * Retrieves movie details from TheMovieDb Api and saves the information
     * in a Movie object. That Movie object is then added to the Movie list.
     */
    public void getIdInformation(int id) {

        Uri uri = Uri.parse(baseUrl).
                buildUpon()
                .appendEncodedPath(String.valueOf(id))
                .appendQueryParameter(apikey, BuildConfig.MovieDB_API_KEY)
                .build();

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, uri.toString(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Movie movie = new Movie();
                movie.setInfo(response);
                Log.v("movie", movie.getTitle());
                mMovieArrayList.add(movie);
                mImageAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        queue.add(jsonObjectRequest);
    }

}

