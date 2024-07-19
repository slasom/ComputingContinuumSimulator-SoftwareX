package es.unex.infinitetime.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UserRemoteDAO {

    @GET("tabs/users/id/{id}")
    public Call<List<UserRemote>> getUser(@Path("id") String id);

    @POST("tabs/users")
    public Call<List<UserRemote>> insertUser(@Body UserRemote user);

    @DELETE("tabs/users/id/{id}")
    public Call<List<UserRemote>> deleteUser(@Path("id") String id);

}
