package es.unex.infinitetime.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface TaskRemoteDAO {

    @GET("tabs/tasks/user_id/{user_id}")
    public Call<List<TaskRemote>> getTasksByUser(@Path("user_id") String userId);

    @POST("tabs/tasks")
    public Call<List<TaskRemote>> insertTasks(@Body List<TaskRemote> tasks);

    @DELETE("tabs/tasks/user_id/{user_id}")
    public Call<List<TaskRemote>> deleteTasksByUser(@Path("user_id") String userId);

}
