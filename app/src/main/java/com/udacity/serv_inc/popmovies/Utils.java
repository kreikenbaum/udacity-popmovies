package com.udacity.serv_inc.popmovies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.udacity.serv_inc.popmovies.data.MovieContract;
import com.udacity.serv_inc.popmovies.data.SimpleMovieDb;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;

import info.movito.themoviedbapi.model.MovieDb;

/**
 * Static helper methods
 * */

public class Utils {
    private static final String MOVIE_BASE = "http://image.tmdb.org/t/p/";
    private static final String IMAGE_SIZE = "w185";
    private static final Uri POSTER = Uri.parse(MOVIE_BASE + IMAGE_SIZE);
    private static final String[] PROJECTION_ARGUMENTS = new String[]{
            MovieContract.MovieEntry._ID
    };

    // todo: avoid failure by this or other
    // credits: https://stackoverflow.com/questions/1560788/
    // TCP/HTTP/DNS (depending on the port, 53=DNS, 80=HTTP, etc.)
    public static boolean isOnline() {
        try {
            int timeoutMs = 1500;
            Socket sock = new Socket();
            SocketAddress sockaddr = new InetSocketAddress("8.8.8.8", 53);

            sock.connect(sockaddr, timeoutMs);
            sock.close();

            return true;
        } catch (IOException e) { return false; }
    }
    /** @return the path to the poster Uri */
    public static Uri posterUri(String rest) {
        return POSTER.buildUpon().appendEncodedPath(rest).build();
    }

    public static String formatYear(String date) {
        return date.split("-")[0];
    }
    public static String formatRating(float rating) {
        return String.format("%.1f / 10", rating);
    }
    public static String formatRuntime(int rating) {
        return String.format("%dmin", rating);
    }

    private static ArrayList<Integer> getFavs(Context context) {
        ArrayList<Integer> out = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI, PROJECTION_ARGUMENTS,
                null, null, null);
        if ( cursor != null ) {
            while(cursor.moveToNext()) {
                int newid = cursor.getInt(0);
                out.add(newid);
            }
        }
        return out;
    }
    public static boolean isFavorite(Context context, MovieDb movie) {
        //        return getFavs(context).indexOf(movie.getId()) > -1;
        for ( MovieDb favMovie: getFavoriteMovies(context) ) {
            if ( favMovie.getId() == movie.getId() ) {
                return true;
            }
        }
        return false;
    }
    public static void addFavorite(Context context, MovieDb movie) {
        ContentValues cv = new ContentValues();
        cv.put(MovieContract.MovieEntry._ID, movie.getId());
        cv.put(MovieContract.MovieEntry.TITLE, movie.getTitle());
        cv.put(MovieContract.MovieEntry.POSTERPATH, movie.getPosterPath());
        context.getContentResolver().insert(
                MovieContract.MovieEntry.CONTENT_URI,
                cv);
    }
    public static void removeFavorite(Context context, MovieDb movie) {
        String[] selectionArgs = new String[]{
            String.valueOf(movie.getId())
        };
        context.getContentResolver().delete(
               MovieContract.MovieEntry.CONTENT_URI,
               MovieContract.MovieEntry._ID + " = ?",
               selectionArgs);
    }

    public static ArrayList<MovieDb> getFavoriteMovies(Context context) {
        ArrayList<MovieDb> out = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI, null, null, null, null);
        if ( cursor != null ) {
            while(cursor.moveToNext()) {
                SimpleMovieDb simpleMovieDb = new SimpleMovieDb(
                        cursor.getInt(cursor.getColumnIndexOrThrow(MovieContract.MovieEntry._ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(MovieContract.MovieEntry.TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(MovieContract.MovieEntry.POSTERPATH))
                );
                out.add(simpleMovieDb);
            }
            cursor.close();
        }
        return out;
    }
}
