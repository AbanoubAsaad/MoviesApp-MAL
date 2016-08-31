package com.abanoub.android.moviesapp.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class MoviesProvider extends ContentProvider {

    static public final String PROVIDER_NAME = "com.abanoub.android.movies.provider";

    static public final String MOVIES_URL = "content://" + PROVIDER_NAME + "/" + MoviesTables.MOVIES_TABLE_NAME;
    static public final Uri MOVIES_URI = Uri.parse(MOVIES_URL);

    private SQLiteDatabase db;

    static public final int MOVIE = 1;
    static public final int MOVIE_ID = 2;

    static public final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(PROVIDER_NAME, MoviesTables.MOVIES_TABLE_NAME, MOVIE);
        uriMatcher.addURI(PROVIDER_NAME, MoviesTables.MOVIES_TABLE_NAME + "/#", MOVIE_ID);
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        MoviesDBHelper faxDBHelper = new MoviesDBHelper(context);
        db = faxDBHelper.getWritableDatabase();
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        {
            SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

            switch (uriMatcher.match(uri)) {
                case MOVIE:
                    qb.setTables(MoviesTables.MOVIES_TABLE_NAME);
                    break;
                case MOVIE_ID:
                    qb.setTables(MoviesTables.MOVIES_TABLE_NAME);
                    qb.appendWhere(MoviesTables.MOVIE_ID + "=" + uri.getPathSegments());
                    break;

                default:
                    throw new IllegalArgumentException("Unknown URI " + uri);
            }

            Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
            c.setNotificationUri(getContext().getContentResolver(), uri);
            return c;
        }


    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        int uriType = uriMatcher.match(uri);
        long rowID = 0;
        switch (uriType) {
            case MOVIE:
                rowID = db.insert(MoviesTables.MOVIES_TABLE_NAME, null, values);
                if (rowID > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                    if (rowID == -1)
                        Log.e("insert", "Failed to insert data into " + MoviesTables.MOVIES_TABLE_NAME + ": " + values);
                    return uri;
                }
                throw new SQLException("Failed to add a record into " + uri);


            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case MOVIE:
                count = db.delete(MoviesTables.MOVIES_TABLE_NAME, selection, selectionArgs);
                break;
            case MOVIE_ID:
                String movie_id = uri.getLastPathSegment();
                count = db.delete(MoviesTables.MOVIES_TABLE_NAME, MoviesTables.MOVIE_ID + " = " + movie_id +
                        (!TextUtils.isEmpty(selection) ? " AND (" +
                                selection + ')' : ""), selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);

        }
        Log.d("number of deleted rows ", "" + count);
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case MOVIE:
                count = db.update(MoviesTables.MOVIES_TABLE_NAME, values, selection, selectionArgs);
                break;
            case MOVIE_ID:
                count = db.update(MoviesTables.MOVIES_TABLE_NAME, values, MoviesTables.MOVIE_ID +
                        " = " + uri.getPathSegments() +
                        (!TextUtils.isEmpty(selection) ? " AND (" +
                                selection + ')' : ""), selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        Log.d("number of updated rows ", "" + count);
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

}

