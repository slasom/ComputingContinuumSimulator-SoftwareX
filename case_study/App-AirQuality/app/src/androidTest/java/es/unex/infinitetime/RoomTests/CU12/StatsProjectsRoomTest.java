package es.unex.infinitetime.RoomTests.CU12;

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

import es.unex.infinitetime.persistence.InfiniteDatabase;
import es.unex.infinitetime.persistence.Project;
import es.unex.infinitetime.persistence.ProjectDAO;
import es.unex.infinitetime.persistence.Task;
import es.unex.infinitetime.persistence.TaskDAO;
import es.unex.infinitetime.persistence.TaskState;
import es.unex.infinitetime.persistence.User;
import es.unex.infinitetime.persistence.UserDAO;

@RunWith(AndroidJUnit4.class)
public class StatsProjectsRoomTest {
    private InfiniteDatabase volatileDB;
    private TaskDAO dao_to_test_task;
    private UserDAO dao_to_test_user;
    private ProjectDAO dao_to_test_project;


    @Before
    public void createVolatileDB(){
        Context context = ApplicationProvider.getApplicationContext();
        volatileDB = Room.inMemoryDatabaseBuilder(context, InfiniteDatabase.class).allowMainThreadQueries().build();
        dao_to_test_task = volatileDB.taskDAO();
        Assert.assertNotNull(dao_to_test_task);
        dao_to_test_user = volatileDB.userDAO();
        Assert.assertNotNull(dao_to_test_user);
        dao_to_test_project = volatileDB.projectDAO();
        Assert.assertNotNull(dao_to_test_project);
    }

    @Test
    public void getTasksNumStats(){

        User item = new User();
        item.setId(1);
        item.setUsername("Usuario 1");
        item.setEmail("emailUser1@unex.es");
        item.setPassword("12345");
        dao_to_test_user.insert(item);

        Project item1 = new Project();
        item1.setId(2);
        item1.setName("Proyecto 1");
        item1.setDescription("Description 1");
        item1.setUserId(1);
        dao_to_test_project.insert(item1);

        Task item2 = new Task();
        item2.setId(3);
        item2.setName("Task 1");
        item2.setDescription("Descripcion 1");
        item2.setDeadline(new Date(1/1/2000));
        item2.setState(TaskState.DOING);
        item2.setPriority(2);
        item2.setEffort(6);
        item2.setUserId(1);
        item2.setProjectId(2);

        Task item3 = new Task();
        item3.setId(4);
        item3.setName("Task 2");
        item3.setDescription("Descripcion 2");
        item3.setDeadline(new Date(2000,1,1));
        item3.setState(TaskState.DONE);
        item3.setPriority(3);
        item3.setEffort(6);
        item3.setUserId(1);
        item3.setProjectId(2);

        Task item4 = new Task();
        item4.setId(5);
        item4.setName("Task 3");
        item4.setDescription("Descripcion 3");
        item4.setDeadline(new Date(2000,1,1));
        item4.setState(TaskState.DONE);
        item4.setPriority(5);
        item4.setEffort(9);
        item4.setUserId(1);
        item4.setProjectId(2);

        dao_to_test_task.insert(item2);
        dao_to_test_task.insert(item3);
        dao_to_test_task.insert(item4);


        int numDOING = dao_to_test_task.getTasksNum(1,1);
        Assert.assertEquals(1, numDOING);

        int numDONE = dao_to_test_task.getTasksNum(1,2);
        Assert.assertEquals(2, numDONE);
    }


    @After
    public void closeVolatileDB(){
        volatileDB.close();
    }
}
