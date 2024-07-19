package es.unex.infinitetime.CU03Unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

import es.unex.infinitetime.persistence.TaskState;
import es.unex.infinitetime.persistence.TaskStateConverter;

public class TaskStateConverterUnitTest {

    @Test
    public void toLong_isCorrect() {
        TaskState value = TaskState.TODO;
        TaskState value2 = TaskState.DONE;
        long ordinal=0;

        assertEquals(ordinal, TaskStateConverter.toLong(value));
        assertNotEquals(ordinal, TaskStateConverter.toLong(value2));
    }

    @Test
    public void toTaskState_isCorrect() {
        TaskState value = TaskState.TODO;
        TaskState value2 = TaskState.DONE;
        long ordinal=0;

        assertEquals(value, TaskStateConverter.toTaskState(ordinal));
        assertNotEquals(value2, TaskStateConverter.toTaskState(ordinal));
    }
}
