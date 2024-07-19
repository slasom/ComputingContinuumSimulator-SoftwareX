package es.unex.infinitetime.api;

import android.util.Log;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.stream.Collectors;

import es.unex.infinitetime.AppExecutors;
import es.unex.infinitetime.persistence.Favorite;
import es.unex.infinitetime.persistence.InfiniteDatabase;
import es.unex.infinitetime.persistence.Project;
import es.unex.infinitetime.persistence.ProjectDAO;
import es.unex.infinitetime.persistence.Task;
import es.unex.infinitetime.persistence.TaskDAO;
import es.unex.infinitetime.persistence.User;
import es.unex.infinitetime.persistence.UserDAO;
import es.unex.infinitetime.ui.login.PersistenceUser;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DownloadFromAPI implements Runnable{

    public static final String BASE_URL = "https://sheet.best/api/sheets/421e4602-4291-4ed8-aed3-83969a14ea31/";

    private final UserRemoteDAO userRemoteDAO;
    private final ProjectRemoteDAO projectRemoteDAO;
    private final TaskRemoteDAO taskRemoteDAO;
    private final FavoriteRemoteDAO favoriteRemoteDAO;

    private final UserDAO userDAO;
    private final ProjectDAO projectDAO;
    private final TaskDAO taskDAO;

    private long userId;

    public DownloadFromAPI() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        userRemoteDAO = retrofit.create(UserRemoteDAO.class);
        projectRemoteDAO = retrofit.create(ProjectRemoteDAO.class);
        taskRemoteDAO = retrofit.create(TaskRemoteDAO.class);
        favoriteRemoteDAO = retrofit.create(FavoriteRemoteDAO.class);

        userDAO = InfiniteDatabase.getDatabase(null).userDAO();
        projectDAO = InfiniteDatabase.getDatabase(null).projectDAO();
        taskDAO = InfiniteDatabase.getDatabase(null).taskDAO();

        userId = PersistenceUser.getInstance().getUserId();
    }

    @Override
    public void run() {
        try {
            User user = userDAO.getUser(userId);
            UserRemote userRemote = userRemoteDAO.getUser(String.valueOf(user.getId())).execute().body().get(0);
            if(userRemote == null){
                Log.d("DownloadFromAPI", "User not found");
                return;
            }
            User userUpdate = User.userFromRemote(userRemote);
            userDAO.update(userUpdate);

            List<ProjectRemote> projectsRemote = projectRemoteDAO.getProjectsByUser(userRemote.getId()).execute().body();
            assert projectsRemote != null;
            List<Project> projects = projectsRemote.stream().map(Project::projectFromRemote).collect(Collectors.toList());
            userDAO.deleteProjectsCreated(userUpdate.getId());
            projects.forEach(projectDAO::insert);

            List<TaskRemote> tasksRemote = taskRemoteDAO.getTasksByUser(userRemote.getId()).execute().body();
            assert tasksRemote != null;
            List<Task> tasks = tasksRemote.stream().map(Task::fromRemote).collect(Collectors.toList());
            userDAO.deleteTasksCreated(userUpdate.getId());
            tasks.forEach(taskDAO::insert);

            List<FavoriteRemote> favoritesRemote = favoriteRemoteDAO.getFavorites(userRemote.getId()).execute().body();
            assert favoritesRemote != null;
            List<Favorite> favorites = favoritesRemote.stream().map(Favorite::fromRemote).collect(Collectors.toList());
            userDAO.deleteFavorites(userUpdate.getId());
            favorites.forEach(userDAO::insertFavorite);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
