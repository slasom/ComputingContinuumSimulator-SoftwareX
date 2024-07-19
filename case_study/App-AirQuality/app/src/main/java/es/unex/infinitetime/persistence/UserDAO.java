package es.unex.infinitetime.persistence;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface UserDAO {

    @Query("SELECT * FROM user WHERE username = :username")
    User getUser(String username);

    @Query("SELECT * FROM user WHERE id = :userId")
    User getUser(long userId);

    @Query("SELECT * FROM user")
    List<User> getAllUsers();

    @Query("SELECT * FROM project WHERE user_id = :userId")
    List<Project> getProjectsCreated(long userId);

    @Query("SELECT * FROM task WHERE user_id = :userId")
    List<Task> getTasksCreated(long userId);

    @Query("SELECT * FROM shared_project WHERE user_id = :userId")
    List<SharedProject> getShared(long userId);

    @Query("SELECT * FROM task WHERE id IN (SELECT task_id FROM favorite WHERE user_id = :userId)")
    List<Task> getFavoriteTasks(long userId);

    @Query("SELECT * FROM favorite WHERE user_id = :userId")
    List<Favorite> getFavorites(long userId);

    @Query("SELECT * FROM favorite WHERE user_id=:userId AND task_id=:taskId")
    Favorite getFavorite(long userId, long taskId);

    @Query("SELECT * FROM project WHERE id IN (SELECT project_id FROM shared_project WHERE user_id = :userId)")
    List<Project> getProjectsShared(long userId);

    @Query("DELETE FROM project WHERE user_id = :userId")
    void deleteProjectsCreated(long userId);

    @Query("DELETE FROM shared_project WHERE user_id = :userId")
    void deleteProjectsShared(long userId);

    @Query("DELETE FROM favorite WHERE user_id = :userId")
    void deleteFavorites(long userId);

    @Query("DELETE FROM shared_project WHERE user_id = :userId")
    void deleteSharedProjects(long userId);

    @Query("DELETE FROM task WHERE user_id = :userId")
    void deleteTasksCreated(long userId);

    @Insert
    void insert(User user);

    @Insert
    void insertFavorite(Favorite favorite);

    @Insert
    void insertSharedProject(SharedProject sharedProject);

    @Update
    int update(User user);

    @Delete
    void delete(User user);


}
