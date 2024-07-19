package es.unex.infinitetime.CU01Unit;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.lang.reflect.Field;

public class ProjectUnitTest {
    @Test
    public void setIdTest() throws NoSuchFieldException, IllegalAccessException {
        long value = 1;
        es.unex.infinitetime.persistence.Project instance = new es.unex.infinitetime.persistence.Project();
        instance.setId(value);
        final Field field = instance.getClass().getDeclaredField("id");
        field.setAccessible(true);
        assertEquals("Fields didn't match", field.get(instance), value);
    }

    @Test
    public void setNameTest() throws NoSuchFieldException, IllegalAccessException {
        String value = "Project1";
        es.unex.infinitetime.persistence.Project instance = new es.unex.infinitetime.persistence.Project();
        instance.setName(value);
        final Field field = instance.getClass().getDeclaredField("name");
        field.setAccessible(true);
        assertEquals("Fields didn't match", field.get(instance), value);
    }

    @Test
    public void setDescriptionTest() throws NoSuchFieldException, IllegalAccessException {
        String value = "Project1 description";
        es.unex.infinitetime.persistence.Project instance = new es.unex.infinitetime.persistence.Project();
        instance.setDescription(value);
        final Field field = instance.getClass().getDeclaredField("description");
        field.setAccessible(true);
        assertEquals("Fields didn't match", field.get(instance), value);
    }

    @Test
    public void setUserIdTest() throws NoSuchFieldException, IllegalAccessException {
        long value = 1;
        es.unex.infinitetime.persistence.Project instanceProject = new es.unex.infinitetime.persistence.Project();
        instanceProject.setUserId(value);
        final Field field = instanceProject.getClass().getDeclaredField("userId");
        field.setAccessible(true);
        assertEquals("Fields didn't match", field.get(instanceProject), value);
    }

}
