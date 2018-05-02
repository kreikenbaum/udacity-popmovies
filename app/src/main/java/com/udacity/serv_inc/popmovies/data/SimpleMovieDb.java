package com.udacity.serv_inc.popmovies.data;

import info.movito.themoviedbapi.model.MovieDb;

public class SimpleMovieDb extends MovieDb {
    private String title;

    @Override
    public String getPosterPath() {
        return posterPath;
    }

    private String posterPath;
    
    public SimpleMovieDb(int id, String title, String posterPath) {
        super();
        super.setId(id);
        this.title = title;
        this.posterPath = posterPath;
    }
}
