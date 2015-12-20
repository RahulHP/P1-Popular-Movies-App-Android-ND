package com.wordpress.rahulhp.freshmovies;

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
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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
        return rootView;
    }


    public class FetchMovieTask extends AsyncTask<Void, Void, MovieItem[]>{
        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

        private MovieItem[] getMovieListFromJson(String resultJsonstr)
            throws JSONException{

            final String TMDB_RESULTS="results";

            final String TMDB_POSTER_PATH = "poster_path";
            final String TMDB_ID = "id";
            final String TMDB_TITLE = "title";
            final String TMDB_OVERVIEW = "overview";
            final String TMDB_RELEASE_DATE = "release_date";
            final String TMDB_POPULARITY = "popularity";



            JSONObject initialJson = new JSONObject(resultJsonstr);
            JSONArray resultsArray = initialJson.getJSONArray(TMDB_RESULTS);

            MovieItem[] results = new MovieItem[resultsArray.length()];

            for (int i=0 ; i< results.length;i++){
                JSONObject movieJson = resultsArray.getJSONObject(i);

                String poster_path = movieJson.getString(TMDB_POSTER_PATH);
                Long id = movieJson.getLong(TMDB_ID);
                String original_title = movieJson.getString(TMDB_TITLE);
                String overview = movieJson.getString(TMDB_OVERVIEW);
                String release_date = movieJson.getString(TMDB_RELEASE_DATE);
                Double popularity = movieJson.getDouble(TMDB_POPULARITY);

                results[i] = new MovieItem(id,original_title,overview,release_date,poster_path, popularity);

            }
            return results;
        }

        @Override
        protected MovieItem[] doInBackground(Void... params) {

            SharedPreferences sharedPrefs =
                    PreferenceManager.getDefaultSharedPreferences(getActivity());



            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String resultJsonstr = null;

            String sort_by_type=sharedPrefs.getString(
                    getString(R.string.pref_sort_order_key),
                    getString(R.string.pref_sort_order_popularity_key));

            try {
                final String BASE_URL="http://api.themoviedb.org/3/discover/movie";
                final String SORT_BY="sort_by";
                final String API_KEY = "api_key";

                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(API_KEY,BuildConfig.TMDB_API_KEY)
                        .appendQueryParameter(SORT_BY,sort_by_type)
                        .build();

                URL url = new URL(builtUri.toString());
                Log.v(LOG_TAG,url.toString());
                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }

                resultJsonstr = buffer.toString();

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            Log.d(LOG_TAG,resultJsonstr);
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
            if (movieItems!=null){
                mMovieList.clear();

                for (MovieItem movie : movieItems){
                    mMovieList.add(movie);
                }
                adapter.notifyDataSetChanged();

            }
        }
    }




}
