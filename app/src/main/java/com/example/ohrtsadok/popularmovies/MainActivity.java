package com.example.ohrtsadok.popularmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

public class MainActivity extends AppCompatActivity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    GridView moviesgrid;
    ArrayList<String> urls;
    ImageAdapter imageAdapter;
    ArrayList<Integer> integerArrayList;
    GetMovieURLS getMovieURLS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        urls = new ArrayList<String>();
        integerArrayList = new ArrayList<Integer>();

        moviesgrid = (GridView) findViewById(R.id.gridView);


        imageAdapter = new ImageAdapter(this,R.layout.imagelayout,urls);
        moviesgrid.setAdapter(imageAdapter);
        GetMovieIds getMovieIds = new GetMovieIds();
        getMovieIds.execute();




        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onStart() {
        super.onStart();




    }
    public String getImageURL(int id){
        final String baseUrl = "http://api.themoviedb.org/3/movie/";
        final String apikey = "api_key";
        String url = null;
        Uri uri = Uri.parse(baseUrl).
                buildUpon()
                .appendEncodedPath( id + "/images")
                .appendQueryParameter(apikey, BuildConfig.MovieDB_API_KEY)
                .build();
        HttpURLConnection urlConnection=  null;
        BufferedReader reader=null;
        JSONObject jsonresults;
        try {
            URL moviedburl = new URL(uri.toString());

            urlConnection = (HttpURLConnection) moviedburl.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
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
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            jsonresults = new JSONObject(buffer.toString());
            JSONArray resultsarray = jsonresults.getJSONArray("posters");
            JSONObject jsonObject = resultsarray.getJSONObject(0);
            url = jsonObject.getString("file_path");
            Log.v("Url", url);

        }
        catch (Exception e){
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

    public class GetMovieIds extends AsyncTask<Void, Void, Integer[]> {

        @Override
        protected Integer[] doInBackground(Void... params) {
            Integer[] ids = null;
            final String baseUrl = "http://api.themoviedb.org/3/movie/popular";
            final String apikey = "api_key";
            Uri uri = Uri.parse(baseUrl).buildUpon().appendQueryParameter(apikey,BuildConfig.MovieDB_API_KEY).build();
            HttpURLConnection urlConnection=  null;
            BufferedReader reader=null;
            JSONObject jsonresults;
            try {
                URL moviedburl = new URL(uri.toString());

                urlConnection = (HttpURLConnection) moviedburl.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
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
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                jsonresults = new JSONObject(buffer.toString());
                JSONArray resultsarray = jsonresults.getJSONArray("results");
                ids = new Integer[resultsarray.length()];
                for (int i = 0; i<resultsarray.length();i++){
                    JSONObject jsonArray = resultsarray.getJSONObject(i);
                    ids[i]=jsonArray.getInt("id");
                }
                Log.v("Results", ids[0].toString());

            }
            catch (Exception e){
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
            return ids;
        }

        @Override
        protected void onPostExecute(Integer[] integers) {

            for(int integer: integers){

                integerArrayList.add(integer);
            }
            getMovieURLS = new GetMovieURLS();
            getMovieURLS.execute(integerArrayList);



        }
    }
    public class GetMovieURLS extends AsyncTask<ArrayList<Integer>, Void, ArrayList<String>> {


        @Override
        protected ArrayList<String> doInBackground(ArrayList<Integer>... params) {
            ArrayList<String> urls2 = new ArrayList<String>();
            for(int i=0;i<params[0].size();i++){

                urls2.add(getImageURL(params[0].get(i)));
                Log.v("ImageAdapter", params[0].get(i).toString());

            }
            return urls2;

        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            for (String url: strings){
                imageAdapter.add(url);
            }
        }
    }
}
