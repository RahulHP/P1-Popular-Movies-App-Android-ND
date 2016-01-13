package com.wordpress.rahulhp.freshmovies;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;
import com.wordpress.rahulhp.freshmovies.Classes.MovieItem;
import com.wordpress.rahulhp.freshmovies.Classes.ReviewApiHelper;
import com.wordpress.rahulhp.freshmovies.Classes.ReviewItem;
import com.wordpress.rahulhp.freshmovies.Classes.TrailerApiHelper;
import com.wordpress.rahulhp.freshmovies.Classes.TrailerItem;

import org.json.JSONException;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by RahulHP on 21/12/15.
 */
public class MovieDetailFragment extends Fragment {
    public View rootView;
    private MovieItem mMovieItem;
    private String LOG_TAG = MovieDetailFragment.class.getSimpleName();

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
        Activity activity = this.getActivity();
        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
        if (appBarLayout != null) {
            appBarLayout.setTitle(mMovieItem.getTitle());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //Log.e(LOG_TAG,"Creating");
        super.onCreate(savedInstanceState);
        if(getArguments().containsKey("MOVIE")){
            mMovieItem = getArguments().getParcelable("MOVIE");
            //assert mMovieItem != null;
            //Log.e(LOG_TAG,mMovieItem.getTitle());
            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mMovieItem.getTitle());
            }
        }
    }

    private void updateReviewsAndTrailers(){
        new FetchObjectTask("videos").execute();
        new FetchObjectTask("reviews").execute();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {



        rootView = inflater.inflate(R.layout.movie_detail, container, false);

        if (mMovieItem != null){
            addMovieDetails(mMovieItem, rootView);
            updateReviewsAndTrailers();
        }


        Button favButton = (Button) rootView.findViewById(R.id.favButton);

        favButton.setOnClickListener(new View.OnClickListener() {
            SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            Set<Long> favMovies;
            Gson gson = new Gson();
            String snackbarString;
            @Override
            public void onClick(View v) {
                String favMoviesJson = pref.getString("FAVS",null);

                if (favMoviesJson != null) {
                    Type type = new TypeToken<HashSet<Long>>(){}.getType();
                    favMovies = gson.fromJson(favMoviesJson,type);
                } else {
                    favMovies = new HashSet<Long>();
                }


                if (favMovies.contains(mMovieItem.getId())){
                    snackbarString="Removing movie from favourites.";
                    favMovies.remove(mMovieItem.getId());
                } else {
                    snackbarString="Adding movie to favourites.";
                    favMovies.add(mMovieItem.getId());
                }

                Snackbar.make(rootView, snackbarString, Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
                favMoviesJson = gson.toJson(favMovies);
                editor.putString("FAVS",favMoviesJson);
                editor.commit();

            }
        });
        return rootView;
    }

    void addMovieDetails(MovieItem mMovieItem,View rootView){
        Log.e(LOG_TAG, "Adding movie details");
        ((TextView) rootView.findViewById(R.id.movie_release_date)).setText(mMovieItem.getRelease_date().substring(0, 4));
        String rating = mMovieItem.getVote_average().toString().concat("/10");
        ((TextView) rootView.findViewById(R.id.movie_vote_average)).setText(rating);
        String url="http://image.tmdb.org/t/p/w342/".concat(mMovieItem.getPoster_path());
        Picasso.with(getActivity())
                .load(url)
                .into((ImageView) rootView.findViewById(R.id.movie_poster));

        ((TextView) rootView.findViewById(R.id.movie_overview)).setText(mMovieItem.getOverview());


    }

    public class FetchObjectTask extends AsyncTask<String,Void,Object[]>{
        final String LOG_TAG = FetchObjectTask.class.getSimpleName();
        String object_type;

        public FetchObjectTask(String object_type) {
            this.object_type = object_type;
        }

        private Object[] getItemListFromJson (String resultJsonstr) throws JSONException{
            Gson gson = new Gson();
            if (object_type.equals("reviews")){
                ReviewApiHelper reviewApi = gson.fromJson(resultJsonstr,ReviewApiHelper.class);
                return reviewApi.getResults();
            } else {
                TrailerApiHelper trailerApi = gson.fromJson(resultJsonstr,TrailerApiHelper.class);
                return trailerApi.getResults();
            }
        }

        @Override
        protected Object[] doInBackground(String... params) {

            String resultJsonstr;

            final String BASE_URL="http://api.themoviedb.org/3/movie/".concat(Long.toString(mMovieItem.getId())).concat("/").concat(object_type);
            final String API_KEY = "api_key";

            try {

                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(API_KEY,BuildConfig.TMDB_API_KEY)
                        .build();
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(builtUri.toString())
                        .build();
                Response response = client.newCall(request).execute();
                resultJsonstr = response.body().string();
            }
            catch (IOException e){
                Log.e(LOG_TAG, "Error ", e);
                return null;
            }

            try {
                return getItemListFromJson(resultJsonstr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        void createReviewView(Object[] objects){
            LinearLayout movie_detail_layout = (LinearLayout) rootView.findViewById(R.id.main_movie_layout);

            View headerView = LayoutInflater.from(getActivity()).inflate(R.layout.movie_detail_header, null);
            TextView headerText = (TextView) headerView.findViewById(R.id.header);
            headerText.setText("Reviews");
            movie_detail_layout.addView(headerView);

            for (Object object : objects){
                final ReviewItem mReview = (ReviewItem) object;
                LayoutInflater inflater = LayoutInflater.from(getContext());
                //LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View mReviewRow = inflater.inflate(R.layout.review_row, null);

                mReviewRow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mReview.getUrl())));
                    }
                });
                String review_text;
                TextView textView = (TextView) mReviewRow.findViewById(R.id.review_text);
                if (mReview.getContent().length() <= 498){
                    review_text = mReview.getContent();
                }
                else {
                    review_text = mReview.getContent().substring(0,500);
                }

                textView.setText(review_text);

                movie_detail_layout.addView(mReviewRow);
            }
        }

        void createTrailerView(Object[] objects){
            LinearLayout movie_detail_layout = (LinearLayout) rootView.findViewById(R.id.main_movie_layout);

            View headerView = LayoutInflater.from(getActivity()).inflate(R.layout.movie_detail_header, null);
            TextView headerText = (TextView) headerView.findViewById(R.id.header);
            headerText.setText("Trailers");
            movie_detail_layout.addView(headerView);


            for (Object object : objects){
                final TrailerItem mTrailer = (TrailerItem) object;
                View mTrailerRow = LayoutInflater.from(getActivity()).inflate(R.layout.trailer_row, null);

                mTrailerRow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String url = "http://www.youtube.com/watch?v=".concat(mTrailer.getKey());
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    }
                });

                TextView textView = (TextView) mTrailerRow.findViewById(R.id.trailer_name);
                textView.setText(mTrailer.getName());

                movie_detail_layout.addView(mTrailerRow);
            }
        }
        
        @Override
        protected void onPostExecute(Object[] objects) {
            if (objects != null){
                if (object_type.equals("reviews")){
                    createReviewView(objects);
                } else {
                    createTrailerView(objects);
                }
            }
        }
    }

}
