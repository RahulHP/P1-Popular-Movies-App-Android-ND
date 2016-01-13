package com.wordpress.rahulhp.freshmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.wordpress.rahulhp.freshmovies.Classes.MovieItem;

public class MovieDetailActvity extends AppCompatActivity {
    Long movie_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        if (savedInstanceState == null){
            Bundle arguments = new Bundle();
            MovieItem movie = (MovieItem) getIntent().getParcelableExtra("MOVIE");
            movie_id = movie.getId();
            arguments.putParcelable("MOVIE",getIntent().getParcelableExtra("MOVIE"));
            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, fragment)
                    .commit();
        }
    }
}
