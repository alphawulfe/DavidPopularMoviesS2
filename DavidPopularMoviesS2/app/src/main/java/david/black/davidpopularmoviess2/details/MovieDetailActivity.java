package david.black.davidpopularmoviess2.details;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import david.black.davidpopularmoviess2.utils.AppExecutors;
import david.black.davidpopularmoviess2.R;
import david.black.davidpopularmoviess2.database.AppDatabase;
import david.black.davidpopularmoviess2.main.MainActivity;
import david.black.davidpopularmoviess2.model.Movie;
import david.black.davidpopularmoviess2.reviews.ReviewActivity;
import david.black.davidpopularmoviess2.database.GetFaveViewModel;
import david.black.davidpopularmoviess2.database.GetFaveViewModelFactory;
import david.black.davidpopularmoviess2.utils.JsonUtils;
import david.black.davidpopularmoviess2.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import java.io.IOException;
import java.net.URL;

public class MovieDetailActivity extends AppCompatActivity {
    public static final String EXTRA_MOVIE = "extra_movie";
    private TextView mTitleView, mSynopsisView, mRatingView, mReleaseDateView;
    private String trailerYoutubeKey = null;
    private ImageView mPosterImageView;
    private Movie mMovie;
    private ImageButton favoriteButton;
    private AppDatabase favorites;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        Intent intent = getIntent();
        mMovie = (Movie) intent.getParcelableExtra(EXTRA_MOVIE);
        favorites = AppDatabase.getInstance(getApplicationContext());
        setTitle(mMovie.getTitle());
        mPosterImageView = findViewById(R.id.detail_poster_imgV);
        Picasso.with(this)
                .load(mMovie.getPosterUrl())
                .into(mPosterImageView);
        populateUI(mMovie);
        final ImageButton trailerButton = findViewById(R.id.play_trailer_button);
        trailerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                playTrailer();
            }
        });
        final Button reviewsButton = findViewById(R.id.reviews_button);
        reviewsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                launchReviewsActivity();
            }
        });
        favoriteButton = findViewById(R.id.favorite_button);
        favoriteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                toggleFavorite();
            }
        });
        setupViewModel();
    }

    private void playTrailer() {
        if (NetworkUtils.deviceIsConnected(getApplicationContext())) {
            URL tmdbUrl = NetworkUtils.buildTrailerQueryUrl(this, mMovie.getId(),
                    MainActivity.getTMBDApiKey());
            Log.d("Query URL",tmdbUrl.toString());
            MovieDbTrailerQueryTask fetchTrailersTask = new MovieDbTrailerQueryTask();
            fetchTrailersTask.execute(tmdbUrl);
        } else {
            Toast.makeText(getApplicationContext(), "No network connection", Toast.LENGTH_LONG).show();
        }
    }



    private void populateUI(Movie movie) {
        mTitleView = findViewById(R.id.title_TextV);
        mTitleView.setText(movie.getTitle());
        mSynopsisView = findViewById(R.id.synopsis_tv);
        mSynopsisView.setText(movie.getSynopsis());
        mReleaseDateView = findViewById(R.id.release_date_tv);
        mReleaseDateView.setText(movie.getReleaseDate());
        mRatingView = findViewById(R.id.rating_tv);
        mRatingView.setText(Float.toString(movie.getRating()));
    }

    public class MovieDbTrailerQueryTask extends AsyncTask<URL, Void, String> {
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
            Toast noTrailerToast = Toast.makeText(getApplicationContext(),
                    "Can't play trailer", Toast.LENGTH_LONG);
            if (queryResult != null) {
                Log.d("Trailer query result",queryResult);
                try {
                    trailerYoutubeKey = JsonUtils.getTrailerYoutubeKey(queryResult);
                } catch (JSONException e) {
                    Log.e("Fix JSON, onPostExecute", e.toString());
                }
            } else {
                noTrailerToast.show();
                return;
            }
            if (trailerYoutubeKey != null) {
                Uri youtubeUri = NetworkUtils.buildYoutubeUri(trailerYoutubeKey);
                Log.d("YoutubeURI",youtubeUri.toString());
                startActivity(new Intent(Intent.ACTION_VIEW, youtubeUri));
            } else {
                noTrailerToast.show();
            }
        }
    }

    private void launchReviewsActivity() {
        int movieId = mMovie.getId();
        String movieTitle = mMovie.getTitle();
        Intent reviewsActivityIntent = new Intent(this, ReviewActivity.class);
        reviewsActivityIntent.putExtra(ReviewActivity.EXTRA_MOVIE_ID, movieId);
        reviewsActivityIntent.putExtra(ReviewActivity.EXTRA_MOVIE_TITLE, movieTitle);
        startActivity(reviewsActivityIntent);
    }


    private void toggleFavorite() {
        if (mMovie.isFavorite()) {
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    favorites.favoriteMovie().deleteFavoriteMovie(mMovie);
                }
            });
            mMovie.setFavorite(false);
            setFaveButtonImage(false);
        } else {
            mMovie.setFavorite(true);
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    favorites.favoriteMovie().insertFavoriteMovie(mMovie);
                }
            });
            setFaveButtonImage(true);
        }
    }

    private void setupViewModel() {
        GetFaveViewModelFactory factory = new GetFaveViewModelFactory(favorites,mMovie.getId());
        final GetFaveViewModel viewModel = ViewModelProviders.of(this, factory).get(GetFaveViewModel.class);
        viewModel.getMovie().observe(this, new Observer<Movie>() {
            @Override
            public void onChanged(@Nullable Movie movie) {
                if (movie != null) {
                    mMovie.setFavorite(movie.isFavorite());
                    setFaveButtonImage(movie.isFavorite());
                }
            }
        });
    }

    private void setFaveButtonImage(boolean isFavorite) {
        if (isFavorite) {
            favoriteButton.setImageDrawable(
                    ContextCompat.getDrawable(this,android.R.drawable.btn_star_big_on));
        } else {
            favoriteButton.setImageDrawable(
                    ContextCompat.getDrawable(this,android.R.drawable.btn_star_big_off));
        }
    }
}
