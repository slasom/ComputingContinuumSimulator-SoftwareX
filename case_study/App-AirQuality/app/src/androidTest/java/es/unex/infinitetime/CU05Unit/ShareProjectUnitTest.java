package es.unex.infinitetime.CU05Unit;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.lang.reflect.Field;

public class ShareProjectUnitTest {

    @Test
    public void setProjectIdTest() throws NoSuchFieldException, IllegalAccessException {
        long value = 1;
        es.unex.infinitetime.persistence.SharedProject instance = new es.unex.infinitetime.persistence.SharedProject();
        instance.setProjectId(value);
        final Field field = instance.getClass().getDeclaredField("projectId");
        field.setAccessible(true);
        assertEquals("Fields didn't match", field.get(instance), value);
    }

    @Test
    public void setUserIdTest() throws NoSuchFieldException, IllegalAccessException {
        long value = 1;
        es.unex.infinitetime.persistence.SharedProject instance = new es.unex.infinitetime.persistence.SharedProject();
        instance.setUserId(value);
        final Field field = instance.getClass().getDeclaredField("userId");
        field.setAccessible(true);
        assertEquals("Fields didn't match", field.get(instance), value);
    }

    @Test
    public void getProjectIdTest() throws NoSuchFieldException, IllegalAccessException {
        es.unex.infinitetime.persistence.SharedProject instance = new es.unex.infinitetime.persistence.SharedProject();
        final Field field = instance.getClass().getDeclaredField("projectId");
        field.setAccessible(true);
        field.set(instance, new Long(1));

        //when
        final Long result = instance.getProjectId();

        //then
        assertEquals("field wasn't retrieved properly", result, new Long(1));
    }

    @Test
    public void getUserIdTest() throws NoSuchFieldException, IllegalAccessException {
        es.unex.infinitetime.persistence.SharedProject instance = new es.unex.infinitetime.persistence.SharedProject();
        final Field field = instance.getClass().getDeclaredField("userId");
        field.setAccessible(true);
        field.set(instance, new Long(1));

        //when
        final Long result = instance.getUserId();

        //then
        assertEquals("field wasn't retrieved properly", result, new Long(1));
    }

}
