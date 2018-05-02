package com.udacity.serv_inc.popmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.udacity.serv_inc.popmovies.data.MovieSource;

import java.util.Observable;
import java.util.Observer;

public class MainActivity extends AppCompatActivity implements Observer {
    private static final String TAG = MainActivity.class.getSimpleName();
    // https://developer.android.com/guide/topics/ui/layout/gridview.html
    private ImageAdapter imageAdapter;
    private MovieSource movieSource;
    static final String ID = "MOVIE_ID";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GridView gridview = findViewById(R.id.gridview);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        movieSource = MovieSource.get(this, sp.getString(MovieSource.KEY,
                        getResources().getString(R.string.popular)));
        movieSource.addObserver(this);
        sp.registerOnSharedPreferenceChangeListener(movieSource);

        imageAdapter = new ImageAdapter(this, movieSource.getPosterPaths());
        gridview.setAdapter(imageAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra(ID, movieSource.getMovie(position).getId());
                startActivity(intent);
            }
        });
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

    @Override
    public void update(Observable o, Object arg) {
        Log.i(TAG, "updated");
        imageAdapter.updateList(movieSource.getPosterPaths());
    }
}
