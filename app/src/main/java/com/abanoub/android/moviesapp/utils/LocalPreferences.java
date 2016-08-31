package com.abanoub.android.moviesapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.abanoub.android.moviesapp.activites.MoviesListActivity;

public class LocalPreferences {

    private final String MOVIES_TYPE_KEY = "MOVIES_TYPE";

    private SharedPreferences mPreferences;

    public LocalPreferences(Context context) {
        mPreferences = context.getSharedPreferences("movies_app_pref", Context.MODE_PRIVATE);
    }

    public void setMoviesType(String value) {
        mPreferences.edit().putString(MOVIES_TYPE_KEY, value).commit();
    }

    public String getMoviesType() {
        return mPreferences.getString(MOVIES_TYPE_KEY, MoviesListActivity.POPULAR_MOVIES);
    }
}
