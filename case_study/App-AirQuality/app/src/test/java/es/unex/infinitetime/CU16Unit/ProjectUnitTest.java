package es.unex.infinitetime.CU16Unit;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.lang.reflect.Field;

public class ProjectUnitTest {

    @Test
    public void getIdTest() throws NoSuchFieldException, IllegalAccessException {
        es.unex.infinitetime.persistence.Project instance = new es.unex.infinitetime.persistence.Project();
        final Field field = instance.getClass().getDeclaredField("id");
        field.setAccessible(true);
        field.set(instance, new Long(1));

        //when
        final Long result = instance.getId();

        //then
        assertEquals("field wasn't retrieved properly", result, new Long(1));
    }

    @Test
    public void getNameTest() throws NoSuchFieldException, IllegalAccessException {
        es.unex.infinitetime.persistence.Project instance = new es.unex.infinitetime.persistence.Project();
        final Field field = instance.getClass().getDeclaredField("name");
        field.setAccessible(true);
        field.set(instance, new String("Project1"));

        //when
        final String result = instance.getName();

        //then
        assertEquals("field wasn't retrieved properly", result, new String("Project1"));
    }

    @Test
    public void getDescriptionTest() throws NoSuchFieldException, IllegalAccessException {
        es.unex.infinitetime.persistence.Project instance = new es.unex.infinitetime.persistence.Project();
        final Field field = instance.getClass().getDeclaredField("description");
        field.setAccessible(true);
        field.set(instance, new String("Project1 description"));

        //when
        final String result = instance.getDescription();

        //then
        assertEquals("field wasn't retrieved properly", result, new String("Project1 description"));
    }

    @Test
    public void getUserIdTest() throws NoSuchFieldException, IllegalAccessException {
        es.unex.infinitetime.persistence.Project instance = new es.unex.infinitetime.persistence.Project();
        final Field field = instance.getClass().getDeclaredField("userId");
        field.setAccessible(true);
        field.set(instance, new Long(1));

        //when
        final Long result = instance.getUserId();

        //then
        assertEquals("field wasn't retrieved properly", result, new Long(1));
    }

}
