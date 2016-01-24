package com.example.ohrtsadok.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by ohrtsadok on 1/16/16.
 */
public class ImageAdapter extends ArrayAdapter<String>{
    Context c;
    int r;
    private ArrayList<String> urls;


    public ImageAdapter(Context context, int resource, ArrayList<String> objects) {
        super(context,resource,objects);
        c = context;
        r = resource;
        urls = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final String baseUrl = "http://image.tmdb.org/t/p/w185";
        ImageView view;
        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = (ImageView) inflater.inflate(r, null);
        } else {

            view = (ImageView) convertView;
        }

        Uri uri = Uri.parse(baseUrl).buildUpon().appendEncodedPath(urls.get(position)). build();
        Log.v("ImageAdapter",uri.toString());
        Picasso.with(c).load(uri.toString()).into(view);
        return view;
    }
}
