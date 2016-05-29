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
    String trailerID;
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
        trailerID = in.readString();
    }

    public void setInfo(JSONObject jsonObject) {
        setId(getIdFromJson(jsonObject));
        setPosterpath(getPosterpathFromJSON(jsonObject));
        setReview(getReviewFromJSON(jsonObject));
        setTitle(getTitleFromJSON(jsonObject));
        setReleasedate(getReleaseDateFromJSON(jsonObject));
        setVote(getVoteFromJSON(jsonObject));
        setRuntime(getRuntimeFromJSON(jsonObject));

    }

    public int getId() {
        return id;

    }


    public void setId(int id1) {
        this.id = id1;
    }

    public int getIdFromJson(JSONObject jsonObject) {

        try {
            return jsonObject.getInt("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public String getPosterpath() {
        return posterpath;
    }

    public void setPosterpath(String imagePath) {
        posterpath = imagePath;
    }

    public String getPosterpathFromJSON(JSONObject jsonObject) {
        try {
            return jsonObject.getString("poster_path");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getReview() {

        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getReviewFromJSON(JSONObject jsonObject) {
        try {
            return jsonObject.getString("overview");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitleFromJSON(JSONObject jsonObject) {
        try {
            return jsonObject.getString("original_title");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getReleasedate() {
        return releasedate;
    }

    public void setReleasedate(String releasedate) {
        this.releasedate = releasedate;


    }

    public String getReleaseDateFromJSON(JSONObject jsonObject) {
        try {
            String releasedate1 = jsonObject.getString("release_date");
            SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd");
            Date date = format.parse(releasedate1);
            format.applyPattern("MMM dd, yyyy");
            return format.format(date);
        } catch (JSONException e) {

            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;

    }

    public String getVote() {
        return vote;
    }

    public void setVote(String vote) {
        this.vote = vote;
    }

    public String getVoteFromJSON(JSONObject jsonObject) {
        try {
            return jsonObject.getInt("vote_average") + "/10";
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getRuntime() {
        return runtime;
    }

    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }

    public String getRuntimeFromJSON(JSONObject jsonObject) {
        try {
            String runtime = jsonObject.getString("runtime");
            return runtime + "min";
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
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
        dest.writeString(trailerID);
    }
}
