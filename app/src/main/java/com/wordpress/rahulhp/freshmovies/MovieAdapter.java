package com.wordpress.rahulhp.freshmovies;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by root on 17/12/15.
 */
public class MovieAdapter extends BaseAdapter {
    private String LOG_TAG = MovieAdapter.class.getSimpleName();
    private Context mContext;
    private ArrayList<MovieItem> mMovieList;
    private Picasso builder;

    public MovieAdapter(Context c,ArrayList<MovieItem> mMovieList){
        this.mContext  = c;
        this.mMovieList = mMovieList;
        this.builder = Picasso.with(c);
        builder.setIndicatorsEnabled(true);

    }

    public int getCount() {
        return mMovieList.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);

        } else {
            imageView = (ImageView) convertView;
        }
        String url="http://image.tmdb.org/t/p/w342/".concat(mMovieList.get(position).poster_path);
        Log.v(LOG_TAG, url);

        builder.with(mContext)
                .load(url)
                .into(imageView);
        imageView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        return imageView;
    }

}
