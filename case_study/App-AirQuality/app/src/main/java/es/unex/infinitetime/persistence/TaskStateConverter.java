package es.unex.infinitetime.persistence;


import androidx.room.TypeConverter;

public class TaskStateConverter {

    @TypeConverter
    public static long toLong(TaskState taskState) {
        return taskState.ordinal();
    }

    @TypeConverter
    public static TaskState toTaskState(long value) {
        return TaskState.values()[(int) value];
    }
}
