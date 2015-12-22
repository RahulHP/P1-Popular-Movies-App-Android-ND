package com.wordpress.rahulhp.freshmovies;

/**
 * Created by root on 22/12/15.
 */
public class TrailerItem {
    Long movie_id;
    String id;
    String key;
    String name;
    String type;
    String site;

    public TrailerItem(Long movie_id, String id, String key, String name, String type, String site) {
        this.movie_id = movie_id;
        this.id = id;
        this.key = key;
        this.name = name;
        this.type = type;
        this.site = site;
    }
}
