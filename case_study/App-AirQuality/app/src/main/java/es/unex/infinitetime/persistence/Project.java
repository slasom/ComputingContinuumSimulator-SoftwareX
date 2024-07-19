package es.unex.infinitetime.persistence;

import static androidx.room.ForeignKey.CASCADE;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import es.unex.infinitetime.api.ProjectRemote;

@Entity(tableName = "project",
    foreignKeys = {
            @ForeignKey(entity = User.class,
                    parentColumns = "id",
                    childColumns = "user_id",
                    onDelete = CASCADE)
    }, indices = {@Index(value = {"user_id"})})
public class Project {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;
    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "description")
    private String description;
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

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public Project() {

    }

    @Ignore
    public Project(long id, String name, String description,long userId){
        this.id=id;
        this.name=name;
        this.description=description;
        this.userId=userId;
    }

    @Ignore
    public static Project projectFromRemote(ProjectRemote projectRemote) {
        return new Project(
                Long.parseLong(projectRemote.getId()),
                projectRemote.getName(),
                projectRemote.getDescription(),
                Long.parseLong(projectRemote.getUserId()));
    }
}
