
package es.unex.infinitetime.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import es.unex.infinitetime.persistence.Project;


public class ProjectRemote {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("user_id")
    @Expose
    private String userId;

    /**
     * No args constructor for use in serialization
     * 
     */
    public ProjectRemote() {
    }

    /**
     * 
     * @param name
     * @param description
     * @param id
     * @param userId
     */
    public ProjectRemote(String id, String name, String description, String userId) {
        super();
        this.id = id;
        this.name = name;
        this.description = description;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public static ProjectRemote fromProject(Project project) {
        return new ProjectRemote(
                Long.toString(project.getId()),
                project.getName(),
                project.getDescription(),
                Long.toString(project.getUserId()));
    }

}
