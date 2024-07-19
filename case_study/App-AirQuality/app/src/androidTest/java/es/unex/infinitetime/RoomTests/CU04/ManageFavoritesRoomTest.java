package es.unex.infinitetime.RoomTests.CU04;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import es.unex.infinitetime.persistence.Favorite;
import es.unex.infinitetime.persistence.InfiniteDatabase;
import es.unex.infinitetime.persistence.Project;
import es.unex.infinitetime.persistence.ProjectDAO;
import es.unex.infinitetime.persistence.Task;
import es.unex.infinitetime.persistence.TaskDAO;
import es.unex.infinitetime.persistence.TaskState;
import es.unex.infinitetime.persistence.User;
import es.unex.infinitetime.persistence.UserDAO;

@RunWith(AndroidJUnit4.class)
public class ManageFavoritesRoomTest {


    private InfiniteDatabase volatileDB;
    private UserDAO dao_to_test_user;
    private ProjectDAO dao_to_test_project;
    private TaskDAO dao_to_test_task;

    @Before
    public void createVolatileDB(){
        Context context = ApplicationProvider.getApplicationContext();
        volatileDB = Room.inMemoryDatabaseBuilder(context, InfiniteDatabase.class).allowMainThreadQueries().build();
        dao_to_test_user = volatileDB.userDAO();
        dao_to_test_project = volatileDB.projectDAO();
        dao_to_test_task = volatileDB.taskDAO();
    }

    @Test
    public void addTask(){
        User user = new User();
        user.setId(10);
        user.setUsername("username");
        user.setPassword("password");
        user.setEmail("email");
        dao_to_test_user.insert(user);
        Project project = new Project();
        project.setId(1);
        project.setName("name");
        project.setDescription("description");
        project.setUserId(10);
        dao_to_test_project.insert(project);
        Task task = new Task();
        task.setId(8);
        task.setName("name");
        task.setDescription("description");
        task.setProjectId(1);
        task.setPriority(1);
        task.setEffort(13);
        task.setDeadline(new Date());
        task.setUserId(10);
        task.setProjectId(1);
        task.setState(TaskState.DONE);
        dao_to_test_task.insert(task);
        dao_to_test_task.addFavorite(user.getId(), task.getId());
        Favorite favorite = dao_to_test_user.getFavorite(user.getId(), task.getId());
        Assert.assertNotNull(favorite);
        Assert.assertEquals(favorite.getTaskId(), task.getId());
        Assert.assertEquals(favorite.getUserId(), user.getId());
        dao_to_test_task.removeFavorite(user.getId(), task.getId());
        favorite = dao_to_test_user.getFavorite(user.getId(), task.getId());
        Assert.assertNull(favorite);
    }

    @After
    public void closeDB(){
        volatileDB.close();
    }
}
