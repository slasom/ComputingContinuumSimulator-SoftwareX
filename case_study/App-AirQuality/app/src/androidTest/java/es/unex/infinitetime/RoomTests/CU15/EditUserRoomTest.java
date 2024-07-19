package es.unex.infinitetime.RoomTests.CU15;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import es.unex.infinitetime.persistence.InfiniteDatabase;
import es.unex.infinitetime.persistence.User;
import es.unex.infinitetime.persistence.UserDAO;

public class EditUserRoomTest {
    private InfiniteDatabase volatileDB;
    private UserDAO dao_to_test_user;

    @Before
    public void createVolatileDB(){
        Context context = ApplicationProvider.getApplicationContext();
        volatileDB = Room.inMemoryDatabaseBuilder(context, InfiniteDatabase.class).allowMainThreadQueries().build();
        dao_to_test_user = volatileDB.userDAO();
        Assert.assertNotNull(dao_to_test_user);
    }

    @Test
    public void editUser(){
        User item1 = new User();
        item1.setId(1);
        item1.setUsername("Usuario 1");
        item1.setEmail("emailUser1@unex.es");
        item1.setPassword("12345");

        dao_to_test_user.insert(item1);

        List<User> items = dao_to_test_user.getAllUsers();
        User recoveredItem = items.get(0);

        recoveredItem.setUsername("Usuario 1 Actualizado");

        dao_to_test_user.update(recoveredItem);

        items = dao_to_test_user.getAllUsers();
        Assert.assertEquals("Usuario 1 Actualizado", items.get(0).getUsername());


    }

    @After
    public void closeVolatileDB(){
        volatileDB.close();
    }
}
