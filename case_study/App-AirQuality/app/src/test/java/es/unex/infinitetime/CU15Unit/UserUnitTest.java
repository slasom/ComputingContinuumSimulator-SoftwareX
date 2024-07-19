package es.unex.infinitetime.CU15Unit;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.lang.reflect.Field;

public class UserUnitTest {

    @Test
    public void getIdTest() throws NoSuchFieldException, IllegalAccessException {
        es.unex.infinitetime.persistence.User instance = new es.unex.infinitetime.persistence.User();
        final Field field = instance.getClass().getDeclaredField("id");
        field.setAccessible(true);
        field.set(instance, new Long(1));

        //when
        final Long result = instance.getId();

        //then
        assertEquals("field wasn't retrieved properly", result, new Long(1));
    }

    @Test
    public void getUserNameTest() throws NoSuchFieldException, IllegalAccessException {
        es.unex.infinitetime.persistence.User instance = new es.unex.infinitetime.persistence.User();
        final Field field = instance.getClass().getDeclaredField("username");
        field.setAccessible(true);
        field.set(instance, new String("User1"));

        //when
        final String result = instance.getUsername();

        //then
        assertEquals("field wasn't retrieved properly", result, new String("User1"));
    }

    @Test
    public void getPasswordTest() throws NoSuchFieldException, IllegalAccessException {
        es.unex.infinitetime.persistence.User instance = new es.unex.infinitetime.persistence.User();
        final Field field = instance.getClass().getDeclaredField("password");
        field.setAccessible(true);
        field.set(instance, new String("1234"));

        //when
        final String result = instance.getPassword();

        //then
        assertEquals("field wasn't retrieved properly", result, new String("1234"));
    }

    @Test
    public void getEmailTest() throws NoSuchFieldException, IllegalAccessException {
        es.unex.infinitetime.persistence.User instance = new es.unex.infinitetime.persistence.User();
        final Field field = instance.getClass().getDeclaredField("email");
        field.setAccessible(true);
        field.set(instance, new String("user1@gmail.com"));

        //when
        final String result = instance.getEmail();

        //then
        assertEquals("field wasn't retrieved properly", result, new String("user1@gmail.com"));
    }
}
