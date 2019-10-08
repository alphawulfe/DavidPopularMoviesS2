package david.black.davidpopularmoviess2.database;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import david.black.davidpopularmoviess2.model.Movie;

public class GetFaveViewModel extends ViewModel {
    private LiveData<Movie> movie;

    public GetFaveViewModel(AppDatabase database, int movieId) {
        movie = database.favoriteMovie().loadMovieById(movieId);
    }

    public LiveData<Movie> getMovie() {
        return movie;
    }
}
