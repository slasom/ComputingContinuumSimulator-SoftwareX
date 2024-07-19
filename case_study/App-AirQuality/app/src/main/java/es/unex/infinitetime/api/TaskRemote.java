
package es.unex.infinitetime.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Locale;

import es.unex.infinitetime.persistence.Task;
import es.unex.infinitetime.persistence.TaskStateConverter;

public class TaskRemote {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("priority")
    @Expose
    private String priority;
    @SerializedName("deadline")
    @Expose
    private String deadline;
    @SerializedName("project_id")
    @Expose
    private String projectId;
    @SerializedName("user_id")
    @Expose
    private String userId;

    /**
     * No args constructor for use in serialization
     * 
     */
    public TaskRemote() {
    }

    /**
     * 
     * @param name
     * @param description
     * @param id
     * @param state
     * @param priority
     * @param deadline
     * @param projectId
     * @param userId
     */
    public TaskRemote(String id, String name, String description, String state, String priority, String deadline, String projectId, String userId) {
        super();
        this.id = id;
        this.name = name;
        this.description = description;
        this.state = state;
        this.priority = priority;
        this.deadline = deadline;
        this.projectId = projectId;
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public static TaskRemote fromTask(Task task) {
        return new TaskRemote(
                Long.toString(task.getId()),
                task.getName(),
                task.getDescription(),
                Long.toString(TaskStateConverter.toLong(task.getState())),
                Long.toString(task.getPriority()),
                new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(task.getDeadline()),
                Long.toString(task.getProjectId()),
                Long.toString(task.getUserId()));
    }

}
