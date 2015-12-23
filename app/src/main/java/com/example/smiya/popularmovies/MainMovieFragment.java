package com.example.smiya.popularmovies;

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
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainMovieFragment extends Fragment {

    private MoviePosterAdapter mMovieAdapter;
    private ArrayList<Movie> mMovies = null;

    public MainMovieFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

//        String[] popularMovieArray = { //poster paths
//                "http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg",
//                "http://image.tmdb.org/t/p/w185//D6e8RJf2qUstnfkTslTXNTUAlT.jpg",
//                "http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg",
//                "http://image.tmdb.org/t/p/w185//D6e8RJf2qUstnfkTslTXNTUAlT.jpg",
//                "http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg",
//                "http://image.tmdb.org/t/p/w185//D6e8RJf2qUstnfkTslTXNTUAlT.jpg",
//        };
//
//        List<String> moviePosterList = new ArrayList<String>(
//                Arrays.asList(popularMovieArray)
//        );

        mMovieAdapter = new MoviePosterAdapter(getActivity(), new ArrayList<Movie>());

        GridView moviePosterGrid = (GridView) rootView.findViewById(R.id.movie_poster_gridview);
        moviePosterGrid.setAdapter(mMovieAdapter);

        return rootView;
    }

    public class FetchMoviesTask extends AsyncTask<String,Void,List<Movie>> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        private List<Movie> getMoviesDataFromJson(String jsonStr) throws JSONException {
            JSONObject movieJson = new JSONObject(jsonStr);
            JSONArray movieArray = movieJson.getJSONArray("results");

            List<Movie> results = new ArrayList<>();

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
        protected List<Movie> doInBackground(String... params) {

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

                Log.v(LOG_TAG, "Built Uri: " + builtUri.toString());

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
                ArrayList<Movie> movieList = (ArrayList<Movie>) getMoviesDataFromJson(movieJsonStr);
                return movieList; // returns an ArrayList of Movies
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                return null;
            }

//            return null;
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            if (movies != null) {
                if (mMovieAdapter != null) {
                    mMovieAdapter.setData(movies); // I get a Runtime OutOfMemoryError here. why?
                }
                mMovies = new ArrayList<>();
                mMovies.addAll(movies);
            }
        }
    }
}
