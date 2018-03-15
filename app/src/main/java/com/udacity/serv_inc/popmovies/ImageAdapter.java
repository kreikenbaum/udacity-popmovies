package com.udacity.serv_inc.popmovies;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;

/**
 * Created on 2/20/18.
 * Taken from https://developer.android.com/guide/topics/ui/layout/gridview.html
 */
class ImageAdapter extends BaseAdapter {
    private static final String TAG = ImageAdapter.class.getSimpleName();
    private final Context context;

    private final List<String> backdrops;

    public ImageAdapter(Context context, List<String> backdrops) {
        this.context = context;
        this.backdrops = backdrops;
    }

    public int getCount() {
        return backdrops.size();
    }

    public Object getItem(int position) {
        return backdrops.get(position);
    }
    
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(context);
//            imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
            imageView.setScaleType(ImageView.ScaleType.FIT_START);
            imageView.setAdjustViewBounds(true);
//            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        // possibly refactor this so that movieSource returns URLs
        String backdropId = backdrops.get(position);
        Uri backdropUri = Utils.posterUri(backdropId);
        Picasso.with(context).load(backdropUri).placeholder(R.mipmap.ic_launcher).into(imageView);
        Log.i(TAG, "Picasso loaded " + backdropUri + " into " + position);
        return imageView;
    }

    public void updateList(List<String> newBackdrops) {
        this.backdrops.clear();
        this.backdrops.addAll(newBackdrops);
        Log.i(TAG, Arrays.toString(this.backdrops.toArray()));
        this.notifyDataSetChanged();
    }
}

