package com.example.smiya.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by smiya on 12/6/15.
 */
public class MoviePosterAdapter extends BaseAdapter {
    private final String LOG_TAG = MoviePosterAdapter.class.getSimpleName();

    private ArrayList<Movie> mMovies;
    private final Context mContext;
    private final LayoutInflater mInflater;

    public MoviePosterAdapter(Context context, ArrayList<Movie> movies) {
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMovies = movies;
    }

    @Override
    public int getCount() {
        return mMovies.size();
    }

    @Override
    public Movie getItem(int position) {
        return mMovies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        Log.v(LOG_TAG, "in Adapter getView");

        final Movie movie = getItem(position);
        String moviePosterPath;

        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate( R.layout.movie_poster, parent, false );
            holder.image = (ImageView) convertView.findViewById( R.id.movie_poster_item );
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // add the build image to the holder
        moviePosterPath = movie.getPosterPath();
        final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
        final String POSTER_WIDTH = "w185";
        final String FULL_POSTER_URL = POSTER_BASE_URL + POSTER_WIDTH + moviePosterPath;
//        Log.v(LOG_TAG, "Full Poster Url: " + FULL_POSTER_URL);

        Picasso.with(mContext).load(FULL_POSTER_URL).into(holder.image);

        return convertView;
    }

    private class ViewHolder {
        ImageView image;
    }

    public void clear() {
        mMovies.clear();
    }

    public void add(Movie movie) {
        mMovies.add(movie);
    }

    public void setData(ArrayList<Movie> data) {
        clear();
        for (Movie movie : data) {
            add(movie);
        }
    }
}
