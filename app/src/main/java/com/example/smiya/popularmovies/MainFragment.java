package com.example.smiya.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment {

    private static String MOVIE_SAVED_STATE = "movie state";
    public MoviePosterAdapter mMovieAdapter;
    public ArrayList<Movie> mMoviesArrayList;

    public MainFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList(MOVIE_SAVED_STATE, mMoviesArrayList);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        FetchMoviesTask moviesTask = new FetchMoviesTask();
        moviesTask.execute("popularity.desc");

        if (savedInstanceState != null) {
            mMoviesArrayList = savedInstanceState.getParcelableArrayList(MOVIE_SAVED_STATE);
        } else {
            mMoviesArrayList = new ArrayList<Movie>();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_movie_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_sort_by_popularity) {
            FetchMoviesTask moviesTask = new FetchMoviesTask();
            moviesTask.execute("popularity.desc");
            return true;
        }
        if (id == R.id.action_sort_by_ratings) {
            FetchMoviesTask moviesTask = new FetchMoviesTask();
            moviesTask.execute("vote_average.desc&vote_count.gte=1000"); // sort the popular movies, not this. this isn't very good results
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        GridView moviePosterGrid = (GridView) rootView.findViewById(R.id.movie_poster_gridview);

        mMovieAdapter = new MoviePosterAdapter(getActivity(), mMoviesArrayList);
        moviePosterGrid.setAdapter(mMovieAdapter);

        moviePosterGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movieItem = mMovieAdapter.getItem(position);
//                String movieTitle = mMovieAdapter.getItem(position).getTitle();
//                Toast.makeText(getActivity(), movieTitle, Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(getActivity(), DetailActivity.class).putExtra(Intent.EXTRA_TEXT, movieItem);

                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra("com.package.Movie", movieItem);
                startActivity(intent);
            }
        });

        return rootView;
    }

//    public class FetchTopRatedMoviesTask extends AsyncTask
    
    public class FetchMoviesTask extends AsyncTask<String,Void,ArrayList<Movie>> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        private ArrayList<Movie> getMoviesDataFromJson(String jsonStr) throws JSONException {
            JSONObject movieJson = new JSONObject(jsonStr);
            JSONArray movieArray = movieJson.getJSONArray("results");

            ArrayList<Movie> results = new ArrayList<>();

            for(int i = 0; i < movieArray.length(); i++) {
                JSONObject movie = movieArray.getJSONObject(i);
                Movie movieModel = new Movie(movie);
                results.add(movieModel);
            }

//            for (Movie s : results) {
//                Log.v(LOG_TAG, "Movie Entry: " + s);
//            }

            return results;
        }

        @Override
        protected ArrayList<Movie> doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String movieJsonStr = null;

            try {
                final String BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
                final String SORT_PARAM = "sort_by";
                final String API_KEY_PARAM = "api_key";

                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_PARAM, params[0])
                        .appendQueryParameter(API_KEY_PARAM, BuildConfig.MOVIE_DB_API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());

//                Log.v(LOG_TAG, "Built Uri: " + builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                movieJsonStr = buffer.toString();

                Log.v(LOG_TAG, "Movies JSON String: " + movieJsonStr);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            // if we got movieJsonStr successfully, try parsing out data
            try {
                ArrayList<Movie> movieList = getMoviesDataFromJson(movieJsonStr);
                return movieList; // returns an ArrayList of Movies
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                return null;
            }

//            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            if (movies != null) {
                if (mMovieAdapter != null) {
//                    Log.v(LOG_TAG, "movies and mMovieAdapter is not null, so set it");
                    mMovieAdapter.setData(movies);
                    mMovieAdapter.notifyDataSetChanged();
                }
//                mMoviesArrayList = new ArrayList<>();
                mMoviesArrayList.addAll(movies);
            } else {
                Log.v(LOG_TAG, "Data set NOT changed");
            }
        }
    }
}
