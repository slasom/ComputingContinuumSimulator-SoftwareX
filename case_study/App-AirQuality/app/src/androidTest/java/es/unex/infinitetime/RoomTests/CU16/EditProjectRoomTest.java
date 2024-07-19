package es.unex.infinitetime.RoomTests.CU16;

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
public class EditProjectRoomTest {
    private InfiniteDatabase volatileDB;
    private ProjectDAO dao_to_test_project;
    private UserDAO dao_to_test_user;

    @Before
    public void createVolatileDB(){
        Context context = ApplicationProvider.getApplicationContext();
        volatileDB = Room.inMemoryDatabaseBuilder(context, InfiniteDatabase.class).allowMainThreadQueries().build();
        dao_to_test_project = volatileDB.projectDAO();
        Assert.assertNotNull(dao_to_test_project);
        dao_to_test_user = volatileDB.userDAO();
        Assert.assertNotNull(dao_to_test_user);
    }

    @Test
    public void editProject(){
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

        List<Project> items = dao_to_test_project.getAllProject();
        Project recoveredItem = items.get(0);

        recoveredItem.setName("Proyecto 1 Actualizado");

        dao_to_test_project.update(recoveredItem);

        items = dao_to_test_project.getAllProject();
        Assert.assertEquals("Proyecto 1 Actualizado", items.get(0).getName());
    }

    @After
    public void closeVolatileDB(){
        volatileDB.close();
    }
}
