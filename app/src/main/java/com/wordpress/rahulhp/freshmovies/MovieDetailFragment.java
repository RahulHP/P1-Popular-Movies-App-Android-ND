package com.wordpress.rahulhp.freshmovies;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by root on 21/12/15.
 */
public class MovieDetailFragment extends Fragment {

    private MovieItem mMovieItem;
    private ArrayList<TrailerItem> mTrailerList;
    //private TrailerAdapter trailerAdapter;

    public MovieDetailFragment() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("MOVIE",mMovieItem);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        mMovieItem = getArguments().getParcelable("MOVIE");

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments().containsKey("MOVIE")){
            mMovieItem = getArguments().getParcelable("MOVIE");
        }
    }
/*
    private void updateTrailers(){
        FetchTrailerTask trailerTask = new FetchTrailerTask();
        trailerTask.execute();
    }*/
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Activity activity = this.getActivity();
        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
        if (appBarLayout != null) {
            appBarLayout.setTitle(mMovieItem.title);
        }

        View rootView = inflater.inflate(R.layout.movie_detail, container, false);

        if (mMovieItem != null){
            ((TextView) rootView.findViewById(R.id.movie_release_date)).setText(mMovieItem.release_date.substring(0, 4));
            String rating = mMovieItem.vote_average.toString().concat("/10");
            ((TextView) rootView.findViewById(R.id.movie_vote_average)).setText(rating);
            String url="http://image.tmdb.org/t/p/w342/".concat(mMovieItem.poster_path);
            Picasso.with(getActivity())
                    .load(url)
                    .into((ImageView) rootView.findViewById(R.id.movie_poster));

            ((TextView) rootView.findViewById(R.id.movie_overview)).setText(mMovieItem.overview);

            mTrailerList = new ArrayList<TrailerItem>();

            /*updateTrailers();
            ListView trailerListView = (ListView) rootView.findViewById(R.id.trailer_listview);*/
            //trailerAdapter = new TrailerAdaper(getActivity(),mTrailerList);
            //trailerListView.setAdapter(trailerAdapter);




        }

        return rootView;
    }

/*
    public class FetchTrailerTask extends AsyncTask<Void,Void,TrailerItem[]>{
        private final String LOG_TAG = FetchTrailerTask.class.getSimpleName();

        private TrailerItem[] getTrailerListFromJson(String resultJsonstr)
            throws JSONException{

            final String TMDB_TRAILER_MOVIE_ID="id";
            final String TMDB_TRAILER_RESULTS="results";

            final String TMDB_TRAILER_ID="id";
            final String TMDB_TRAILER_KEY="key";
            final String TMDB_TRAILER_NAME="name";
            final String TMDB_TRAILER_SITE="site";
            final String TMDB_TRAILER_TYPE="type";

            JSONObject initialJson = new JSONObject(resultJsonstr);

            long movie_id = initialJson.getLong(TMDB_TRAILER_MOVIE_ID);

            JSONArray resultsArray = initialJson.getJSONArray(TMDB_TRAILER_RESULTS);



        }
    }*/
}
