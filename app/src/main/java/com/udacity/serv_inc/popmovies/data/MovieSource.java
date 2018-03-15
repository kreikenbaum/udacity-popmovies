package com.udacity.serv_inc.popmovies.data;

import android.accounts.NetworkErrorException;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;
import android.util.SparseIntArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.tools.MovieDbException;

/**
- Tasks
  - provide images
  - later: provide more info
- impl
  - observable, observed by adapter
  - if images available, notify observer
- help
  - git clone
  - try creating docs
*/


public class MovieSource extends Observable
     implements SharedPreferences.OnSharedPreferenceChangeListener {
    static final String TAG = MovieSource.class.getSimpleName();

    static final String API_KEY = "d71d2f344d3e61ffc32b11784f3e26eb";

    private static MovieSource instance;

    TmdbMovies tmdb;

    public List<MovieDb> getMovies() {
        if (isPopular()) {
            return popularMovies;
        } else {
            return topMovies;
        }
    }

    private List<MovieDb> popularMovies;
    private List<MovieDb> topMovies;
    private SparseArray<MovieDb> movieDetails;
    private boolean popular;

    /** @return movie at position <code>position</code> */
    public MovieDb getMovie(int position) {
        MovieDb out = getMovies().get(position);
        if ( movieDetails.get(out.getId()) != null ) {
            return movieDetails.get(out.getId());
        }
        return out;
    }

    void setMovies(List<MovieDb> movies) {
        if (isPopular()) {
            this.popularMovies = movies;
        } else {
            this.topMovies = movies;
        }
        this.updateListener();
    }

    void updateListener() {
        this.setChanged();
        this.notifyObservers();
    }

    /** @return list of poster file names */
    public List<String> getPosterPaths() {
        if ( getMovies() == null ) {
            return new ArrayList<String>();
        }
        List<String> out = new ArrayList<>();
        for ( MovieDb movie: getMovies() ) {
            out.add(movie.getPosterPath());
        }
        return out;
    }

    boolean isPopular() {
        return popular;
    }

    private MovieSource(boolean popular) {
        this.popular = popular;
        this.movieDetails = new SparseArray<>();
        new DownloadMovieTask().execute(this);
    }
    /** @return existing or newly created MovieSource */
    public static synchronized MovieSource get(boolean popular) {
        if (instance == null) {
            instance = new MovieSource(popular);
        }
        return instance;
    }
    /** @return existing MovieSource or null */
    public static MovieSource get() {
        return instance;
    }

    public void setMovie(int movieId, MovieDb movieDb) {
        movieDetails.append(movieId, movieDb);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        if (key.equals("show_popular")) {
            this.popular = sharedPreferences.getBoolean("show_popular", true);
            if ( getMovies() == null ) {
                new DownloadMovieTask().execute(this);
            } else {
                this.updateListener();
            }
        }
    }
}

class DownloadMovieTask extends AsyncTask<MovieSource, Void, List<MovieDb>> {

    private MovieSource source;
    @Override
    protected List<MovieDb> doInBackground(MovieSource... movieSources) {
        // android.os.Debug.waitForDebugger();

        source = movieSources[0];
        if (source.tmdb == null) {
            try {
                source.tmdb = new TmdbApi(source.API_KEY).getMovies();
            } catch (MovieDbException e) {
                e.printStackTrace();
                return null;
            }
        }

        List<MovieDb> movies;
        if ( source.isPopular() ) {
            movies = source.tmdb.getPopularMovies("", 0).getResults();
        } else {
            movies = source.tmdb.getTopRatedMovies("", 0).getResults();
        }
        Log.i(MovieSource.TAG, "images loaded");

        return movies;
    }

    @Override
    protected void onPostExecute(List<MovieDb> movieDbs) {
        source.setMovies(movieDbs);
        for (MovieDb movie: movieDbs) {
            new DownloadDetailTask()
                .execute(new Pair<MovieSource, Integer>(source, movie.getId()));
        }
    }
}

class DownloadDetailTask extends AsyncTask<Pair<MovieSource, Integer>, Void, MovieDb> {
    private MovieSource source;
    private int movieId;

    @Override
    protected MovieDb doInBackground(Pair<MovieSource, Integer>[] pairs) {
        source = pairs[0].first;
        movieId = pairs[0].second;
        return source.tmdb.getMovie(movieId, "");
    }

    @Override
    protected void onPostExecute(MovieDb movieDb) {
        source.setMovie(movieId, movieDb);
    }
}
