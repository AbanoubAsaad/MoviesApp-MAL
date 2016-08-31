package com.abanoub.android.moviesapp.api.Models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.abanoub.android.moviesapp.database.MoviesProvider;
import com.abanoub.android.moviesapp.database.MoviesTables;

import java.io.Serializable;

public class Movie implements Serializable {

    private final String image_url = "http://image.tmdb.org/t/p/w185/";

    private String poster_path;
    private String overview;
    private String release_date;
    private long id;
    private String title;
    private double vote_average;

    private boolean isFavorite;

    public Movie(String poster_path, String overview, String release_date, long id, String title, double vote_average) {
        this.poster_path = poster_path;
        this.overview = overview;
        this.release_date = release_date;
        this.id = id;
        this.title = title;
        this.vote_average = vote_average;
    }

    public String getPoster_path() {
        return image_url + poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }


    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getVote_average() {
        return vote_average;
    }

    public void setVote_average(double vote_average) {
        this.vote_average = vote_average;
    }

    public boolean isFavorite(Context context) {
        String whereClause = MoviesTables.MOVIE_ID + "=" + getId();
        Cursor cursor = context.getContentResolver().query(
                MoviesProvider.MOVIES_URI, null, whereClause, null, null);
        setFavorite(cursor.moveToFirst());
        cursor.close();

        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public void addToFavorite(Context context) {

        setFavorite(true);

        ContentValues values = new ContentValues();
        values.put(MoviesTables.MOVIE_ID, getId());
        values.put(MoviesTables.MOVIE_TITLE, getTitle());
        values.put(MoviesTables.MOVIE_OVERVIEW, getOverview());
        values.put(MoviesTables.MOVIE_POSTER_PATH, getPoster_path());
        values.put(MoviesTables.MOVIE_VOTE_AVERAGE, getVote_average());
        values.put(MoviesTables.MOVIE_RELEASE_DATE, getRelease_date());

        context.getContentResolver().insert(MoviesProvider.MOVIES_URI, values);
    }

    public void removeFromFavorite(Context context) {

        setFavorite(false);

        String whereClause = MoviesTables.MOVIE_ID + "=" + getId();
        context.getContentResolver().delete(MoviesProvider.MOVIES_URI, whereClause, null);
    }


}
