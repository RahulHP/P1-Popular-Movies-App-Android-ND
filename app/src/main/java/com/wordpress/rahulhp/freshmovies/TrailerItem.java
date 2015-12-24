package com.wordpress.rahulhp.freshmovies;

/**
 * Created by root on 22/12/15.
 */
public class TrailerItem {
    String id;
    String iso_639_1;
    String key;
    String name;
    String site;
    int size;
    String type;


    TrailerItem(String id, String iso_639_1, String key, String name, String site, int size, String type) {
        this.id = id;
        this.iso_639_1 = iso_639_1;
        this.key = key;
        this.name = name;
        this.site = site;
        this.size = size;
        this.type = type;
    }

}
