package es.unex.infinitetime.CU07Unit;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import es.unex.infinitetime.api.FavoriteRemote;
import es.unex.infinitetime.api.FavoriteRemoteDAO;
import es.unex.infinitetime.api.ProjectRemote;
import es.unex.infinitetime.api.ProjectRemoteDAO;
import es.unex.infinitetime.api.TaskRemote;
import es.unex.infinitetime.api.TaskRemoteDAO;
import es.unex.infinitetime.api.UserRemote;
import es.unex.infinitetime.api.UserRemoteDAO;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class APITest {

    private Retrofit retrofit;
    private MockWebServer mockWebServer;
    private ObjectMapper objectMapper;

    @Before
    public void setUp(){
        mockWebServer = new MockWebServer();
        retrofit = new Retrofit.Builder()
                .baseUrl(mockWebServer.url("").toString())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void userRemoteDAOTest() throws IOException {
        UserRemote userRemote = new UserRemote();
        userRemote.setId("1");
        userRemote.setUsername("username");
        userRemote.setPassword("password");
        userRemote.setEmail("email");

        List<UserRemote> userRemotes = new ArrayList<>();
        userRemotes.add(userRemote);

        MockResponse mockResponse = new MockResponse();
        mockResponse.setResponseCode(200);
        try{
            mockResponse.setBody(objectMapper.writeValueAsString(userRemotes));
        } catch (IOException e) {
            e.printStackTrace();
        }
        mockWebServer.enqueue(mockResponse);

        UserRemoteDAO userRemoteDAO = retrofit.create(UserRemoteDAO.class);
        Call<List<UserRemote>> call = userRemoteDAO.getUser("1");

        Response<List<UserRemote>> response = call.execute();

        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.code());

        List<UserRemote> userRemotesResponse = response.body();

        UserRemote userRemoteReceived = userRemotesResponse.get(0);

        Assert.assertEquals(userRemote.getId(), userRemoteReceived.getId());
        Assert.assertEquals(userRemote.getUsername(), userRemoteReceived.getUsername());
        Assert.assertEquals(userRemote.getPassword(), userRemoteReceived.getPassword());
        Assert.assertEquals(userRemote.getEmail(), userRemoteReceived.getEmail());
    }

    @Test
    public void projectRemoteDAOTest() throws IOException {
        ProjectRemote projectRemote = new ProjectRemote();
        projectRemote.setId("1");
        projectRemote.setName("name");
        projectRemote.setDescription("description");
        projectRemote.setUserId("1");

        List<ProjectRemote> projectRemotes = new ArrayList<>();
        projectRemotes.add(projectRemote);

        MockResponse mockResponse = new MockResponse();
        mockResponse.setResponseCode(200);
        try{
            mockResponse.setBody(objectMapper.writeValueAsString(projectRemotes));
        } catch (IOException e) {
            e.printStackTrace();
        }
        mockWebServer.enqueue(mockResponse);

        ProjectRemoteDAO projectRemoteDAO = retrofit.create(ProjectRemoteDAO.class);
        Call<List<ProjectRemote>> call = projectRemoteDAO.getProjectsByUser("1");

        Response<List<ProjectRemote>> response = call.execute();

        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.code());

        List<ProjectRemote> projectRemotesResponse = response.body();

        Assert.assertEquals(1, projectRemotesResponse.size());

        ProjectRemote projectRemoteReceived = projectRemotesResponse.get(0);

        Assert.assertEquals(projectRemote.getId(), projectRemoteReceived.getId());
        Assert.assertEquals(projectRemote.getName(), projectRemoteReceived.getName());
        Assert.assertEquals(projectRemote.getDescription(), projectRemoteReceived.getDescription());

    }

    @Test
    public void taskRemoteDAOTest() throws IOException {
        TaskRemote taskRemote = new TaskRemote();
        taskRemote.setId("1");
        taskRemote.setName("test");
        taskRemote.setDescription("test");
        taskRemote.setProjectId("1");
        taskRemote.setUserId("1");
        taskRemote.setState("1");
        taskRemote.setPriority("1");
        taskRemote.setDeadline("10-10-2022");

        List<TaskRemote> taskRemotes = new ArrayList<>();
        taskRemotes.add(taskRemote);

        MockResponse mockResponse = new MockResponse();
        mockResponse.setResponseCode(200);
        try{
            mockResponse.setBody(objectMapper.writeValueAsString(taskRemotes));
        } catch (IOException e) {
            e.printStackTrace();
        }
        mockWebServer.enqueue(mockResponse);

        TaskRemoteDAO taskRemoteDAO = retrofit.create(TaskRemoteDAO.class);
        Call<List<TaskRemote>> call = taskRemoteDAO.getTasksByUser("1");

        Response<List<TaskRemote>> response = call.execute();

        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.code());

        List<TaskRemote> taskRemotesResponse = response.body();

        Assert.assertEquals(1, taskRemotesResponse.size());

        TaskRemote taskRemoteReceived = taskRemotesResponse.get(0);

        Assert.assertEquals(taskRemote.getId(), taskRemoteReceived.getId());
        Assert.assertEquals(taskRemote.getName(), taskRemoteReceived.getName());
        Assert.assertEquals(taskRemote.getDescription(), taskRemoteReceived.getDescription());
        Assert.assertEquals(taskRemote.getState(), taskRemoteReceived.getState());
        Assert.assertEquals(taskRemote.getPriority(), taskRemoteReceived.getPriority());
        Assert.assertEquals(taskRemote.getDeadline(), taskRemoteReceived.getDeadline());

    }

    @Test
    public void favoriteRemoteDAO() throws IOException {

        FavoriteRemote favoriteRemote = new FavoriteRemote();
        favoriteRemote.setUserId("1");
        favoriteRemote.setTaskId("1");

        List<FavoriteRemote> favoriteRemotes = new ArrayList<>();
        favoriteRemotes.add(favoriteRemote);

        MockResponse mockResponse = new MockResponse();
        mockResponse.setResponseCode(200);

        try{
            mockResponse.setBody(objectMapper.writeValueAsString(favoriteRemotes));
        } catch (IOException e) {
            e.printStackTrace();
        }

        mockWebServer.enqueue(mockResponse);

        FavoriteRemoteDAO favoriteRemoteDAO = retrofit.create(FavoriteRemoteDAO.class);

        Call<List<FavoriteRemote>> call = favoriteRemoteDAO.getFavorites("1");

        Response<List<FavoriteRemote>> response = call.execute();

        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.code());

        List<FavoriteRemote> favoriteRemotesResponse = response.body();

        Assert.assertEquals(1, favoriteRemotesResponse.size());

        FavoriteRemote favoriteRemoteReceived = favoriteRemotesResponse.get(0);

        Assert.assertNotNull(favoriteRemoteReceived);

    }

    @After
    public void tearDown() throws IOException {
        mockWebServer.shutdown();
    }
}
