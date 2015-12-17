package com.wordpress.rahulhp.freshmovies;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

/**
 * Created by root on 17/12/15.
 */
public class MovieAdapter extends BaseAdapter {
    private Context mContext;

    public MovieAdapter(Context c){
        mContext  = c;
    }

    public int getCount() {
        return mThumbIds.length;
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

        imageView.setImageResource(mThumbIds[position]);
        return imageView;
    }

    private Integer[] mThumbIds = {
            R.drawable.art_clear, R.drawable.art_clouds,
            R.drawable.art_fog,R.drawable.art_light_clouds,
            R.drawable.art_light_rain,R.drawable.art_rain
    };
}
