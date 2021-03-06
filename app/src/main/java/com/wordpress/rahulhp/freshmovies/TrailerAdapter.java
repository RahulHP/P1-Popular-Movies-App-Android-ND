package com.wordpress.rahulhp.freshmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wordpress.rahulhp.freshmovies.Classes.TrailerItem;

import java.util.ArrayList;

/**
 * Created by root on 23/12/15.
 */
public class TrailerAdapter extends BaseAdapter {
    private final String LOG_TAG = TrailerAdapter.class.getSimpleName();
    private Context mContext;
    private ArrayList<TrailerItem> mTrailerList;

    public TrailerAdapter(Context mContext, ArrayList<TrailerItem> mTrailerList) {
        this.mContext = mContext;
        this.mTrailerList = mTrailerList;
    }

    public int getCount() {
        return mTrailerList.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.trailer_row,parent,false);
        }

        textView = (TextView) convertView.findViewById(R.id.trailer_name);
        textView.setText(mTrailerList.get(position).getName());

        return convertView;
    }
}
