package es.unex.infinitetime.RoomTests.CU10;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

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


public class EditTaskRoomTest {

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
    public void TaskDBUpdateTest(){

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

        Task task = new Task();
        task.setId(2);
        task.setName("Task 1");
        task.setDescription("Descripcion 1");
        task.setDeadline(new Date(2000,10,1));
        task.setState(TaskState.DOING);
        task.setPriority(2);
        task.setEffort(6);
        task.setUserId(1);
        task.setProjectId(10);

        dao_to_test_task.insert(task);

        Task recoveredItem = dao_to_test_task.getTask(2);

        recoveredItem.setName("Task 1 Actualizado");

        dao_to_test_task.update(recoveredItem);

        recoveredItem = dao_to_test_task.getTask(2);
        Assert.assertEquals("Task 1 Actualizado", recoveredItem.getName());

    }

}
