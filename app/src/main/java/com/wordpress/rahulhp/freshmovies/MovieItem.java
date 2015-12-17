package com.wordpress.rahulhp.freshmovies;

/**
 * Created by root on 17/12/15.
 */
public class MovieItem {
    String id;
    String original_title;
    String overview;
    String release_date;
    String poster_path;
    String vote_average;
    String popularity;

    MovieItem(String id,String original_title,String overview,String release_date,String poster_path, String vote_average){
        this.id=id;
        this.original_title=original_title;
        this.overview=overview;
        this.release_date=release_date;
        this.poster_path=poster_path;
        this.vote_average=vote_average;
        this.popularity=popularity;
    }

}
