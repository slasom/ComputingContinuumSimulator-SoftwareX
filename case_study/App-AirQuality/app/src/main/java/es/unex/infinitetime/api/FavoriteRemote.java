
package es.unex.infinitetime.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import es.unex.infinitetime.persistence.Favorite;

public class FavoriteRemote {

    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("task_id")
    @Expose
    private String taskId;

    /**
     * No args constructor for use in serialization
     * 
     */
    public FavoriteRemote() {
    }

    /**
     * 
     * @param userId
     * @param taskId
     */
    public FavoriteRemote(String userId, String taskId) {
        super();
        this.userId = userId;
        this.taskId = taskId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public static FavoriteRemote fromFavorite(Favorite favorite) {
        return new FavoriteRemote(
                Long.toString(favorite.getUserId()),
                Long.toString(favorite.getTaskId()));
    }

}
