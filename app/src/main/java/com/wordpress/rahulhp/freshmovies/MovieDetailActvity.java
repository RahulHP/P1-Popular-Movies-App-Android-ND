package com.wordpress.rahulhp.freshmovies;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wordpress.rahulhp.freshmovies.Classes.MovieItem;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            Set<Long> favMovies;
            Gson gson = new Gson();
            String snackbarString;

            @Override
            public void onClick(View view) {
                String favMoviesJson = pref.getString("FAVS", null);
                if (favMoviesJson != null) {
                    Type type = new TypeToken<HashSet<Long>>(){}.getType();
                    favMovies = gson.fromJson(favMoviesJson,type);
                } else {
                    favMovies = new HashSet<Long>();
                }

                if (favMovies.contains(movie_id)){
                    snackbarString="Removing movie from favourites.";
                    favMovies.remove(movie_id);
                } else {
                    snackbarString="Adding movie to favourites.";
                    favMovies.add(movie_id);
                }

                Snackbar.make(view,snackbarString, Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
                favMoviesJson = gson.toJson(favMovies);
                editor.putString("FAVS",favMoviesJson);
                editor.commit();



            }
        });

    }
}
