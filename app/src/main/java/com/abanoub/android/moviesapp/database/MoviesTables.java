package com.abanoub.android.moviesapp.database;

import android.database.sqlite.SQLiteDatabase;

public final class MoviesTables {

    public static final String MOVIES_TABLE_NAME = "movies";
    public static final String MOVIE_ID = "_id";
    public static final String MOVIE_TITLE = "title";
    public static final String MOVIE_POSTER_PATH = "poster_path";
    public static final String MOVIE_OVERVIEW = "overview";
    public static final String MOVIE_RELEASE_DATE = "release_date";
    public static final String MOVIE_VOTE_AVERAGE = "vote_average";

    private static final String CREATE_WEATHER_TABLE =
            " CREATE TABLE " + MOVIES_TABLE_NAME + "(" +
                    MOVIE_ID + " LONG , " +
                    MOVIE_TITLE + " TEXT , " +
                    MOVIE_OVERVIEW + " TEXT , " +
                    MOVIE_RELEASE_DATE + " TEXT , " +
                    MOVIE_VOTE_AVERAGE + " DOUBLE , " +
                    MOVIE_POSTER_PATH + " TEXT" +
                    ");";

    public static void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_WEATHER_TABLE);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MoviesTables.CREATE_WEATHER_TABLE);
        onCreate(db);
    }
}
