package es.unex.infinitetime.CU03Unit;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Date;

import es.unex.infinitetime.persistence.TaskState;

public class TaskUnitTest {

    @Test
    public void setIdTest() throws NoSuchFieldException, IllegalAccessException {
        long value = 1;
        es.unex.infinitetime.persistence.Task instance = new es.unex.infinitetime.persistence.Task();
        instance.setId(value);
        final Field field = instance.getClass().getDeclaredField("id");
        field.setAccessible(true);
        assertEquals("Fields didn't match", field.get(instance), value);
    }

    @Test
    public void setNameTest() throws NoSuchFieldException, IllegalAccessException {
        String value = "Task1";
        es.unex.infinitetime.persistence.Task instance = new es.unex.infinitetime.persistence.Task();
        instance.setName(value);
        final Field field = instance.getClass().getDeclaredField("name");
        field.setAccessible(true);
        assertEquals("Fields didn't match", field.get(instance), value);
    }


    @Test
    public void setDescriptionTest() throws NoSuchFieldException, IllegalAccessException {
        String value = "Task1 description";
        es.unex.infinitetime.persistence.Task instance = new es.unex.infinitetime.persistence.Task();
        instance.setDescription(value);
        final Field field = instance.getClass().getDeclaredField("description");
        field.setAccessible(true);
        assertEquals("Fields didn't match", field.get(instance), value);
    }

    @Test
    public void setStateTest() throws NoSuchFieldException, IllegalAccessException {
        TaskState value = TaskState.TODO;
        es.unex.infinitetime.persistence.Task instance = new es.unex.infinitetime.persistence.Task();
        instance.setState(value);
        final Field field = instance.getClass().getDeclaredField("state");
        field.setAccessible(true);
        assertEquals("Fields didn't match", field.get(instance), value);
    }

    @Test
    public void setPriorityTest() throws NoSuchFieldException, IllegalAccessException {
        long value = 1;
        es.unex.infinitetime.persistence.Task instance = new es.unex.infinitetime.persistence.Task();
        instance.setPriority(value);
        final Field field = instance.getClass().getDeclaredField("priority");
        field.setAccessible(true);
        assertEquals("Fields didn't match", field.get(instance), value);
    }

    @Test
    public void setEffortTest() throws NoSuchFieldException, IllegalAccessException {
        long value = 1;
        es.unex.infinitetime.persistence.Task instance = new es.unex.infinitetime.persistence.Task();
        instance.setEffort(value);
        final Field field = instance.getClass().getDeclaredField("effort");
        field.setAccessible(true);
        assertEquals("Fields didn't match", field.get(instance), value);
    }

    @Test
    public void setDeadlineTest() throws NoSuchFieldException, IllegalAccessException {
        Date value = new Date(2000,1,1);
        es.unex.infinitetime.persistence.Task instance = new es.unex.infinitetime.persistence.Task();
        instance.setDeadline(value);
        final Field field = instance.getClass().getDeclaredField("deadline");
        field.setAccessible(true);
        assertEquals("Fields didn't match", field.get(instance), value);
    }

    @Test
    public void setProjectIdTest() throws NoSuchFieldException, IllegalAccessException {
        long value = 1;
        es.unex.infinitetime.persistence.Task instance = new es.unex.infinitetime.persistence.Task();
        instance.setProjectId(value);
        final Field field = instance.getClass().getDeclaredField("projectId");
        field.setAccessible(true);
        assertEquals("Fields didn't match", field.get(instance), value);
    }

    @Test
    public void setUserIdTest() throws NoSuchFieldException, IllegalAccessException {
        long value = 1;
        es.unex.infinitetime.persistence.Task instance = new es.unex.infinitetime.persistence.Task();
        instance.setUserId(value);
        final Field field = instance.getClass().getDeclaredField("userId");
        field.setAccessible(true);
        assertEquals("Fields didn't match", field.get(instance), value);
    }
}
