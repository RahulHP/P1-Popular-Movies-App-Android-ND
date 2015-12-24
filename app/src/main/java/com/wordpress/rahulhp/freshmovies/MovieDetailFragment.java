package com.wordpress.rahulhp.freshmovies;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.IOException;

/**
 * Created by RahulHP on 21/12/15.
 */
public class MovieDetailFragment extends Fragment {
    public View rootView;
    private MovieItem mMovieItem;



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

    private void updateReviewsAndTrailers(){
        new FetchObjectTask("videos").execute();
        new FetchObjectTask("reviews").execute();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Activity activity = this.getActivity();
        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
        if (appBarLayout != null) {
            appBarLayout.setTitle(mMovieItem.title);
        }

        rootView = inflater.inflate(R.layout.movie_detail, container, false);

        if (mMovieItem != null){
            addMovieDetails(mMovieItem, rootView);
            updateReviewsAndTrailers();
        }

        return rootView;
    }

    void addMovieDetails(MovieItem mMovieItem,View rootView){

        ((TextView) rootView.findViewById(R.id.movie_release_date)).setText(mMovieItem.release_date.substring(0, 4));
        String rating = mMovieItem.vote_average.toString().concat("/10");
        ((TextView) rootView.findViewById(R.id.movie_vote_average)).setText(rating);
        String url="http://image.tmdb.org/t/p/w342/".concat(mMovieItem.poster_path);
        Picasso.with(getActivity())
                .load(url)
                .into((ImageView) rootView.findViewById(R.id.movie_poster));

        ((TextView) rootView.findViewById(R.id.movie_overview)).setText(mMovieItem.overview);
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

            final String BASE_URL="http://api.themoviedb.org/3/movie/".concat(Long.toString(mMovieItem.id)).concat("/").concat(object_type);
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
            for (Object object : objects){
                final ReviewItem mReview = (ReviewItem) object;
                View mReviewRow = LayoutInflater.from(getActivity()).inflate(R.layout.review_row, null);

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
            for (Object object : objects){
                final TrailerItem mTrailer = (TrailerItem) object;
                View mTrailerRow = LayoutInflater.from(getActivity()).inflate(R.layout.trailer_row, null);

                mTrailerRow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String url = "http://www.youtube.com/watch?v=".concat(mTrailer.key);
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    }
                });

                TextView textView = (TextView) mTrailerRow.findViewById(R.id.trailer_name);
                textView.setText(mTrailer.name);

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
