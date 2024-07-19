package es.unex.infinitetime.CU07Unit;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import es.unex.infinitetime.api.FavoriteRemote;
import es.unex.infinitetime.api.ProjectRemote;
import es.unex.infinitetime.api.TaskRemote;
import es.unex.infinitetime.api.UserRemote;
import es.unex.infinitetime.persistence.Favorite;
import es.unex.infinitetime.persistence.Project;
import es.unex.infinitetime.persistence.Task;
import es.unex.infinitetime.persistence.TaskState;
import es.unex.infinitetime.persistence.User;


public class ConversionRemoteObjectsUnitTest {

    @Before
    public void setup () {
    }

    @Test
    public void userRemoteTest () throws ExecutionException, InterruptedException {

        UserRemote userRemote = new UserRemote();
        userRemote.setId("1");
        userRemote.setUsername("test");
        userRemote.setPassword("test");
        userRemote.setEmail("test@gmail.com");

        User user = User.userFromRemote(userRemote);
        assertEquals(1, user.getId());
        assertEquals("test", user.getUsername());
        assertEquals("test", user.getPassword());
        assertEquals("test@gmail.com", user.getEmail());

        userRemote = UserRemote.fromUser(user);
        assertEquals("1", userRemote.getId());
        assertEquals("test", userRemote.getUsername());
        assertEquals("test", userRemote.getPassword());
        assertEquals("test@gmail.com", userRemote.getEmail());
    }

    @Test
    public void projectRemoteTest () throws ExecutionException, InterruptedException {

        ProjectRemote projectRemote = new ProjectRemote();
        projectRemote.setId("1");
        projectRemote.setName("test");
        projectRemote.setDescription("test");
        projectRemote.setUserId("1");

        Project project = Project.projectFromRemote(projectRemote);
        assertEquals(1, project.getId());
        assertEquals("test", project.getName());
        assertEquals("test", project.getDescription());
        assertEquals(1, project.getUserId());

        projectRemote = ProjectRemote.fromProject(project);
        assertEquals("1", projectRemote.getId());
        assertEquals("test", projectRemote.getName());
        assertEquals("test", projectRemote.getDescription());
        assertEquals("1", projectRemote.getUserId());
    }

    @Test
    public void taskRemoteTask () throws ExecutionException, InterruptedException, ParseException {
        TaskRemote taskRemote = new TaskRemote();
        taskRemote.setId("1");
        taskRemote.setName("test");
        taskRemote.setDescription("test");
        taskRemote.setProjectId("1");
        taskRemote.setUserId("1");
        taskRemote.setState("1");
        taskRemote.setPriority("1");
        taskRemote.setDeadline("10-10-2022");

        Task task = Task.fromRemote(taskRemote);
        assertEquals(1, task.getId());
        assertEquals("test", task.getName());
        assertEquals("test", task.getDescription());
        assertEquals(1, task.getProjectId());
        assertEquals(1, task.getUserId());
        assertEquals(TaskState.DOING, task.getState());
        assertEquals(1, task.getPriority());
        Date date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse("10-10-2022");
        assertEquals(date, task.getDeadline());

        taskRemote = TaskRemote.fromTask(task);
        assertEquals("1", taskRemote.getId());
        assertEquals("test", taskRemote.getName());
        assertEquals("test", taskRemote.getDescription());
        assertEquals("1", taskRemote.getProjectId());
        assertEquals("1", taskRemote.getUserId());
        assertEquals("1", taskRemote.getState());
        assertEquals("1", taskRemote.getPriority());
        assertEquals("10-10-2022", taskRemote.getDeadline());

    }

    @Test
    public void FavoriteRemoteTest() throws ExecutionException, InterruptedException {
        FavoriteRemote favoriteRemote = new FavoriteRemote();
        favoriteRemote.setUserId("1");
        favoriteRemote.setTaskId("1");

        Favorite favorite = Favorite.fromRemote(favoriteRemote);
        assertEquals(1, favorite.getUserId());
        assertEquals(1, favorite.getTaskId());

        favoriteRemote = FavoriteRemote.fromFavorite(favorite);
        assertEquals("1", favoriteRemote.getUserId());
        assertEquals("1", favoriteRemote.getTaskId());
    }
}
