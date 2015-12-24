package com.wordpress.rahulhp.freshmovies.Classes;

/**
 * Created by root on 24/12/15.
 */
public class ReviewItem {
    String id;
    String author;
    String content;
    String url;

    public ReviewItem(String id, String author, String content, String url) {
        this.id = id;
        this.author = author;
        this.content = content;
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public String getUrl() {
        return url;
    }
}
