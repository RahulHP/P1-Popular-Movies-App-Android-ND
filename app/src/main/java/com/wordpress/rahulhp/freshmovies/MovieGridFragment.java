package com.wordpress.rahulhp.freshmovies;

import android.content.Context;
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
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.wordpress.rahulhp.freshmovies.Classes.MovieApiHelper;
import com.wordpress.rahulhp.freshmovies.Classes.MovieItem;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieGridFragment extends Fragment {
    private String LOG_TAG = MovieGridFragment.class.getSimpleName();
    private ArrayList<MovieItem> mMovieList;
    private MovieAdapter adapter;
    private boolean mTwoPane=false;
    public MovieGridFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
        if (getArguments().containsKey("mTwoPane")){
            mTwoPane = getArguments().getBoolean("mTwoPane");
            /*if (mTwoPane){
                Log.e(LOG_TAG,"TWO PANES");
            } else {
                Log.e(LOG_TAG,"SINGLE PANE");
            }*/
        }
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
                if (mTwoPane){
                    //Log.e(LOG_TAG,"TWO PANESa");
                    Bundle arguments = new Bundle();
                    arguments.putParcelable("MOVIE",mMovieList.get(position));
                    MovieDetailFragment fragment = new MovieDetailFragment();
                    fragment.setArguments(arguments);
                    //Log.e(LOG_TAG, "Launching");
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.movie_detail_container,fragment)
                            .commit();
                } else {
                    //Log.e(LOG_TAG,"SINGLE PANEa");
                Intent intent = new Intent(getContext(),MovieDetailActvity.class);
                intent.putExtra("MOVIE",mMovieList.get(position));
                startActivity(intent);}

            }
        });
        return rootView;
    }


    public class FetchMovieTask extends AsyncTask<Void, Void, MovieItem[]>{
        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

        private MovieItem[] getMovieListFromJson(String resultJsonstr) {
            Gson gson = new Gson();
            try {
                MovieApiHelper trial = gson.fromJson(resultJsonstr,MovieApiHelper.class);
                return trial.getResults();
            }
            catch (Exception e){
                Log.e(LOG_TAG, "Error ", e);
                return null;
            }
        }

        private MovieItem getSingleMovieFromJson(String resultJsonstr) {
            Gson gson = new Gson();
            try {
                MovieItem movie = gson.fromJson(resultJsonstr,MovieItem.class);
                return movie;
            } catch (Exception e){
                Log.e(LOG_TAG, "Error ", e);
                return null;
            }

        }

        private String returnJsonResponse(Uri requestUri){
            try {
                OkHttpClient client = new OkHttpClient();
                Request  request = new Request.Builder()
                        .url(requestUri.toString())
                        .build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            }  catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            }
        }
        @Override
        protected MovieItem[] doInBackground(Void... params) {

            String resultJsonstr;

            SharedPreferences sharedPrefs =
                    PreferenceManager.getDefaultSharedPreferences(getActivity());
            String sort_by_type=sharedPrefs.getString(
                    getString(R.string.pref_sort_order_key),
                    getString(R.string.pref_sort_order_popularity_key));

            if (sort_by_type==getString(R.string.pref_sort_order_highest_rated_key) ||
                    sort_by_type == getString(R.string.pref_sort_order_popularity_key)){

                final String BASE_URL="http://api.themoviedb.org/3/discover/movie";
                final String SORT_BY="sort_by";
                final String API_KEY = "api_key";

                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(API_KEY,BuildConfig.TMDB_API_KEY)
                        .appendQueryParameter(SORT_BY,sort_by_type)
                        .build();

                resultJsonstr = returnJsonResponse(builtUri);
                return getMovieListFromJson(resultJsonstr);

            } else {
                SharedPreferences pref = getActivity().getApplicationContext()
                        .getSharedPreferences("MyPref", Context.MODE_PRIVATE);
                Set<Long> favMovies;
                Gson gson = new Gson();
                String favMoviesJson = pref.getString("FAVS", null);

                if (favMoviesJson == null) {return null;}

                Type type = new TypeToken<HashSet<Long>>(){}.getType();
                favMovies = gson.fromJson(favMoviesJson,type);
                MovieItem[] movieItems = new MovieItem[favMovies.size()];
                int count=0;
                for (Long movie_id : favMovies){
                    final String BASE_URL="http://api.themoviedb.org/3/movie";
                    final String API_KEY = "api_key";
                    Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                            .appendPath(Long.toString(movie_id))
                            .appendQueryParameter(API_KEY, BuildConfig.TMDB_API_KEY)
                            .build();

                    resultJsonstr = returnJsonResponse(builtUri);
                    movieItems[count]=getSingleMovieFromJson(resultJsonstr);
                    count++;
                }
                return movieItems;
            }
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
