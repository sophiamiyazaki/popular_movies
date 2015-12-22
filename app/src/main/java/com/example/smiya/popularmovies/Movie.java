package com.example.smiya.popularmovies;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by smiya on 12/6/15.
 */
public class Movie {
    String title;
    String releaseDate;
    String posterPath;
    String overview;
    int voteAverage;

    public Movie(JSONObject movie) throws JSONException {
        this.title = movie.getString("original_title");
        this.releaseDate = movie.getString("release_date");
        this.posterPath = movie.getString("poster_path");
        this.overview = movie.getString("overview");
        this.voteAverage = movie.getInt("vote_average");
    }

    public String getTitle() {
        return title;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getOverview() {
        return overview;
    }

    public int getVoteAverage() {
        return voteAverage;
    }
}
