package es.unex.infinitetime.persistence;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TaskDAO {


    @Query("SELECT * FROM task ")
    List<Task> getAllTasks();

    @Query("SELECT COUNT (*) FROM task WHERE user_id=:userId AND state=:state")
    int getTasksNum(long userId, int state);


    @Query("SELECT * FROM task WHERE id IN (SELECT task_id FROM favorite WHERE user_id = :userId)")
    List <Task> getAllTaskFavorite(long userId);

    @Query("SELECT * FROM task WHERE id = :taskId")
    Task getTask(long taskId);

    @Query("INSERT INTO favorite (user_id, task_id) VALUES (:userId, :taskId)")
    void addFavorite(long userId, long taskId);

    @Query("DELETE FROM favorite WHERE user_id = :userId AND task_id = :taskId")
    void removeFavorite(long userId, long taskId);

    @Insert
    void insert(Task task);

    @Update
    int update(Task task);

    @Delete
    void delete(Task task);
}
