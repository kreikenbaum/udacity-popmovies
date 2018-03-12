package com.udacity.serv_inc.popmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by web on 3/1/18.
 */

public class Utils {
    private static final String MOVIE_BASE = "http://image.tmdb.org/t/p/";
    private static final String IMAGE_SIZE = "w185";
    private static final Uri POSTER = Uri.parse(MOVIE_BASE + IMAGE_SIZE);

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

    private static final String FAVS = "FAVS";
    private static Type listType = new TypeToken<List<Integer>>() {}.getType();
    private static ArrayList<Integer> getFavs(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        return gson.fromJson(sp.getString(FAVS, "[]"), listType);
    }
    private static void setFavs(Context context, ArrayList<Integer> favs) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = sp.edit();
        Gson gson = new Gson();
        edit.putString(FAVS, gson.toJson(favs, listType));
        edit.apply();
    }
    public static boolean isFavorite(Context context, int movieId) {
        return getFavs(context).contains(movieId);
    }
    public static void addFavorite(Context context, int movieId) {
        ArrayList<Integer> favs = getFavs(context);
        favs.add(movieId);
        setFavs(context, favs);
    }
    public static void removeFavorite(Context context, int movieId) {
        ArrayList<Integer> favs = getFavs(context);
        favs.remove(favs.indexOf(movieId));
        setFavs(context, favs);
    }
}
