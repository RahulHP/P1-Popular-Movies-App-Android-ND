package com.wordpress.rahulhp.freshmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class MovieDetailActvity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        //setSupportActionBar(toolbar);

        //ActionBar actionBar = getSupportActionBar();
        //if (actionBar != null) {
        //    actionBar.setDisplayHomeAsUpEnabled(true);
        //}


        if (savedInstanceState == null){
            Bundle arguments = new Bundle();
            MovieItem mMovie = getIntent().getParcelableExtra("MOVIE");
            Log.v("MOVIED",mMovie.title);
            arguments.putParcelable("MOVIE",getIntent().getParcelableExtra("MOVIE"));
            Log.v("MOVIED-Test",arguments.getParcelable("MOVIE").toString());
            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, fragment)
                    .commit();
        }

    }
}
