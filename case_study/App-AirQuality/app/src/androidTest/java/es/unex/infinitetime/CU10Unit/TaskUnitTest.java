package es.unex.infinitetime.CU10Unit;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Date;

import es.unex.infinitetime.persistence.TaskState;

public class TaskUnitTest {

    @Test
    public void getIdTest() throws NoSuchFieldException, IllegalAccessException {
        es.unex.infinitetime.persistence.Task instance = new es.unex.infinitetime.persistence.Task();
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
        es.unex.infinitetime.persistence.Task instance = new es.unex.infinitetime.persistence.Task();
        final Field field = instance.getClass().getDeclaredField("name");
        field.setAccessible(true);
        field.set(instance, new String("Task1"));

        //when
        final String result = instance.getName();

        //then
        assertEquals("field wasn't retrieved properly", result, new String("Task1"));
    }


    @Test
    public void getDescriptionTest() throws NoSuchFieldException, IllegalAccessException {
        es.unex.infinitetime.persistence.Task instance = new es.unex.infinitetime.persistence.Task();
        final Field field = instance.getClass().getDeclaredField("description");
        field.setAccessible(true);
        field.set(instance, new String("Task1 description"));

        //when
        final String result = instance.getDescription();

        //then
        assertEquals("field wasn't retrieved properly", result, new String("Task1 description"));
    }

    @Test
    public void getStateTest() throws NoSuchFieldException, IllegalAccessException {
        es.unex.infinitetime.persistence.Task instance = new es.unex.infinitetime.persistence.Task();
        final Field field = instance.getClass().getDeclaredField("state");
        field.setAccessible(true);
        field.set(instance, TaskState.TODO);

        //when
        final TaskState result = instance.getState();

        //then
        assertEquals("field wasn't retrieved properly", result, TaskState.TODO);
    }

    @Test
    public void getPriorityTest() throws NoSuchFieldException, IllegalAccessException {
        es.unex.infinitetime.persistence.Task instance = new es.unex.infinitetime.persistence.Task();
        final Field field = instance.getClass().getDeclaredField("priority");
        field.setAccessible(true);
        field.set(instance, new Long(1));

        //when
        final Long result = instance.getPriority();

        //then
        assertEquals("field wasn't retrieved properly", result, new Long(1));
    }

    @Test
    public void getEffortTest() throws NoSuchFieldException, IllegalAccessException {
        es.unex.infinitetime.persistence.Task instance = new es.unex.infinitetime.persistence.Task();
        final Field field = instance.getClass().getDeclaredField("effort");
        field.setAccessible(true);
        field.set(instance, new Long(1));

        //when
        final Long result = instance.getEffort();

        //then
        assertEquals("field wasn't retrieved properly", result, new Long(1));
    }

    @Test
    public void getDeadlineTest() throws NoSuchFieldException, IllegalAccessException {
        es.unex.infinitetime.persistence.Task instance = new es.unex.infinitetime.persistence.Task();
        final Field field = instance.getClass().getDeclaredField("deadline");
        field.setAccessible(true);
        field.set(instance, new Date(2000,1,1));

        //when
        final Date result = instance.getDeadline();

        //then
        assertEquals("field wasn't retrieved properly", result, new Date(2000,1,1));
    }


    @Test
    public void geProjectIdTest() throws NoSuchFieldException, IllegalAccessException {
        es.unex.infinitetime.persistence.Task instance = new es.unex.infinitetime.persistence.Task();
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
        es.unex.infinitetime.persistence.Task instance = new es.unex.infinitetime.persistence.Task();
        final Field field = instance.getClass().getDeclaredField("userId");
        field.setAccessible(true);
        field.set(instance, new Long(1));

        //when
        final Long result = instance.getUserId();

        //then
        assertEquals("field wasn't retrieved properly", result, new Long(1));
    }









}
