package com.wordpress.rahulhp.freshmovies;

/**
 * Created by root on 23/12/15.
 */
public class TrailerApiHelper {
    int id;
    TrailerItem[] results;

    public TrailerApiHelper(int id, TrailerItem[] results) {
        this.id = id;
        this.results = results;
    }

    public TrailerItem[] getResults() {
        return results;
    }

    public String Length(){
        return "Length is" + results.length;
    }
    @Override
    public String toString() {
        return "TrailerApiHelper{" +
                "id=" + id +
                '}';
    }
}
