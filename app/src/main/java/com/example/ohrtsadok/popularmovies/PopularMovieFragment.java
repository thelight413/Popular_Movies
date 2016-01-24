package com.example.ohrtsadok.popularmovies;


import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by ohrtsadok on 1/24/16.
 */
public class PopularMovieFragment extends Fragment {
    GridView moviesgrid;
    ArrayList<String> urls;
    ImageAdapter imageAdapter;
    ArrayList<Integer> integerArrayList;

    @Override
    public void onStart() {
        super.onStart();
        GetMovieIds getMovieIds = new GetMovieIds();
        getMovieIds.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.pmfragment, container);
        moviesgrid = (GridView) rootView.findViewById(R.id.gridView);
        urls = new ArrayList<>();
        integerArrayList = new ArrayList<>();


        imageAdapter = new ImageAdapter(getActivity(), R.layout.imagelayout, urls);
        moviesgrid.setAdapter(imageAdapter);

        return rootView;
    }

    public String getImageURL(int id) {
        final String baseUrl = "http://api.themoviedb.org/3/movie/";
        final String apikey = "api_key";
        String url = null;
        Uri uri = Uri.parse(baseUrl).
                buildUpon()
                .appendEncodedPath(id + "/images")
                .appendQueryParameter(apikey, BuildConfig.MovieDB_API_KEY)
                .build();
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        JSONObject jsonresults;
        try {
            URL moviedburl = new URL(uri.toString());

            urlConnection = (HttpURLConnection) moviedburl.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder stringBuilder = new StringBuilder();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                stringBuilder.append(line + "\n");
            }

            if (stringBuilder.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            jsonresults = new JSONObject(stringBuilder.toString());
            JSONArray resultsarray = jsonresults.getJSONArray("posters");
            JSONObject jsonObject = resultsarray.getJSONObject(0);
            url = jsonObject.getString("file_path");
            Log.v("Url", url);

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (urlConnection != null) {
            urlConnection.disconnect();
        }
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return url;

    }

    public class GetMovieIds extends AsyncTask<Void, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(Void... params) {
            ArrayList<Integer> ids = new ArrayList<>();
            ArrayList<String> theurls = new ArrayList<>();
            final String baseUrl = "http://api.themoviedb.org/3/movie/popular";
            final String apikey = "api_key";
            Uri uri = Uri.parse(baseUrl).buildUpon().appendQueryParameter(apikey, BuildConfig.MovieDB_API_KEY).build();
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            JSONObject jsonresults;
            try {
                URL moviedburl = new URL(uri.toString());

                urlConnection = (HttpURLConnection) moviedburl.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder stringBuilder = new StringBuilder();
                if (inputStream == null) {
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {

                    stringBuilder.append(line + "\n");
                }

                if (stringBuilder.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                jsonresults = new JSONObject(stringBuilder.toString());
                JSONArray resultsarray = jsonresults.getJSONArray("results");
                for (int i = 0; i < resultsarray.length(); i++) {
                    JSONObject jsonArray = resultsarray.getJSONObject(i);
                    ids.add(jsonArray.getInt("id"));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            for (int id : ids) {
                theurls.add(getImageURL(id));
            }
            return theurls;
        }

        @Override
        protected void onPostExecute(ArrayList<String> urls) {
            for (String url : urls) {

                imageAdapter.add(url);

            }
        }
    }
}
