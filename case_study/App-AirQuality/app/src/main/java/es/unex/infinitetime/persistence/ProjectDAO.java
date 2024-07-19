package es.unex.infinitetime.persistence;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ProjectDAO {

    @Query("SELECT * FROM project WHERE id = :projectId")
    Project getProject(long projectId);

    @Query("SELECT * FROM project ")
    List<Project> getAllProject();

    @Query("SELECT * FROM task WHERE project_id = :projectId")
    List<Task> getTasks(long projectId);

    @Query("INSERT INTO shared_project (project_id, user_id) VALUES (:projectId, :userId)")
    void shareProject(long projectId, long userId);

    @Query("DELETE FROM shared_project WHERE project_id = :projectId AND user_id = :userId")
    void stopSharingProject(long projectId, long userId);

    @Query("SELECT * FROM shared_project WHERE project_id = :projectId and user_id = :userId")
    SharedProject getSharedProject(long projectId, long userId);

    @Insert
    void insert(Project project);

    @Update
    int update(Project project);

    @Delete
    void delete(Project project);
}
