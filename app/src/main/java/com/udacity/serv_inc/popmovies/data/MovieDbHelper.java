package com.udacity.serv_inc.popmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Manages a local database for movie data, adapted from Sunshine S12.04
 */
public class MovieDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "movie.db";

    private static final int DATABASE_VERSION = 1;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_MOVIE_TABLE =
            "CREATE TABLE " + MovieContract.MovieEntry.TABLE_NAME + " (" +
            MovieContract.MovieEntry._ID   + " INTEGER PRIMARY KEY, " +
            MovieContract.MovieEntry.TITLE + " TEXT NOT NULL, " +
            MovieContract.MovieEntry.POSTERPATH + " TEXT NOT NULL );";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
