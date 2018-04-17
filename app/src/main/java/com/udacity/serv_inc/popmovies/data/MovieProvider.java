package com.udacity.serv_inc.popmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;


public class MovieProvider extends ContentProvider {
    private MovieDbHelper mOpenHelper;


    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    
    @Override
    public Cursor query( Uri uri,  String[] projection,  String selection,  String[] selectionArgs,  String sortOrder) {
        return mOpenHelper.getReadableDatabase().query(MovieContract.MovieEntry.TABLE_NAME,
                null, null, null, null, null, null);

    }

    
    @Override
    public String getType( Uri uri) {
        return null;
    }

    
    @Override
    public Uri insert( Uri uri,  ContentValues values) {
        long resultId = mOpenHelper.getWritableDatabase().insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
        return MovieContract.MovieEntry.CONTENT_URI
                .buildUpon().appendPath(Long.toString(resultId)).build();
    }

    @Override
    public int delete( Uri uri,  String selection,  String[] selectionArgs) {
        return mOpenHelper.getWritableDatabase().delete(MovieContract.MovieEntry.TABLE_NAME,
                selection, selectionArgs);
    }

    @Override
    public int update( Uri uri,  ContentValues values,  String selection,  String[] selectionArgs) {
        throw new UnsupportedOperationException("not implemented");
    }
}
