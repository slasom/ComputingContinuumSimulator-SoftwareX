package es.unex.infinitetime.RoomTests.CU06;

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
public class ShowProjectsRoomTest {

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
    public void ProjectDBGetAllTest(){

        User user = new User();
        user.setId(1);
        user.setUsername("username");
        user.setPassword("password");
        user.setEmail("email");
        dao_to_test_user.insert(user);

        Project item1 = new Project();
        item1.setId(10);
        item1.setName("Proyecto 1");
        item1.setDescription("Description 1");
        item1.setUserId(1);

        Project item2 = new Project();
        item2.setId(11);
        item2.setName("Proyecto 2");
        item2.setDescription("Description 2");
        item2.setUserId(1);

        dao_to_test_project.insert(item1);
        dao_to_test_project.insert(item2);

        List<Project> recoveredItems = dao_to_test_project.getAllProject();
        Assert.assertEquals(2, recoveredItems.size());

        Assert.assertEquals(item1.getId(), recoveredItems.get(0).getId());
        Assert.assertEquals(item1.getName(), recoveredItems.get(0).getName());
        Assert.assertEquals(item1.getUserId(), recoveredItems.get(0).getUserId());
        Assert.assertEquals(item1.getDescription(), recoveredItems.get(0).getDescription());

        Assert.assertEquals(item2.getId(), recoveredItems.get(1).getId());
        Assert.assertEquals(item2.getName(), recoveredItems.get(1).getName());
        Assert.assertEquals(item2.getUserId(), recoveredItems.get(1).getUserId());
        Assert.assertEquals(item2.getDescription(), recoveredItems.get(1).getDescription());

    }

    @After
    public void closeVolatileDB(){
        volatileDB.close();
    }

}
