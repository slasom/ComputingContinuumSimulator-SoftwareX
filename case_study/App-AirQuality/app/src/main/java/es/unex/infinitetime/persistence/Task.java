package es.unex.infinitetime.persistence;

import static androidx.room.ForeignKey.CASCADE;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import es.unex.infinitetime.api.TaskRemote;

@Entity(tableName = "task",
    foreignKeys = {
            @ForeignKey(entity = Project.class,
                    parentColumns = "id",
                    childColumns = "project_id",
                    onDelete = CASCADE)
    }, indices = {@Index(value = {"project_id"}), @Index(value = {"user_id"})})
public class Task {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "state")
    @TypeConverters(TaskStateConverter.class)
    private TaskState state = TaskState.TODO;

    @ColumnInfo(name ="priority")
    private long priority;

    @ColumnInfo(name = "effort")
    private long effort;

    @ColumnInfo(name = "deadline")
    @TypeConverters(DateConverter.class)
    private Date deadline;

    @ColumnInfo(name = "project_id")
    private long projectId;

    @ColumnInfo(name = "user_id")
    private long userId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskState getState() {
        return state;
    }

    public void setState(TaskState state) {
        this.state = state;
    }

    public long getPriority() {
        return priority;
    }

    public void setPriority(long priority) {
        this.priority = priority;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getEffort() {
        return effort;
    }

    public void setEffort(long effort) {
        this.effort = effort;
    }

    @Ignore
    public Task (long id, String name, String description, TaskState state, long priority, Date deadline, long projectId, long userId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.state = state;
        this.priority = priority;
        this.deadline = deadline;
        this.projectId = projectId;
        this.userId = userId;
    }

    public Task(){

    }

    @Ignore
    public static Task fromRemote(TaskRemote taskRemote) {
        Task task = new Task();
        task.setId(Long.parseLong(taskRemote.getId()));
        task.setName(taskRemote.getName());
        task.setDescription(taskRemote.getDescription());
        task.setState(TaskStateConverter.toTaskState(Long.parseLong(taskRemote.getState())));
        task.setPriority(Long.parseLong(taskRemote.getPriority()));
        try {
            task.setDeadline(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(taskRemote.getDeadline()));
        } catch (ParseException e) {
            e.printStackTrace();
        };
        task.setProjectId(Long.parseLong(taskRemote.getProjectId()));
        task.setUserId(Long.parseLong(taskRemote.getUserId()));
        return task;
    }

}
