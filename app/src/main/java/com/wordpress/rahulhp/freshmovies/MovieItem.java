package com.wordpress.rahulhp.freshmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by root on 17/12/15.
 */
public class MovieItem implements Parcelable{
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
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(original_title);
        dest.writeString(overview);
        dest.writeString(release_date);
        dest.writeString(poster_path);
        dest.writeDouble(popularity);
    }

    @Override
    public String toString() {
        return original_title;
    }
    private MovieItem(Parcel in){
        this.id= in.readLong();
        this.original_title=in.readString();
        this.overview=in.readString();
        this.release_date=in.readString();
        this.poster_path=in.readString();
        this.popularity=in.readDouble();
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
