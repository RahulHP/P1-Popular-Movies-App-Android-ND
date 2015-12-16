package com.wordpress.rahulhp.freshmovies;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieGridFragment extends Fragment {

    private ArrayAdapter<String> mGridAdapter;
    public MovieGridFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        String[] movies ={
                "Hello",
                "Test",
                "Movie",
                "Another",
                "Clean",
                "Mobile",
        };
        List<String> movieGrid = new ArrayList<String>(Arrays.asList(movies));

        mGridAdapter= new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_item_movie,
                R.id.list_item_movie_textview,
                movieGrid);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_movies);
        gridView.setAdapter(mGridAdapter);
        return rootView;
    }
}
