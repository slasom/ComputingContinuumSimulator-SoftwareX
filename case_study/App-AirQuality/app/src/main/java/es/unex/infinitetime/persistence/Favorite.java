package es.unex.infinitetime.persistence;

import static androidx.room.ForeignKey.CASCADE;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;

import es.unex.infinitetime.api.FavoriteRemote;

@Entity(tableName = "favorite", primaryKeys = {"user_id", "task_id"},
    foreignKeys = {
                @ForeignKey(entity = User.class,
                        parentColumns = "id",
                        childColumns = "user_id",
                        onDelete = CASCADE),
                @ForeignKey(entity = Task.class,
                        parentColumns = "id",
                        childColumns = "task_id",
                        onDelete = CASCADE)
    }, indices = {@Index(value = {"user_id", "task_id"}),@Index(value = {"task_id"})})
public class Favorite {

    @ColumnInfo(name = "user_id")
    private long userId;
    @ColumnInfo(name = "task_id")
    private long taskId;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    @Ignore
    public static Favorite fromRemote(FavoriteRemote favoriteRemote) {
        Favorite favorite = new Favorite();
        favorite.setUserId(Long.parseLong(favoriteRemote.getUserId()));
        favorite.setTaskId(Long.parseLong(favoriteRemote.getTaskId()));
        return favorite;
    }
}
