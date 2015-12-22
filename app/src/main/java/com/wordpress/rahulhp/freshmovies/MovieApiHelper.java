package com.wordpress.rahulhp.freshmovies;

/**
 * Created by root on 22/12/15.
 */
public class MovieApiHelper {
    int page;
    MovieItem[] results;

    public MovieApiHelper(int page, MovieItem[] results) {
        this.page = page;
        this.results = results;
    }

    @Override
    public String toString() {
        return Integer.toString(page).concat(results[0].title);
    }

    public MovieItem[] getResults() {
        return results;
    }
}
