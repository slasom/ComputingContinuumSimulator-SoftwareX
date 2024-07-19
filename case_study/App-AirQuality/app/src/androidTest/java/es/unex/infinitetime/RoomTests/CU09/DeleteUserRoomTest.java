package es.unex.infinitetime.RoomTests.CU09;

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
import es.unex.infinitetime.persistence.User;
import es.unex.infinitetime.persistence.UserDAO;

@RunWith(AndroidJUnit4.class)
public class DeleteUserRoomTest {

    private InfiniteDatabase volatileDB;
    private UserDAO dao_to_test_user;


    @Before
    public void createVolatileDB(){
        Context context = ApplicationProvider.getApplicationContext();
        volatileDB = Room.inMemoryDatabaseBuilder(context, InfiniteDatabase.class).allowMainThreadQueries().build();
        dao_to_test_user = volatileDB.userDAO();

    }

    @Test
    public void TaskDBDelete() throws InterruptedException {

        User user = new User();
        user.setId(1);
        user.setUsername("username");
        user.setPassword("password");
        user.setEmail("email");
        dao_to_test_user.insert(user);

        List <User> recoveredItems = dao_to_test_user.getAllUsers();
        Assert.assertEquals(1, recoveredItems.size());
        dao_to_test_user.delete(user);
        Assert.assertNull(dao_to_test_user.getUser(1));
        Assert.assertNull(dao_to_test_user.getUser("Usuario 1"));

    }

    @After
    public void closeVolatileDB(){
        volatileDB.close();
    }


}
