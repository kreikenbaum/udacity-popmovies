package com.udacity.serv_inc.popmovies.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;

import com.udacity.serv_inc.popmovies.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.TmdbMovies.MovieMethod;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.tools.MovieDbException;

import static com.udacity.serv_inc.popmovies.data.MovieSource.STATE_TOP;

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
    // copied from resources
    public static final String KEY = "display_type";
    static final String STATE_POPULAR = "popular";
    static final String STATE_TOP = "top";
    static final String STATE_FAVORITES = "favorites";
    
    static final String TAG = MovieSource.class.getSimpleName();
    static final String API_KEY = "d71d2f344d3e61ffc32b11784f3e26eb";
    
    private static MovieSource instance;

    TmdbMovies tmdb;
    Context context;

    public String getStatus() {
        return status;
    }

    private String status;
    private List<MovieDb> popularMovies;
    private List<MovieDb> topMovies;
    private List<MovieDb> favoriteMovies;
    private SparseArray<MovieDb> movieDetails;

    
    private MovieSource(Context context, String status) {
        this.status = status;
        this.movieDetails = new SparseArray<>();
        this.context = context.getApplicationContext();
        new DownloadMovieTask().execute(this);
    }
    /** @return existing or newly created MovieSource */
    public static synchronized MovieSource get(Context context, String status) {
        if (instance == null) {
            instance = new MovieSource(context, status);
        }
        return instance;
    }
    /** @return existing MovieSource or null */
    public static MovieSource getExisting() {
        return instance;
    }


    private List<MovieDb> getMovies() {
        switch (status) {
            case STATE_POPULAR:
                return popularMovies;
            case STATE_TOP:
                return topMovies;
            case STATE_FAVORITES:
                return favoriteMovies;
            default:
                throw new UnsupportedOperationException("status " + status);
        }
    }

    /** @return movie at position <code>position</code> */
    public MovieDb getMovie(int position) {
        MovieDb out = getMovies().get(position);
        if ( movieDetails.get(out.getId()) != null ) {
            return movieDetails.get(out.getId());
        }
        return out;
    }

    public MovieDb getMovieById(int id) {
        return movieDetails.get(id);
    }

    void setMovies(List<MovieDb> movies) {
        switch (status) {
            case STATE_POPULAR:
                this.popularMovies = movies;
                break;
            case STATE_TOP:
                this.topMovies = movies;
                break;
            case STATE_FAVORITES:
                this.favoriteMovies = movies;
                break;
            default:
                throw new UnsupportedOperationException("status " + status);
        }
        this.updateListener();
    }

    private void updateListener() {
        this.setChanged();
        this.notifyObservers();
    }

    /** @return list of poster file names */
    public List<String> getPosterPaths() {
        if ( getMovies() == null ) {
            return new ArrayList<>();
        }
        List<String> out = new ArrayList<>();
        for ( MovieDb movie: getMovies() ) {
            out.add(movie.getPosterPath());
        }
        return out;
    }

    public void setMovie(int movieId, MovieDb movieDb) {
        movieDetails.append(movieId, movieDb);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        if (KEY.equals(key)) {
            this.status = sharedPreferences.getString(KEY, null);
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
                source.tmdb = new TmdbApi(MovieSource.API_KEY).getMovies();
            } catch (MovieDbException e) {
                e.printStackTrace();
                return null;
            }
        }

        List<MovieDb> movies;
        switch (source.getStatus()) {
            case MovieSource.STATE_POPULAR:
                movies = source.tmdb.getPopularMovies("", 0).getResults();
                break;
            case MovieSource.STATE_TOP:
                movies = source.tmdb.getTopRatedMovies("", 0).getResults();
                break;
            case MovieSource.STATE_FAVORITES:
                movies = Utils.getFavoriteMovies(source.context);
                break;
            default:
                throw new UnsupportedOperationException("status " + source.getStatus());
        }
        Log.i(MovieSource.TAG, "movies loaded");

        return movies;
    }

    @Override
    protected void onPostExecute(List<MovieDb> movieDbs) {
        if ( movieDbs == null ) {
            Log.e(MovieSource.TAG, "failed to load");
            return;
        }
        source.setMovies(movieDbs);
        for (MovieDb movie: movieDbs) {
            new DownloadDetailTask().execute(new Pair<>(source, movie.getId()));
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
        return source.tmdb.getMovie(
                movieId, "",
                TmdbMovies.MovieMethod.videos, TmdbMovies.MovieMethod.reviews);
    }

    @Override
    protected void onPostExecute(MovieDb movieDb) {
        source.setMovie(movieId, movieDb);
    }
}
