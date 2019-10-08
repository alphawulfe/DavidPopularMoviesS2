package david.black.davidpopularmoviess2.main;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import david.black.davidpopularmoviess2.details.MovieDetailActivity;
import david.black.davidpopularmoviess2.model.Movie;
import david.black.davidpopularmoviess2.R;
import david.black.davidpopularmoviess2.database.FaveViewModel;
import david.black.davidpopularmoviess2.utils.JsonUtils;
import david.black.davidpopularmoviess2.utils.NetworkUtils;

import org.json.JSONException;
import java.io.IOException;
import java.net.URL;
import java.util.List;


public class MainActivity
        extends AppCompatActivity
        implements GridAdapter.OnClickHandler, SharedPreferences.OnSharedPreferenceChangeListener{

    private RecyclerView.LayoutManager posterGridLayoutManager;
    private String sortOrder;
    private String mMovieQueryResult = null;
    private static String TMBDApiKey;
    private List<Movie> favoriteMovies;
    private GridAdapter gridAdapter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.open_settings) {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupPreferences();
        setupViewModel();
        RecyclerView posterGridRecyclerView = (RecyclerView) findViewById(R.id.poster_grid);
        posterGridLayoutManager = new GridLayoutManager(this, 3);
        posterGridRecyclerView.setLayoutManager(posterGridLayoutManager);
        gridAdapter = new GridAdapter(this, this);
        posterGridRecyclerView.setAdapter(gridAdapter);
        TMBDApiKey = getString(R.string.key_themoviedb);
        fetchMovies();

    }

    private void fetchMovies() {
        if (sortOrder.equals(getString(R.string.order_pref_favorite_value))) {
            if (favoriteMovies != null) {
                gridAdapter.setPosterUrls(favoriteMovies);
            }
        } else {
            if (NetworkUtils.deviceIsConnected(this)) {
                URL tmdbUrl = NetworkUtils.buildMovieQueryUrl(this, sortOrder, TMBDApiKey);
                MovieDbMovieQueryTask fetchMoviesTask = new MovieDbMovieQueryTask();
                fetchMoviesTask.execute(tmdbUrl);
            } else {
                Toast.makeText(this, "Zero network connection", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onItemClicked(int position) {
        launchDetailActivity(position);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.order_pref_key))) {
            setSortOrder(sharedPreferences.getString(
                    getString(R.string.order_pref_key),
                    getString(R.string.order_pref_default)));
            fetchMovies();
        }
    }

    public class MovieDbMovieQueryTask extends AsyncTask<URL, Void, String> {
        @Override
        protected String doInBackground(URL... urls) {
            String result = null;
            try {
                result = NetworkUtils.getResponseFromHttpUrl(urls[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String queryResult) {
            super.onPostExecute(queryResult);
            mMovieQueryResult = queryResult;
            gridAdapter.setPosterUrls(queryResult);
        }
    }

    private void launchDetailActivity(int position) {
        Movie movie;
        if (sortOrder.equals(getString(R.string.order_pref_favorite_value))) {
            movie = favoriteMovies.get(position);
        } else {
            try {
                String movieJson = JsonUtils.getMovieJson(mMovieQueryResult, position);
                movie = JsonUtils.parseMovieJson(movieJson);

            } catch (JSONException e) {
                Log.e("FixLaunchDetailActivity", e.toString());
                Toast.makeText(this, "No details available.", Toast.LENGTH_LONG)
                        .show();
                return;
            }
        }
        Intent detailActivityIntent = new Intent(this, MovieDetailActivity.class);
        detailActivityIntent.putExtra(MovieDetailActivity.EXTRA_MOVIE, movie);
        startActivity(detailActivityIntent);
    }

    private void setupPreferences(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        setSortOrder(sharedPreferences.getString(
                getString(R.string.order_pref_key),
                getString(R.string.order_pref_default)));
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onDestroy() {
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }

    public String getSortOrder() { return sortOrder; }
    public void setSortOrder(String sortOrder) { this.sortOrder = sortOrder; }
    public static String getTMBDApiKey() { return TMBDApiKey; }

    private void setupViewModel() {
        FaveViewModel viewModel = ViewModelProviders.of(this).get(FaveViewModel.class);
        viewModel.getFavorites().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> movies) {
                favoriteMovies = movies;
                if (sortOrder.equals(getString(R.string.order_pref_favorite_value))) {
                    gridAdapter.setPosterUrls(movies);
                }
            }
        });
    }
}
