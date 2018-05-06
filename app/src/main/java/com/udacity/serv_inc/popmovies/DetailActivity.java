package com.udacity.serv_inc.popmovies;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.udacity.serv_inc.popmovies.data.MovieSource;
import com.udacity.serv_inc.popmovies.databinding.ActivityDetailBinding;

import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.Reviews;
import info.movito.themoviedbapi.model.Video;

public class DetailActivity extends AppCompatActivity {
    private ActivityDetailBinding binding;
    private MovieDb movie;
    private LinearLayout layout;
    private boolean initialized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        binding.buttonFavorite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!initialized) {
                    return;
                }
                if (isChecked) {
                    Utils.addFavorite(DetailActivity.this, movie);
                } else {
                    Utils.removeFavorite(DetailActivity.this, movie);
                }
            }
        });
        layout = findViewById(R.id.linear_layout);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initialized = false;
        this.movie = MovieSource.getExisting()
            .getMovieById(getIntent().getExtras().getInt(MainActivity.ID));
        // populate UI
        Picasso.with(this).load(Utils.posterUri(movie.getPosterPath())).into(binding.ivPoster);
        binding.tvDescription.setText(movie.getOverview());
        binding.tvDuration.setText(Utils.formatRuntime(movie.getRuntime()));
        binding.tvRating.setText(Utils.formatRating(movie.getVoteAverage()));
        binding.tvTitle.setText(movie.getTitle());
        binding.tvYear.setText(Utils.formatYear(movie.getReleaseDate()));
        binding.buttonFavorite.setChecked(Utils.isFavorite(this, movie));
        insertVideosAndReviews();
        initialized = true;
    }

    private void insertVideosAndReviews() {
        for (Video v: this.movie.getVideos()) {
            addVideo(v);
        }
        for (Reviews r: this.movie.getReviews()) {
            addReview(r);
        }
    }

    private void addReview(Reviews r) {
        TextView tv = new TextView(DetailActivity.this);
        tv.setText(r.getAuthor() + ": " + r.getContent());
        layout.addView(tv);
    }

    private void addVideo(final Video video) {
        // create video view
        LayoutInflater layoutInflater = getLayoutInflater();
        Button b = (Button)layoutInflater.inflate(R.layout.part_video, null, false);
        b.setText(video.getName());
        // add onclicklistener: launch youtube-app
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // add onclicklistener: launch youtube-app: https://stackoverflow.com/a/7115040/1587329
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube://" + video.getKey()));
                startActivity(intent);
            }
        });
        layout.addView(b, layout.getChildCount()-2);
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
