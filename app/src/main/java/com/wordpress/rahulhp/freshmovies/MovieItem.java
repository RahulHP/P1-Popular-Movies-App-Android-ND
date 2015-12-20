package com.wordpress.rahulhp.freshmovies;

/**
 * Created by root on 17/12/15.
 */
public class MovieItem {
    Long id;
    String original_title;
    String overview;
    String release_date;
    String poster_path;
    Double popularity;

    MovieItem(Long id,String original_title,String overview,String release_date,String poster_path, Double popularity){
        this.id=id;
        this.original_title=original_title;
        this.overview=overview;
        this.release_date=release_date;
        this.poster_path=poster_path;
        this.popularity=popularity;
    }

    @Override
    public String toString() {
        return original_title;
    }
}
