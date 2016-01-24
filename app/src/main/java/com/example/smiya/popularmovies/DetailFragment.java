package com.example.smiya.popularmovies;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DetailFragment extends Fragment {

    Date sourceDate = null;
    String displayReleaseDate = "";
    RatingBar ratingBar;

    public DetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        // Get the movie object in passed as a Parcelable in the intent and parse the pieces
        Bundle b = getActivity().getIntent().getExtras();
        Movie movieItem = b.getParcelable("com.package.Movie");
        String movieTitle = movieItem.getTitle();
        String movieReleaseDate = movieItem.getReleaseDate();
//        String moviePosterPath = movieItem.getPosterPath();
        String movieBackdropPath = movieItem.getBackdropPath();
        String movieOverview = movieItem.getOverview();
//        String movieVoteAverage = String.valueOf(movieItem.getVoteAverage());
        ratingBar = (RatingBar) rootView.findViewById(R.id.detail_rating_bar);

        displayReleaseDate = "(" + getOutputDateString(movieReleaseDate) + ")";

        ((TextView) rootView.findViewById(R.id.detail_title)).setText(movieTitle);
        ((TextView) rootView.findViewById(R.id.detail_release_date)).setText(displayReleaseDate);
        ((TextView) rootView.findViewById(R.id.detail_synopsis)).setText(movieOverview);
//        ((TextView) rootView.findViewById(R.id.detail_vote_average)).setText(movieVoteAverage);
//        ImageView posterImageView = (ImageView) rootView.findViewById(R.id.detail_poster_image);
        ImageView backdropImageView = (ImageView) rootView.findViewById(R.id.detail_backdrop_image);

        float rating = (float) (movieItem.getVoteAverage()/2);
        ratingBar.setRating(rating);

        // Populate the movie poster imageview
        final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
        final String POSTER_WIDTH = "w300";
//        final String FULL_POSTER_URL = POSTER_BASE_URL + POSTER_WIDTH + moviePosterPath;
        final String FULL_BACKDROP_URL = POSTER_BASE_URL + POSTER_WIDTH + movieBackdropPath;
//        Log.e("TEST",FULL_POSTER_URL);
//        Log.e("BACKDROP", FULL_BACKDROP_URL);
//        Picasso.with(getActivity()).load(FULL_POSTER_URL).into(posterImageView);
        Picasso.with(getActivity()).load(FULL_BACKDROP_URL).into(backdropImageView);

        return rootView;
    }

    private String getOutputDateString(String movieReleaseDate) {
        SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            sourceDate = sourceFormat.parse(movieReleaseDate);
            SimpleDateFormat destinationFormat = new SimpleDateFormat("MMM yyyy");
            displayReleaseDate = destinationFormat.format(sourceDate);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return displayReleaseDate;
    }
}
