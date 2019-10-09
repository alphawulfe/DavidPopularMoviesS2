package david.black.davidpopularmoviess2.database;

import android.support.annotation.NonNull;
import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.AndroidViewModel;
import david.black.davidpopularmoviess2.model.Movie;
import java.util.List;


public class FaveViewModel extends AndroidViewModel {
    private LiveData<List<Movie>> favorites;
    public FaveViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        favorites = database.favoriteMovie().loadFavoriteMovies();
    }
    public LiveData<List<Movie>> getFavorites() {
        return favorites;
    }

}
