package com.wordpress.rahulhp.freshmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MovieDetailActvity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        if (savedInstanceState == null){
            Bundle arguments = new Bundle();
            arguments.putParcelable("MOVIE",getIntent().getParcelableExtra("MOVIE"));
            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, fragment)
                    .commit();
        }

    }
}
