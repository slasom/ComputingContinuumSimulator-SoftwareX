package es.unex.infinitetime.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ProjectRemoteDAO {

    @GET("tabs/projects/user_id/{user_id}")
    public Call<List<ProjectRemote>> getProjectsByUser(@Path("user_id") String userId);
    @GET("tabs/projects")
    public Call<List<ProjectRemote>> getAllProjects();
    @POST("tabs/projects")
    public Call<List<ProjectRemote>> insertProjects(@Body List<ProjectRemote> projects);

    @DELETE("tabs/projects/user_id/{user_id}")
    public Call<List<ProjectRemote>> deleteProjectsByUser(@Path("user_id") String userId);
}
