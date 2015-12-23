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
import java.util.ArrayList;

/**
 * Created by root on 21/12/15.
 */
public class MovieDetailFragment extends Fragment {
    public View rootView;
    private MovieItem mMovieItem;
    private ArrayList<TrailerItem> mTrailerList;
    private ArrayList<ReviewItem> mReviewList;


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

    private void updateTrailers(){
        FetchTrailerTask trailerTask = new FetchTrailerTask();
        trailerTask.execute();

        FetchMiscTask miscTask = new FetchMiscTask();
        miscTask.execute();
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

            mTrailerList = new ArrayList<TrailerItem>();
            mReviewList = new ArrayList<ReviewItem>();

            updateTrailers();


            ((TextView) rootView.findViewById(R.id.movie_release_date)).setText(mMovieItem.release_date.substring(0, 4));
            String rating = mMovieItem.vote_average.toString().concat("/10");
            ((TextView) rootView.findViewById(R.id.movie_vote_average)).setText(rating);
            String url="http://image.tmdb.org/t/p/w342/".concat(mMovieItem.poster_path);
            Picasso.with(getActivity())
                    .load(url)
                    .into((ImageView) rootView.findViewById(R.id.movie_poster));

            ((TextView) rootView.findViewById(R.id.movie_overview)).setText(mMovieItem.overview);

        }

        return rootView;
    }

    public class FetchMiscTask extends AsyncTask<Void,Void,ReviewItem[]>{
        private final String LOG_TAG = FetchMiscTask.class.getSimpleName();

        private ReviewItem[] getTrailerListFromJson(String resultJsonstr)
                throws JSONException {
            Log.e(LOG_TAG,resultJsonstr);
            Gson gson = new Gson();
            ReviewApiHelper reviewApi = gson.fromJson(resultJsonstr,ReviewApiHelper.class);
            Log.e(LOG_TAG,Integer.toString(reviewApi.getResults().length));
            return reviewApi.getResults();
        }

        @Override
        protected ReviewItem[] doInBackground(Void... params) {
            String resultJsonstr = null;

            final String BASE_URL="http://api.themoviedb.org/3/movie/".concat(Long.toString(mMovieItem.id)).concat("/reviews");
            final String API_KEY = "api_key";

            try {

                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(API_KEY,BuildConfig.TMDB_API_KEY)
                        .build();
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(builtUri.toString())
                        .build();
                //Log.e(LOG_TAG,builtUri.toString());
                Response response = client.newCall(request).execute();
                resultJsonstr = response.body().string();
            }
            catch (IOException e){
                Log.e(LOG_TAG, "Error ", e);
                return null;
            }

            try {
                return getTrailerListFromJson(resultJsonstr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;

        }

        @Override
        protected void onPostExecute(ReviewItem[] reviewItems) {
            Log.e(LOG_TAG,"In onPostExecute");
            Log.e(LOG_TAG,Integer.toString(reviewItems.length));
            if (reviewItems!=null){
                mReviewList.clear();
                for (ReviewItem reviewItem:reviewItems){
                    Log.e(LOG_TAG,reviewItem.getAuthor());
                    mReviewList.add(reviewItem);
                }
            }

            //https://discussions.udacity.com/t/what-is-the-best-way-to-show-trailers-and-reviews/33464/6?u=rahulhp
            LinearLayout trailer_trial = (LinearLayout) rootView.findViewById(R.id.main_movie_layout);
            for (final ReviewItem mReview : mReviewList){
                View mReviewRow = LayoutInflater.from(getActivity()).inflate(R.layout.review_row, null);

                mReviewRow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mReview.getUrl())));
                    }
                });
                String review_text=null;
                TextView textView = (TextView) mReviewRow.findViewById(R.id.review_text);
                if (mReview.getContent().length() <= 498){
                    review_text = mReview.getContent();
                }
                else {
                review_text = mReview.getContent().substring(0,500);
                }
                Log.e(LOG_TAG,review_text);
                textView.setText(review_text);

                trailer_trial.addView(mReviewRow);
            }

        }
    }
    public class FetchTrailerTask extends AsyncTask<Void,Void,TrailerItem[]> {
        private final String LOG_TAG = FetchTrailerTask.class.getSimpleName();

        private TrailerItem[] getTrailerListFromJson(String resultJsonstr)
            throws JSONException {
            //Log.e(LOG_TAG,resultJsonstr);
            Gson gson = new Gson();
            TrailerApiHelper trailerApi = gson.fromJson(resultJsonstr,TrailerApiHelper.class);
            //Log.e(LOG_TAG,trailerApi.Length());
            return trailerApi.getResults();
        }

        @Override
        protected TrailerItem[] doInBackground(Void... params) {
            String resultJsonstr = null;

            final String BASE_URL="http://api.themoviedb.org/3/movie/".concat(Long.toString(mMovieItem.id)).concat("/videos");
            final String API_KEY = "api_key";

            try {

                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(API_KEY,BuildConfig.TMDB_API_KEY)
                        .build();
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(builtUri.toString())
                        .build();
                //Log.e(LOG_TAG,builtUri.toString());
                Response response = client.newCall(request).execute();
                resultJsonstr = response.body().string();
            }
            catch (IOException e){
                Log.e(LOG_TAG, "Error ", e);
                return null;
            }

            try {
                return getTrailerListFromJson(resultJsonstr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;

        }

        @Override
        protected void onPostExecute(TrailerItem[] trailerItems) {

            if (trailerItems!=null){
                mTrailerList.clear();
                for (TrailerItem trailerItem:trailerItems){
                    mTrailerList.add(trailerItem);
                }
                //trailerAdapter.notifyDataSetChanged();

            }
            //https://discussions.udacity.com/t/what-is-the-best-way-to-show-trailers-and-reviews/33464/6?u=rahulhp
            LinearLayout trailer_trial = (LinearLayout) rootView.findViewById(R.id.main_movie_layout);
            for (final TrailerItem mTrailer : mTrailerList){
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

                trailer_trial.addView(mTrailerRow);
            }

        }
    }
}
