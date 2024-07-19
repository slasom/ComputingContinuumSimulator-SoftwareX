package es.unex.infinitetime.persistence;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

@Database(entities = {User.class, Task.class, Project.class, SharedProject.class, Favorite.class}, version = 1, exportSchema = false)
public abstract class InfiniteDatabase extends RoomDatabase{
    private static volatile InfiniteDatabase INSTANCE;

    public static InfiniteDatabase getDatabase(final Context context) {
        synchronized (InfiniteDatabase.class) {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        InfiniteDatabase.class, "infinite_database")
                        .build();
            }
        }
        return INSTANCE;
    }

    public abstract UserDAO userDAO();
    public abstract TaskDAO taskDAO();
    public abstract ProjectDAO projectDAO();
}
