package es.unex.infinitetime.RoomTests.CU01;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import es.unex.infinitetime.persistence.InfiniteDatabase;
import es.unex.infinitetime.persistence.Project;
import es.unex.infinitetime.persistence.ProjectDAO;
import es.unex.infinitetime.persistence.User;
import es.unex.infinitetime.persistence.UserDAO;

@RunWith(AndroidJUnit4.class)
public class AddProjectRoomTest {

    private InfiniteDatabase volatileDB;
    private UserDAO dao_to_test_user;
    private ProjectDAO dao_to_test_project;


    @Before
    public void createVolatileDB(){
        Context context = ApplicationProvider.getApplicationContext();
        volatileDB = Room.inMemoryDatabaseBuilder(context, InfiniteDatabase.class).allowMainThreadQueries().build();
        dao_to_test_user = volatileDB.userDAO();
        dao_to_test_project = volatileDB.projectDAO();
    }


    @Test
    public void addProject(){
        User user = new User();
        user.setId(1);
        user.setUsername("username");
        user.setPassword("password");
        user.setEmail("email");
        dao_to_test_user.insert(user);
        Project project = new Project();
        project.setId(1);
        project.setName("name");
        project.setDescription("description");
        project.setUserId(1);
        dao_to_test_project.insert(project);
        Project getProject = dao_to_test_project.getProject(1);
        Assert.assertNotNull(getProject);
        Assert.assertEquals(getProject.getId(), project.getId());
        Assert.assertEquals(getProject.getName(), project.getName());
        Assert.assertEquals(getProject.getDescription(), project.getDescription());
        Assert.assertEquals(getProject.getUserId(), project.getUserId());
        Assert.assertEquals(dao_to_test_project.getAllProject().size(), 1);
    }

    @After
    public void closeDB(){
        volatileDB.close();
    }

}
