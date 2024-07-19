package es.unex.infinitetime.ui.task;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import es.unex.infinitetime.AppExecutors;
import es.unex.infinitetime.R;
import es.unex.infinitetime.databinding.FragmentTaskBinding;
import es.unex.infinitetime.persistence.DateConverter;
import es.unex.infinitetime.persistence.InfiniteDatabase;
import es.unex.infinitetime.persistence.Project;
import es.unex.infinitetime.persistence.Task;
import es.unex.infinitetime.persistence.TaskState;
import es.unex.infinitetime.persistence.User;
import es.unex.infinitetime.ui.login.PersistenceUser;


public class AddTaskFragment extends Fragment {

    private FragmentTaskBinding binding;
    private Long mProjectId;

    public AddTaskFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTaskBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.acceptTaskBtn.setOnClickListener(v -> {

            InfiniteDatabase db = InfiniteDatabase.getDatabase(getContext());
            Task task = new Task();
            task.setName(binding.nameTask.getText().toString());
            task.setDescription(binding.descriptionTask.getText().toString());
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            try {
                task.setDeadline(sdf.parse(binding.dateTask.getText().toString()));
            } catch (Exception e) {
                e.printStackTrace();
            }

            if(binding.spinnerTaskState.getSelectedItem().toString().equals("Por hacer")){
                task.setState(TaskState.TODO);
            } else if(binding.spinnerTaskState.getSelectedItem().toString().equals("En progreso")){
                task.setState(TaskState.DOING);
            } else if(binding.spinnerTaskState.getSelectedItem().toString().equals("Hechas")){
                task.setState(TaskState.DONE);
            }

            task.setPriority(Long.parseLong(binding.priorityTask.getText().toString()));

            task.setEffort(Long.parseLong(binding.spinnerTaskEffort.getSelectedItem().toString()));

            PersistenceUser persistenceUser = PersistenceUser.getInstance();
            task.setUserId(persistenceUser.getUserId());

            task.setProjectId(getArguments().getLong("project_id"));


            AppExecutors.getInstance().diskIO().execute(() -> {
                db.taskDAO().insert(task);
            });

            Navigation.findNavController(v).navigateUp();
        });

        binding.cancelTaskBtn.setOnClickListener(v -> {
            Navigation.findNavController(v).navigateUp();
        });
    }
}