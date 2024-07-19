package es.unex.infinitetime.CU02Unit;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.lang.reflect.Field;

public class UserUnitTest {

    @Test
    public void setIdTest() throws NoSuchFieldException, IllegalAccessException {
        long value = 1;
        es.unex.infinitetime.persistence.User instance = new es.unex.infinitetime.persistence.User();
        instance.setId(value);
        final Field field = instance.getClass().getDeclaredField("id");
        field.setAccessible(true);
        assertEquals("Fields didn't match", field.get(instance), value);
    }

    @Test
    public void setUserNameTest() throws NoSuchFieldException, IllegalAccessException {
        String value = "User1";
        es.unex.infinitetime.persistence.User instance = new es.unex.infinitetime.persistence.User();
        instance.setUsername(value);
        final Field field = instance.getClass().getDeclaredField("username");
        field.setAccessible(true);
        assertEquals("Fields didn't match", field.get(instance), value);
    }

    @Test
    public void setPasswordTest() throws NoSuchFieldException, IllegalAccessException {
        String value = "1234";
        es.unex.infinitetime.persistence.User instance = new es.unex.infinitetime.persistence.User();
        instance.setPassword(value);
        final Field field = instance.getClass().getDeclaredField("password");
        field.setAccessible(true);
        assertEquals("Fields didn't match", field.get(instance), value);
    }

    @Test
    public void setEmailTest() throws NoSuchFieldException, IllegalAccessException {
        String value = "user1@gmail.com";
        es.unex.infinitetime.persistence.User instance = new es.unex.infinitetime.persistence.User();
        instance.setEmail(value);
        final Field field = instance.getClass().getDeclaredField("email");
        field.setAccessible(true);
        assertEquals("Fields didn't match", field.get(instance), value);
    }

}
