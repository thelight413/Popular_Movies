/*
 * Copyright (C) 2016 The Android Open Source Project
 * Created by ohrtsadok on 4/11/16.
 */
package com.example.ohrtsadok.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/*
 * Content provider that controls the movie database
 */
public class MovieProvider extends ContentProvider {

    static final int MOVIE = 100;
    static final int FAVORITE = 300;
    static final int TRAILER = 400;
    static final int TRAILER_WITH_MOVIEID = 450;
    static final int REVIEW = 500;
    static final int REVIEW_WITH_MOVIEID = 550;
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private static final String sTrailerWithMovieIdSelection =
            MovieContract.TrailerEntry.COLUMN_MOVIE_ID + "= ?";
    private static final String sReviewWithMovieIdSelection = MovieContract.ReviewEntry.COLUMN_MOVIE_ID + "= ?";
    private MovieDbHelper mOpenHelper;

    static UriMatcher buildUriMatcher() {
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;
        uriMatcher.addURI(authority, "movie", MOVIE);
        uriMatcher.addURI(authority, "favorite", FAVORITE);
        uriMatcher.addURI(authority, "trailer", TRAILER);
        uriMatcher.addURI(authority, "trailer/*", TRAILER_WITH_MOVIEID);
        uriMatcher.addURI(authority, "review", REVIEW);
        uriMatcher.addURI(authority, "review/*", REVIEW_WITH_MOVIEID);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case FAVORITE: {
                retCursor = mOpenHelper.getReadableDatabase().query(MovieContract.FavoriteEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            }
            case TRAILER: {
                retCursor = mOpenHelper.getReadableDatabase().query(MovieContract.TrailerEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            }
            case TRAILER_WITH_MOVIEID:
                String movieid = MovieContract.TrailerEntry.getMovieIdFromUri(uri);
                retCursor = mOpenHelper.getReadableDatabase().query(MovieContract.TrailerEntry.TABLE_NAME, projection, sTrailerWithMovieIdSelection, new String[]{movieid}, null, null, sortOrder);
                break;
            case REVIEW: {
                retCursor = mOpenHelper.getReadableDatabase().query(MovieContract.ReviewEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            }
            case REVIEW_WITH_MOVIEID:
                String movie = MovieContract.ReviewEntry.getMovieIdFromUri(uri);
                retCursor = mOpenHelper.getReadableDatabase().query(MovieContract.ReviewEntry.TABLE_NAME, projection, sReviewWithMovieIdSelection, new String[]{movie}, null, null, sortOrder);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case TRAILER:
                return MovieContract.TrailerEntry.CONTENT_TYPE;
            case TRAILER_WITH_MOVIEID:
                return MovieContract.TrailerEntry.CONTENT_TYPE;
            case REVIEW:
                return MovieContract.ReviewEntry.CONTENT_TYPE;
            case REVIEW_WITH_MOVIEID:
                return MovieContract.ReviewEntry.CONTENT_TYPE;
            case FAVORITE:
                return MovieContract.FavoriteEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case FAVORITE: {
                long _id = db.insert(MovieContract.FavoriteEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MovieContract.FavoriteEntry.buildFavoriteUri();
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case TRAILER: {
                long _id = db.insert(MovieContract.TrailerEntry.TABLE_NAME, null, values);

                if (_id > 0)
                    returnUri = MovieContract.TrailerEntry.buildTrailerUri();
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case REVIEW: {
                long _id = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MovieContract.ReviewEntry.buildReviewUri();
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase sqLiteDatabase = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        int rowsDeleted = 0;
        switch (match) {
            case FAVORITE:
                rowsDeleted = sqLiteDatabase.delete(MovieContract.FavoriteEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TRAILER:
                rowsDeleted = sqLiteDatabase.delete(MovieContract.TrailerEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case REVIEW:
                rowsDeleted = sqLiteDatabase.delete(MovieContract.ReviewEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                break;
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase sqLiteDatabase = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated = 0;
        switch (match) {
            case FAVORITE:
                rowsUpdated = sqLiteDatabase.update(MovieContract.FavoriteEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case TRAILER:
                rowsUpdated = sqLiteDatabase.update(MovieContract.TrailerEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case REVIEW:
                rowsUpdated = sqLiteDatabase.update(MovieContract.ReviewEntry.TABLE_NAME, values, selection, selectionArgs);
            default:
                break;

        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case FAVORITE: {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.FavoriteEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            case TRAILER: {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.TrailerEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            case REVIEW: {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            default:
                return super.bulkInsert(uri, values);
        }
    }

}
