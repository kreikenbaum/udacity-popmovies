package com.udacity.serv_inc.popmovies;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CompoundButton;

import com.squareup.picasso.Picasso;
import com.udacity.serv_inc.popmovies.data.MovieSource;
import com.udacity.serv_inc.popmovies.databinding.ActivityDetailBinding;

import info.movito.themoviedbapi.model.MovieDb;

public class DetailActivity extends AppCompatActivity {
    private ActivityDetailBinding binding;
    private MovieDb movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        binding.buttonFavorite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Utils.addFavorite(DetailActivity.this, movie.getId());
                } else {
                    Utils.removeFavorite(DetailActivity.this, movie.getId());
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        this.movie = MovieSource.get()
            .getMovie(getIntent().getExtras().getInt(MainActivity.INDEX));
        // populate UI
        Picasso.with(this).load(Utils.posterUri(movie.getPosterPath())).into(binding.ivPoster);
        binding.tvDescription.setText(movie.getOverview());
        binding.tvDuration.setText(Utils.formatRuntime(movie.getRuntime()));
        binding.tvRating.setText(Utils.formatRating(movie.getVoteAverage()));
        binding.tvTitle.setText(movie.getTitle());
        binding.tvYear.setText(Utils.formatYear(movie.getReleaseDate()));
        binding.buttonFavorite.setChecked(Utils.isFavorite(this, movie.getId()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_menu, menu);
        return true;
    }

    public void startSettings(MenuItem item) {
        startActivity(new Intent( this, SettingsActivity.class));
    }
}
