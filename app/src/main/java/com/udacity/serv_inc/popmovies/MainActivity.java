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
import android.widget.Toast;

import com.udacity.serv_inc.popmovies.data.MovieSource;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Observable;
import java.util.Observer;

public class MainActivity extends AppCompatActivity implements Observer {
    static final String TAG = MainActivity.class.getSimpleName();
    // https://developer.android.com/guide/topics/ui/layout/gridview.html
    private ImageAdapter imageAdapter;
    private MovieSource movieSource;
    private GridView gridview;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        movieSource = MovieSource.get(sp.getBoolean("show_popular", true));
        movieSource.addObserver(this);
        gridview = (GridView) findViewById(R.id.gridview);
        imageAdapter = new ImageAdapter(this, movieSource.getPosterPaths());
        gridview.setAdapter(imageAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra(Intent.EXTRA_INDEX, position);
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
