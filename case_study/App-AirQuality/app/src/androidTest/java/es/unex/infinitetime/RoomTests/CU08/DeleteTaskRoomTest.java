package es.unex.infinitetime.RoomTests.CU08;

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
import java.util.List;

import es.unex.infinitetime.persistence.InfiniteDatabase;
import es.unex.infinitetime.persistence.Project;
import es.unex.infinitetime.persistence.ProjectDAO;
import es.unex.infinitetime.persistence.Task;
import es.unex.infinitetime.persistence.TaskDAO;
import es.unex.infinitetime.persistence.TaskState;
import es.unex.infinitetime.persistence.User;
import es.unex.infinitetime.persistence.UserDAO;

@RunWith(AndroidJUnit4.class)
public class DeleteTaskRoomTest {

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
    public void TaskDBDelete() throws InterruptedException {

        User user = new User();
        user.setId(1);
        user.setUsername("username");
        user.setPassword("password");
        user.setEmail("email");
        dao_to_test_user.insert(user);

        Project project = new Project();
        project.setId(10);
        project.setName("Proyecto 1");
        project.setDescription("Description 1");
        project.setUserId(1);
        dao_to_test_project.insert(project);

        Task task1 = new Task();
        task1.setId(2);
        task1.setName("Task 1");
        task1.setDescription("Descripcion 1");
        task1.setDeadline(new Date(2000,10,1));
        task1.setState(TaskState.DOING);
        task1.setPriority(2);
        task1.setEffort(6);
        task1.setUserId(1);
        task1.setProjectId(10);

        dao_to_test_task.insert(task1);

        List<Task> recoveredItems1 = dao_to_test_task.getAllTasks();
        Assert.assertEquals(1, recoveredItems1.size());

        dao_to_test_task.delete(task1);

        Assert.assertNull(dao_to_test_task.getTask(2));
        List<Task> recoveredItems2 = dao_to_test_task.getAllTasks();
        Assert.assertEquals(0,  recoveredItems2.size());
    }

    @After
    public void closeVolatileDB(){
        volatileDB.close();
    }

}
