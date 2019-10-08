package david.black.davidpopularmoviess2.database;
import android.arch.persistence.room.Database;
import android.content.Context;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import david.black.davidpopularmoviess2.model.Movie;

@Database(entities = {Movie.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "favorites";
    private static final Object LOCK = new Object();
    private static AppDatabase ourInstance;
    public static AppDatabase getInstance(Context context) {
        if (ourInstance == null) {
            synchronized (LOCK) {
                ourInstance = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, AppDatabase.DATABASE_NAME)
                        .build();
            }
        }
        return ourInstance;
    }

    public abstract FavoriteMovie favoriteMovie();
}
