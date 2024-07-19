package es.unex.infinitetime.api;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import es.unex.infinitetime.persistence.InfiniteDatabase;
import es.unex.infinitetime.persistence.User;
import es.unex.infinitetime.persistence.UserDAO;
import es.unex.infinitetime.ui.login.PersistenceUser;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UploadToAPI implements Runnable {

    public static final String BASE_URL = "https://sheet.best/api/sheets/421e4602-4291-4ed8-aed3-83969a14ea31/";

    private final UserRemoteDAO userRemoteDAO;
    private final ProjectRemoteDAO projectRemoteDAO;
    private final TaskRemoteDAO taskRemoteDAO;
    private final FavoriteRemoteDAO favoriteRemoteDAO;

    private final UserDAO userDAO;

    private final long userId;


    public UploadToAPI() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        userRemoteDAO = retrofit.create(UserRemoteDAO.class);
        projectRemoteDAO = retrofit.create(ProjectRemoteDAO.class);
        taskRemoteDAO = retrofit.create(TaskRemoteDAO.class);
        favoriteRemoteDAO = retrofit.create(FavoriteRemoteDAO.class);

        userDAO = InfiniteDatabase.getDatabase(null).userDAO();

        userId = PersistenceUser.getInstance().getUserId();
    }

    @Override
    public void run() {
        try {
            User user = userDAO.getUser(userId);
            UserRemote userRemote = UserRemote.fromUser(userDAO.getUser(user.getId()));
            userRemoteDAO.deleteUser(userRemote.getId()).execute();
            userRemoteDAO.insertUser(userRemote).execute();

            List<ProjectRemote> projectsRemote = userDAO.getProjectsCreated(user.getId()).stream().map(
                    ProjectRemote::fromProject
            ).collect(Collectors.toList());

            projectRemoteDAO.deleteProjectsByUser(userRemote.getId()).execute();
            projectRemoteDAO.insertProjects(projectsRemote).execute();

            List<TaskRemote> tasksRemote = userDAO.getTasksCreated(user.getId()).stream().map(
                    TaskRemote::fromTask
            ).collect(Collectors.toList());

            taskRemoteDAO.deleteTasksByUser(userRemote.getId()).execute();
            taskRemoteDAO.insertTasks(tasksRemote).execute();

            List<FavoriteRemote> favoritesRemote = userDAO.getFavorites(user.getId()).stream().map(
                    FavoriteRemote::fromFavorite
            ).collect(Collectors.toList());

            favoriteRemoteDAO.deleteFavorites(userRemote.getId()).execute();
            favoriteRemoteDAO.insertFavorites(favoritesRemote).execute();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
