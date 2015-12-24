package com.wordpress.rahulhp.freshmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.wordpress.rahulhp.freshmovies.Classes.MovieApiHelper;
import com.wordpress.rahulhp.freshmovies.Classes.MovieItem;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieGridFragment extends Fragment {
    private String LOG_TAG = MovieGridFragment.class.getSimpleName();
    private ArrayList<MovieItem> mMovieList;
    private MovieAdapter adapter;
    public MovieGridFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.moviegridfragment,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            updateMovies();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateMovies(){

        FetchMovieTask movieTask = new FetchMovieTask();

        movieTask.execute();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMovieList = new ArrayList<MovieItem>();
        updateMovies();


        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_movies);
        adapter = new MovieAdapter(getActivity(),mMovieList);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(),MovieDetailActvity.class);
                intent.putExtra("MOVIE",mMovieList.get(position));
                startActivity(intent);
            }
        });
        return rootView;
    }


    public class FetchMovieTask extends AsyncTask<Void, Void, MovieItem[]>{
        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

        private MovieItem[] getMovieListFromJson(String resultJsonstr)
            throws JSONException{
            Gson gson = new Gson();
            MovieApiHelper trial = gson.fromJson(resultJsonstr,MovieApiHelper.class);

            return trial.getResults();
        }


        @Override
        protected MovieItem[] doInBackground(Void... params) {

            String resultJsonstr = null;

            SharedPreferences sharedPrefs =
                    PreferenceManager.getDefaultSharedPreferences(getActivity());
            String sort_by_type=sharedPrefs.getString(
                    getString(R.string.pref_sort_order_key),
                    getString(R.string.pref_sort_order_popularity_key));

            final String BASE_URL="http://api.themoviedb.org/3/discover/movie";
            final String SORT_BY="sort_by";
            final String API_KEY = "api_key";

            try {
                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(API_KEY,BuildConfig.TMDB_API_KEY)
                        .appendQueryParameter(SORT_BY,sort_by_type)
                        .build();


                OkHttpClient client = new OkHttpClient();
                Request  request = new Request.Builder()
                        .url(builtUri.toString())
                        .build();
                Response response = client.newCall(request).execute();
                resultJsonstr = response.body().string();

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            }


            try {
                return getMovieListFromJson(resultJsonstr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;

        }

        @Override
        protected void onPostExecute(MovieItem[] movieItems) {
            //Log.e(LOG_TAG,Integer.toString(movieItems.length));
            if (movieItems!=null){
                mMovieList.clear();
                for (MovieItem movie : movieItems){
                    mMovieList.add(movie);
                    //Log.e(LOG_TAG,movie.title);
                }
                adapter.notifyDataSetChanged();

            }
        }
    }




}
