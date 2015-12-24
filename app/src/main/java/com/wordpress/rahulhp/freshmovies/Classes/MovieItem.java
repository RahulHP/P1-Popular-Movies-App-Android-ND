package com.wordpress.rahulhp.freshmovies.Classes;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by root on 17/12/15.
 */
public class MovieItem implements Parcelable{
    Long id;
    String title;
    String overview;
    String release_date;
    String poster_path;
    Double popularity;
    Double vote_average;

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getOverview() {
        return overview;
    }

    public String getRelease_date() {
        return release_date;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public Double getPopularity() {
        return popularity;
    }

    public Double getVote_average() {
        return vote_average;
    }

    MovieItem(Long id,String title,String overview,String release_date,String poster_path, Double popularity, Double vote_average){
        this.id=id;
        this.title = title;
        this.overview=overview;
        this.release_date=release_date;
        this.poster_path=poster_path;
        this.popularity=popularity;
        this.vote_average=vote_average;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(overview);
        dest.writeString(release_date);
        dest.writeString(poster_path);
        dest.writeDouble(popularity);
        dest.writeDouble(vote_average);
    }

    @Override
    public String toString() {
        return title;
    }
    private MovieItem(Parcel in){
        this.id= in.readLong();
        this.title =in.readString();
        this.overview=in.readString();
        this.release_date=in.readString();
        this.poster_path=in.readString();
        this.popularity=in.readDouble();
        this.vote_average=in.readDouble();
    }

    public static final Parcelable.Creator<MovieItem> CREATOR = new Parcelable.Creator<MovieItem>(){
        @Override
        public MovieItem createFromParcel(Parcel source) {
            return new MovieItem(source);
        }

        @Override
        public MovieItem[] newArray(int size) {
            return new MovieItem[size];
        }
    };



}
