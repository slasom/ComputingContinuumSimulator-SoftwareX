package es.unex.infinitetime.RoomTests.CU13;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import es.unex.infinitetime.persistence.InfiniteDatabase;
import es.unex.infinitetime.persistence.Project;
import es.unex.infinitetime.persistence.ProjectDAO;
import es.unex.infinitetime.persistence.User;
import es.unex.infinitetime.persistence.UserDAO;

@RunWith(AndroidJUnit4.class)
public class DeleteProjectRoomTest {
    private InfiniteDatabase volatileDB;
    private UserDAO dao_to_test_user;
    private ProjectDAO dao_to_test_project;


    @Before
    public void createVolatileDB(){
        Context context = ApplicationProvider.getApplicationContext();
        volatileDB = Room.inMemoryDatabaseBuilder(context, InfiniteDatabase.class).allowMainThreadQueries().build();
        dao_to_test_user = volatileDB.userDAO();
        Assert.assertNotNull(dao_to_test_user);
        dao_to_test_project = volatileDB.projectDAO();
        Assert.assertNotNull(dao_to_test_project);
    }

    @Test
    public void deleteProject(){
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

        Project item2 = new Project();
        item2.setId(3);
        item2.setName("Proyecto 1");
        item2.setDescription("Description 1");
        item2.setUserId(1);
        dao_to_test_project.insert(item2);

        Project item3 = new Project();
        item3.setId(4);
        item3.setName("Proyecto 1");
        item3.setDescription("Description 1");
        item3.setUserId(1);
        dao_to_test_project.insert(item3);

        List<Project> recoveredItems = dao_to_test_project.getAllProject();
        Assert.assertEquals(3, recoveredItems.size());
        dao_to_test_project.delete(item1);
        List<Project> recoveredItems2 = dao_to_test_project.getAllProject();
        Assert.assertEquals(2, recoveredItems2.size());

    }

    @After
    public void closeVolatileDB(){
        volatileDB.close();
    }
}