package es.unex.infinitetime.datosEjemplo;

import java.util.ArrayList;
import java.util.Date;

import es.unex.infinitetime.persistence.Favorite;
import es.unex.infinitetime.persistence.Project;
import es.unex.infinitetime.persistence.SharedProject;
import es.unex.infinitetime.persistence.Task;
import es.unex.infinitetime.persistence.TaskState;
import es.unex.infinitetime.persistence.User;

public class ExampleData {
    public ArrayList<User> users;
    public ArrayList<Task> tasks;
    public ArrayList<Project> projects;
    public ArrayList<Favorite> favorites;
    public ArrayList<SharedProject> sharedProjects;

    public ExampleData() {

        users = new ArrayList<>();
        tasks = new ArrayList<>();
        projects = new ArrayList<>();
        favorites = new ArrayList<>();
        sharedProjects = new ArrayList<>();

        User user1 = new User();
        user1.setId(1);
        user1.setUsername("user1");
        user1.setPassword("password1");
        user1.setEmail("email1@email.com");
        User user2 = new User();
        user2.setId(2);
        user2.setUsername("user2");
        user2.setPassword("password2");
        user2.setEmail("email2@email.com");
        User user3 = new User();
        user3.setId(3);
        user3.setUsername("user3");
        user3.setPassword("password3");
        user3.setEmail("email3@email.com");
        User user4 = new User();
        user4.setId(4);
        user4.setUsername("user4");
        user4.setPassword("password4");
        user4.setEmail("email4@email.com");
        User user5 = new User();
        user5.setId(5);
        user5.setUsername("user5");
        user5.setPassword("password5");
        user5.setEmail("email5@email.com");

        users.add(user1);
        users.add(user2);
        users.add(user3);
        users.add(user4);
        users.add(user5);

        Task task1 = new Task();
        task1.setId(1);
        task1.setName("task1");
        task1.setDescription("description1");
        task1.setState(TaskState.TODO);
        task1.setPriority(1);
        Date d1 = new Date(2020, 1, 15, 15, 30);
        task1.setDeadline(d1);
        task1.setProjectId(1);
        task1.setUserId(1);
        Task task2 = new Task();
        task2.setId(2);
        task2.setName("task2");
        task2.setDescription("description2");
        task2.setState(TaskState.DOING);
        task2.setPriority(2);
        Date d2 = new Date(2020, 2, 15, 15, 30);
        task2.setDeadline(d2);
        task2.setProjectId(2);
        task2.setUserId(2);
        Task task3 = new Task();
        task3.setId(1);
        task3.setName("task3");
        task3.setDescription("description3");
        task3.setState(TaskState.DONE);
        task3.setPriority(3);
        Date d3 = new Date(2020, 3, 15, 15, 30);
        task3.setDeadline(d3);
        task3.setProjectId(3);
        task3.setUserId(3);
        Task task4 = new Task();
        task4.setId(4);
        task4.setName("task4");
        task4.setDescription("description4");
        task4.setState(TaskState.TODO);
        task4.setPriority(4);
        Date d4 = new Date(2020, 4, 15, 15, 30);
        task4.setDeadline(d4);
        task4.setProjectId(4);
        task4.setUserId(4);
        Task task5 = new Task();
        task5.setId(5);
        task5.setName("task5");
        task5.setDescription("description5");
        task5.setState(TaskState.DOING);
        task5.setPriority(5);
        Date d5 = new Date(2020, 5, 15, 15, 30);
        task5.setDeadline(d5);
        task5.setProjectId(5);
        task5.setUserId(5);

        tasks.add(task1);
        tasks.add(task2);
        tasks.add(task3);
        tasks.add(task4);
        tasks.add(task5);

        Project project1 = new Project(1, "Project 1", "Description 1", 1);
        Project project2 = new Project(2, "Project 2", "Description 2", 2);
        Project project3 = new Project(3, "Project 3", "Description 3", 3);
        Project project4 = new Project(4, "Project 4", "Description 4", 4);
        Project project5 = new Project(5, "Project 5", "Description 5", 5);

        projects.add(project1);
        projects.add(project2);
        projects.add(project3);
        projects.add(project4);
        projects.add(project5);

        Favorite favorite1 = new Favorite();
        favorite1.setUserId(1);
        favorite1.setTaskId(1);
        Favorite favorite2 = new Favorite();
        favorite2.setUserId(2);
        favorite2.setTaskId(2);
        Favorite favorite3 = new Favorite();
        favorite3.setUserId(3);
        favorite3.setTaskId(3);
        Favorite favorite4 = new Favorite();
        favorite4.setUserId(4);
        favorite4.setTaskId(4);
        Favorite favorite5 = new Favorite();
        favorite5.setUserId(5);
        favorite5.setTaskId(5);

        favorites.add(favorite1);
        favorites.add(favorite2);
        favorites.add(favorite3);
        favorites.add(favorite4);
        favorites.add(favorite5);

        SharedProject sharedProject1 = new SharedProject();
        sharedProject1.setUserId(2);
        sharedProject1.setProjectId(1);
        SharedProject sharedProject2 = new SharedProject();
        sharedProject2.setUserId(1);
        sharedProject2.setProjectId(2);
        SharedProject sharedProject3 = new SharedProject();
        sharedProject3.setUserId(1);
        sharedProject3.setProjectId(3);
        SharedProject sharedProject4 = new SharedProject();
        sharedProject4.setUserId(5);
        sharedProject4.setProjectId(4);
        SharedProject sharedProject5 = new SharedProject();
        sharedProject5.setUserId(4);
        sharedProject5.setProjectId(5);
    }

    public ArrayList<User> getUsersList(){
        return this.users;
    }

    public ArrayList<Task> getTasksList(){
        return this.tasks;
    }

    public ArrayList<Project> getProjectsList(){
        return this.projects;
    }

    public ArrayList<Favorite> getFavoritesList(){
        return this.favorites;
    }

    public void deleteProject(long id){
        for (int i=0; i<projects.size(); i++){
            if (projects.get(i).getId() == id){
                projects.remove(i);
                return;
            }
        }
    }

    public ArrayList<SharedProject> getSharedProjectsList(){
        return this.sharedProjects;
    }

}
