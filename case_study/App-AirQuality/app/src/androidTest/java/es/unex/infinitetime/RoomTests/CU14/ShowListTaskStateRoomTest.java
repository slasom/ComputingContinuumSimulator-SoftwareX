package es.unex.infinitetime.RoomTests.CU14;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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

public class ShowListTaskStateRoomTest {
    private InfiniteDatabase volatileDB;
    private UserDAO dao_to_test_user;
    private ProjectDAO dao_to_test_project;
    private TaskDAO dao_to_test_task;


    @Before
    public void createVolatileDB(){
        Context context = ApplicationProvider.getApplicationContext();
        volatileDB = Room.inMemoryDatabaseBuilder(context, InfiniteDatabase.class).allowMainThreadQueries().build();
        dao_to_test_user = volatileDB.userDAO();
        Assert.assertNotNull(dao_to_test_user);
        dao_to_test_project = volatileDB.projectDAO();
        Assert.assertNotNull(dao_to_test_project);
        dao_to_test_task = volatileDB.taskDAO();
        Assert.assertNotNull(dao_to_test_task);
    }

    @Test
    public void showTasks(){
        User item = new User();
        item.setId(1);
        item.setUsername("Usuario 1");
        item.setEmail("emailUser1@unex.es");
        item.setPassword("12345");
        dao_to_test_user.insert(item);

        Project project = new Project();
        project.setId(2);
        project.setName("Proyecto 1");
        project.setDescription("Description 1");
        project.setUserId(1);
        dao_to_test_project.insert(project);

        Task item1 = new Task();
        item1.setId(3);
        item1.setName("Task 1");
        item1.setDescription("Descripcion 1");
        item1.setDeadline(new Date(2000,1,1));
        item1.setState(TaskState.DOING);
        item1.setPriority(2);
        item1.setEffort(6);
        item1.setUserId(1);
        item1.setProjectId(2);

        Task item2 = new Task();
        item2.setId(4);
        item2.setName("Task 2");
        item2.setDescription("Descripcion 2");
        item2.setDeadline(new Date(2000,1,1));
        item2.setState(TaskState.DONE);
        item2.setPriority(3);
        item2.setEffort(6);
        item2.setUserId(1);
        item2.setProjectId(2);

        Task item3 = new Task();
        item3.setId(5);
        item3.setName("Task 3");
        item3.setDescription("Descripcion 3");
        item3.setDeadline(new Date(2000,1,1));
        item3.setState(TaskState.DONE);
        item3.setPriority(5);
        item3.setEffort(9);
        item3.setUserId(1);
        item3.setProjectId(2);

        dao_to_test_task.insert(item1);
        dao_to_test_task.insert(item2);
        dao_to_test_task.insert(item3);

        List <Task> recoveredItems = dao_to_test_task.getAllTasks();
        Assert.assertEquals(3, recoveredItems.size());
    }

    @After
    public void closeDB(){
        volatileDB.close();
    }


}
