package com.wordpress.rahulhp.freshmovies.Classes;

import com.wordpress.rahulhp.freshmovies.Classes.ReviewItem;

/**
 * Created by root on 24/12/15.
 */
public class ReviewApiHelper {
    Long id;
    Long page;
    ReviewItem[] results;
    Long total_pages;
    Long total_results;

    public ReviewApiHelper(Long id, Long page, ReviewItem[] results, Long total_pages, Long total_results) {
        this.id = id;
        this.page = page;
        this.results = results;
        this.total_pages = total_pages;
        this.total_results = total_results;
    }

    public Long getId() {
        return id;
    }

    public Long getPage() {
        return page;
    }

    public ReviewItem[] getResults() {
        return results;
    }

    public Long getTotal_pages() {
        return total_pages;
    }

    public Long getTotal_results() {
        return total_results;
    }
}
