package es.unex.infinitetime.RoomTests.CU02;

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
import es.unex.infinitetime.persistence.User;
import es.unex.infinitetime.persistence.UserDAO;

@RunWith(AndroidJUnit4.class)
public class AddUserRoomTest {

    private InfiniteDatabase volatileDB;
    private UserDAO dao_to_test_user;

    @Before
    public void createVolatileDB(){
        Context context = ApplicationProvider.getApplicationContext();
        volatileDB = Room.inMemoryDatabaseBuilder(context, InfiniteDatabase.class).allowMainThreadQueries().build();
        dao_to_test_user = volatileDB.userDAO();
    }

    @Test
    public void addUser(){
        User user = new User();
        user.setId(1);
        user.setUsername("username");
        user.setPassword("password");
        user.setEmail("email");
        dao_to_test_user.insert(user);
        User getUser = dao_to_test_user.getUser(1);
        Assert.assertNotNull(getUser);
        Assert.assertEquals(getUser.getId(), user.getId());
        Assert.assertEquals(getUser.getUsername(), user.getUsername());
        Assert.assertEquals(getUser.getPassword(), user.getPassword());
        Assert.assertEquals(getUser.getEmail(), user.getEmail());
        Assert.assertEquals(dao_to_test_user.getAllUsers().size(), 1);
    }

    @After
    public void closeDB(){
        volatileDB.close();
    }

}
