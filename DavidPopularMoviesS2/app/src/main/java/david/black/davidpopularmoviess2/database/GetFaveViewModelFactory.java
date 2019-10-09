package david.black.davidpopularmoviess2.database;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

public class GetFaveViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private final AppDatabase favorites;
    private final int mMovieID;
    public GetFaveViewModelFactory(AppDatabase database, int movieId) {
        favorites = database;
        mMovieID = movieId;
    }
    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new GetFaveViewModel(favorites, mMovieID);
    }
}
