/*
 * Copyright (C) 2016 The Android Open Source Project
 * Created by ohrtsadok on 2/2/16.
 */

package com.example.ohrtsadok.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Represents a Movie object which contains a movie's details.
 */
public class Movie implements Parcelable {

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
    int id;
    String posterpath;
    String review;
    String title;
    String releasedate;
    String vote;
    String runtime;

    public Movie() {

    }

    protected Movie(Parcel in) {
        id = in.readInt();
        posterpath = in.readString();
        review = in.readString();
        title = in.readString();
        vote = in.readString();
        releasedate = in.readString();
        runtime = in.readString();
    }

    public void setInfo(JSONObject jsonObject) {
        setId(jsonObject);
        setPosterpath(jsonObject);
        setReview(jsonObject);
        setTitle(jsonObject);
        setReleasedate(jsonObject);
        setVote(jsonObject);
        setRuntime(jsonObject);

    }

    public int getId() {
        return id;

    }

    public void setId(JSONObject jsonObject) {
        try {
            this.id = jsonObject.getInt("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getPosterpath() {
        return posterpath;
    }

    public void setPosterpath(JSONObject jsonObject) {
        try {
            this.posterpath = jsonObject.getString("poster_path");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getReview() {

        return review;
    }

    public void setReview(JSONObject jsonObject) {
        try {
            this.review = jsonObject.getString("overview");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(JSONObject jsonObject) {
        try {
            this.title = jsonObject.getString("original_title");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getReleasedate() {
        return releasedate;
    }

    public void setReleasedate(JSONObject jsonObject) {
        try {
            String releasedate1 = jsonObject.getString("release_date");
            SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd");
            Date date = format.parse(releasedate1);
            format.applyPattern("MMM dd, yyyy");
            this.releasedate = format.format(date);
        } catch (JSONException e) {

            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    public String getVote() {
        return vote;
    }

    public void setVote(JSONObject jsonObject) {
        try {
            this.vote = jsonObject.getInt("vote_average") + "/10";
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getRuntime() {
        return runtime;
    }

    public void setRuntime(JSONObject jsonObject) {
        try {
            String runtime = jsonObject.getString("runtime");
            this.runtime = runtime + "min";
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(posterpath);
        dest.writeString(review);
        dest.writeString(title);
        dest.writeString(vote);
        dest.writeString(releasedate);
        dest.writeString(runtime);
    }
}
