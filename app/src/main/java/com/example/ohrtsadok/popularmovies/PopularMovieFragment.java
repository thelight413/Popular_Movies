/*
 * Copyright (C) 2016 The Android Open Source Project
 * Created by ohrtsadok on 1/24/16.
 */

package com.example.ohrtsadok.popularmovies;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.preference.PreferenceManager;
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
import com.example.ohrtsadok.popularmovies.data.MovieContract;
import com.example.ohrtsadok.popularmovies.data.MovieDbHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/*
 * Obtains Movie data and displays it in a GridView
 */
public class PopularMovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    static final int COL_MOVIEID_ID = 1;
    static final int COL_MOVIE_TITLE = 2;
    static final int COL_MOVIE_POSTER_PATH = 3;
    static final int COL_MOVIE_RELEASE_DATE = 4;
    static final int COL_MOVIE_RUNTIME = 5;
    static final int COL_MOVIE_VOTE = 6;
    static final int COL_MOVIE_REVIEW = 7;
    private static final int FAVORITELOADER_ID = 1;
    private static final String[] FAVORITE_COLUMNS = {

            MovieContract.FavoriteEntry.TABLE_NAME + "." + MovieContract.FavoriteEntry._ID,
            MovieContract.FavoriteEntry.COLUMN_MOVIE_ID,
            MovieContract.FavoriteEntry.COLUMN_TITLE,
            MovieContract.FavoriteEntry.COLUMN_POSTER_PATH,
            MovieContract.FavoriteEntry.COLUMN_RELEASE_DATE,
            MovieContract.FavoriteEntry.COLUMN_RUNTIME,
            MovieContract.FavoriteEntry.COLUMN_VOTE,
            MovieContract.FavoriteEntry.COLUMN_REVIEW,
    };
    final String baseUrl = "http://api.themoviedb.org/3/movie";
    final String apikey = "api_key";
    public GridView movieGrid;
    ImageAdapter mImageAdapter;
    RequestQueue queue;
    ArrayList<Movie> mMovieArrayList;
    String type;
    MovieDbHelper movieDbHelper;
    SQLiteDatabase db;

    @Override
    public void onStart() {
        super.onStart();
        mImageAdapter.clear();
        queue = Volley.newRequestQueue(getActivity());
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        type = preferences.getString("SortBy", "popular");
        if (!(type.equals("favorite"))) {
            getMovieInfo();
        } else {
            getLoaderManager().initLoader(FAVORITELOADER_ID, null, this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.pmfragment, container, false);
        movieDbHelper = new MovieDbHelper(getContext());
        db = movieDbHelper.getWritableDatabase();


        movieGrid = (GridView) rootView.findViewById(R.id.gridView);
        mMovieArrayList = new ArrayList<>();
        mImageAdapter = new ImageAdapter(getActivity(), R.layout.imagelayout, mMovieArrayList);
        movieGrid.setAdapter(mImageAdapter);
        movieGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((Callback) getActivity()).OnItemSelected(mMovieArrayList.get(position));

            }
        });
        return rootView;
    }

    /*
     * This will get the movies ids and calls the getIdInformation method
     * to create the Movie objects to add to the MovieList.
     */
    public void getMovieInfo() {

        Uri uri = Uri.parse(baseUrl).buildUpon().appendEncodedPath(type).appendQueryParameter(apikey, BuildConfig.APIKEY).build();
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
                .appendQueryParameter(apikey, BuildConfig.APIKEY)
                .build();
        final int movieid = id;
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, uri.toString(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Movie movie = new Movie();
                movie.setInfo(response);
                getMovieTrailerID(movieid);
                getMovieReviews(movieid);
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

    /*
     *  Retrieves trailer IDs from TheMovieDb Api and saves the information
     * in the movie database.
     */
    public void getMovieTrailerID(int id) {
        Uri uri = Uri.parse(baseUrl)
                .buildUpon()
                .appendEncodedPath(String.valueOf(id))
                .appendEncodedPath("videos")
                .appendQueryParameter(apikey, BuildConfig.APIKEY)
                .build();
        final String movieid = String.valueOf(id);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, uri.toString(), null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray results = response.getJSONArray("results");
                    for (int i = 0; i < results.length(); i++) {
                        JSONObject jsonObject = results.getJSONObject(i);
                        addTrailerIdToDb(jsonObject.getString("id"), String.valueOf(movieid), jsonObject.getString("name"));
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
     *  Retrieves reviews from TheMovieDb Api and saves the information
     * in the movie database.
     */
    public void getMovieReviews(int id) {
        Uri uri = Uri.parse(baseUrl)
                .buildUpon()
                .appendEncodedPath(String.valueOf(id))
                .appendEncodedPath("reviews")
                .appendQueryParameter(apikey, BuildConfig.APIKEY)
                .build();
        final String movieid = String.valueOf(id);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, uri.toString(), null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray results = response.getJSONArray("results");
                    for (int i = 0; i < results.length(); i++) {
                        JSONObject jsonObject = results.getJSONObject(i);
                        String content = jsonObject.getString("content");
                        String author = jsonObject.getString("author");
                        addReviewsToDb(jsonObject.getString("id"), String.valueOf(movieid), author, content);
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
     * Inserts trailer ID that was retrieved from TheMovieDb Api to the movie database
     */
    private void addTrailerIdToDb(String trailerid, String movieid, String name) {
        ContentValues cv = new ContentValues();
        cv.put(MovieContract.TrailerEntry.COLUMN_MOVIE_ID, movieid);
        cv.put(MovieContract.TrailerEntry.COLUMN_TRAILER_ID, trailerid);
        cv.put(MovieContract.TrailerEntry.COLUMN_TRAILER_TITLE, name);
        Uri uri = MovieContract.TrailerEntry.buildTrailerUri();
        getActivity().getContentResolver().insert(uri, cv);
    }

    /*
     * Inserts reviews that was retrieved from TheMovieDb Api to the movie database
     */
    private void addReviewsToDb(String review, String movieid, String author, String content) {
        ContentValues cv = new ContentValues();
        cv.put(MovieContract.ReviewEntry.COLUMN_MOVIE_ID, movieid);
        cv.put(MovieContract.ReviewEntry.COLUMN_REVIEW_ID, review);
        cv.put(MovieContract.ReviewEntry.COLUMN_REVIEW_AUTHOR, author);
        cv.put(MovieContract.ReviewEntry.COLUMN_REVIEW_CONTENT, content);
        Uri uri = MovieContract.ReviewEntry.buildReviewUri();
        getActivity().getContentResolver().insert(uri, cv);

    }
    /*
     * Query the movie database for the favorite movies the user chose.
     */

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        Uri favoriteuri = MovieContract.FavoriteEntry.buildFavoriteUri();
        switch (id) {
            case FAVORITELOADER_ID:
                return new CursorLoader(getActivity(), favoriteuri, FAVORITE_COLUMNS, null, null, null);
            default:
                return null;
        }

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        while (data.moveToNext()) {
            Movie movie = new Movie();
            movie.setId(Integer.parseInt(data.getString(COL_MOVIEID_ID)));
            movie.setPosterpath(data.getString(COL_MOVIE_POSTER_PATH));
            movie.setReleasedate(data.getString(COL_MOVIE_RELEASE_DATE));
            movie.setReview(data.getString(COL_MOVIE_REVIEW));
            movie.setRuntime(data.getString(COL_MOVIE_RUNTIME));
            movie.setTitle(data.getString(COL_MOVIE_TITLE));
            movie.setVote(data.getString(COL_MOVIE_VOTE));
            mMovieArrayList.add(movie);
        }
        mImageAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader loader) {
        mImageAdapter.clear();
    }

    /*
     * Interface for this fragment so it can interact with any parent activity
     */
    public interface Callback {
        void OnItemSelected(Movie movie);
    }

}

