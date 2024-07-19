package es.unex.infinitetime.CU04Unit;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.lang.reflect.Field;

public class FavoriteUnitTest {



    @Test
    public void setUserIdTest() throws NoSuchFieldException, IllegalAccessException {
        long value = 1;
        es.unex.infinitetime.persistence.Favorite instance = new es.unex.infinitetime.persistence.Favorite();
        instance.setUserId(value);
        final Field field = instance.getClass().getDeclaredField("userId");
        field.setAccessible(true);
        assertEquals("Fields didn't match", field.get(instance), value);
    }

    @Test
    public void setTaskIdTest() throws NoSuchFieldException, IllegalAccessException {
        long value = 1;
        es.unex.infinitetime.persistence.Favorite instance = new es.unex.infinitetime.persistence.Favorite();
        instance.setTaskId(value);
        final Field field = instance.getClass().getDeclaredField("taskId");
        field.setAccessible(true);
        assertEquals("Fields didn't match", field.get(instance), value);
    }



    @Test
    public void getUserIdTest() throws NoSuchFieldException, IllegalAccessException {
        es.unex.infinitetime.persistence.Favorite instance = new es.unex.infinitetime.persistence.Favorite();
        final Field field = instance.getClass().getDeclaredField("userId");
        field.setAccessible(true);
        field.set(instance, new Long(1));

        //when
        final Long result = instance.getUserId();

        //then
        assertEquals("field wasn't retrieved properly", result, new Long(1));
    }

    @Test
    public void getTaskIdTest() throws NoSuchFieldException, IllegalAccessException {
        es.unex.infinitetime.persistence.Favorite instance = new es.unex.infinitetime.persistence.Favorite();
        final Field field = instance.getClass().getDeclaredField("taskId");
        field.setAccessible(true);
        field.set(instance, new Long(1));

        //when
        final Long result = instance.getTaskId();

        //then
        assertEquals("field wasn't retrieved properly", result, new Long(1));
    }
}
