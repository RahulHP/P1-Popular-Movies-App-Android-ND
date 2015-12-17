package com.wordpress.rahulhp.freshmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieGridFragment extends Fragment {

    private ArrayList<MovieItem> mMovieAdapter;
    private ArrayAdapter<String> mGridAdapter;
    public MovieGridFragment(){}

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    private void updateMovies(){
        FetchMovieTask movieTask = new FetchMovieTask();
        movieTask.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String[] movies ={
                "Hello",
                "Test",
                "Movie",
                "Another",
                "Clean",
                "Mobile",
        };
        List<String> movieGrid = new ArrayList<String>(Arrays.asList(movies));

        mGridAdapter= new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_item_movie,
                R.id.list_item_movie_textview,
                movieGrid);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_movies);
        gridView.setAdapter(new MovieAdapter(getActivity()));

        //ImageView imageView = (ImageView) rootView.findViewById(R.id.imageView);
        //Picasso.with(getActivity())
        //        .load("https://cms-assets.tutsplus.com/uploads/users/21/posts/19431/featured_image/CodeFeature.jpg")
        //        .into(imageView);
        return rootView;
    }


    public class FetchMovieTask extends AsyncTask<Void, Void, MovieItem[]>{
        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

        private MovieItem[] getMovieListFromJson(String resultJsonstr){

            return  null;
        }

        @Override
        protected MovieItem[] doInBackground(Void... params) {
            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String resultJsonstr = null;

            String sort_by_type="popularity.desc";
            String api_key="xyz";
            try {
                final String BASE_URL="http://api.themoviedb.org/3/discover/movie";
                final String SORT_BY="sort_by";
                final String API_KEY = "api_key";

                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(API_KEY,api_key)
                        .appendQueryParameter(SORT_BY,sort_by_type)
                        .build();

                URL url = new URL(builtUri.toString());

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

            //try {
                return getMovieListFromJson(resultJsonstr);
            //} catch (JSONException e) {
            //    Log.e(LOG_TAG, e.getMessage(), e);
            //    e.printStackTrace();
            //}

            // This will only happen if there was an error getting or parsing the forecast.
            //return null;

        }

        @Override
        protected void onPostExecute(MovieItem[] movieItems) {
            if (movieItems!=null){
                mMovieAdapter.clear();
                for (MovieItem movie : movieItems){
                    mMovieAdapter.add(movie);
                }
            }
        }
    }




}
