package es.unex.infinitetime.ui.task;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.Snackbar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import es.unex.infinitetime.AppExecutors;
import es.unex.infinitetime.R;
import es.unex.infinitetime.databinding.FragmentItemTaskBinding;
import es.unex.infinitetime.databinding.FragmentTaskBinding;
import es.unex.infinitetime.persistence.InfiniteDatabase;
import es.unex.infinitetime.persistence.Project;
import es.unex.infinitetime.persistence.Task;
import es.unex.infinitetime.persistence.TaskState;
import es.unex.infinitetime.persistence.User;
import es.unex.infinitetime.ui.login.PersistenceUser;

public class EditTaskFragment extends Fragment {

    private static final String ARG_PARAM1 = "task_id";

    private long taskId;

    private FragmentTaskBinding binding;

    public EditTaskFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentTaskBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    private int getSpinnerPosition(long effort){
        for(int i = 0; i < binding.spinnerTaskEffort.getAdapter().getCount(); i++){
            if(binding.spinnerTaskEffort.getAdapter().getItem(i).equals(String.valueOf(effort))){
                return i;
            }
        }
        return -1;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        taskId = getArguments().getLong(ARG_PARAM1);;

        AppExecutors.getInstance().diskIO().execute(() -> {
            Task task = InfiniteDatabase.getDatabase(getContext()).taskDAO().getTask(taskId);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

            AppExecutors.getInstance().mainThread().execute(() -> {
                binding.nameTask.setText(task.getName());
                binding.descriptionTask.setText(task.getDescription());
                binding.spinnerTaskEffort.setSelection(getSpinnerPosition(task.getEffort()));
                binding.priorityTask.setText(String.valueOf(task.getPriority()));
                binding.spinnerTaskState.setSelection(task.getState().ordinal());


                try {
                    binding.dateTask.setText(sdf.format(task.getDeadline()));

                }catch (Exception e){
                    e.printStackTrace();
                    Snackbar.make(binding.getRoot(), "Fecha introducida anteriormente no valida", Snackbar.LENGTH_LONG).show();
                }


                binding.cancelTaskBtn.setOnClickListener(v -> {
                    Navigation.findNavController(v).navigateUp();
                });
                binding.acceptTaskBtn.setOnClickListener(v -> {
                    task.setId(task.getId());
                    task.setName(binding.nameTask.getText().toString());
                    task.setDescription(binding.descriptionTask.getText().toString());
                    task.setEffort(Long.parseLong(binding.spinnerTaskEffort.getSelectedItem().toString()));
                    task.setPriority(Long.parseLong(binding.priorityTask.getText().toString()));
                    if(binding.spinnerTaskState.getSelectedItem().toString().equals("Por hacer")){
                        task.setState(TaskState.TODO);
                    } else if(binding.spinnerTaskState.getSelectedItem().toString().equals("En progreso")){
                        task.setState(TaskState.DOING);
                    } else if(binding.spinnerTaskState.getSelectedItem().toString().equals("Hechas")){
                        task.setState(TaskState.DONE);
                    }
                    String date= binding.dateTask.getText().toString();
                    Date date1 = null;

                    try {
                        date1 = new SimpleDateFormat("dd/MM/yyyy").parse(date);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Snackbar.make(v, "Error al parsear la fecha. El formato debe ser: dd/MM/yyyy", Snackbar.LENGTH_LONG).show();
                    }
                    task.setDeadline(date1);


                    AppExecutors.getInstance().diskIO().execute(()->{

                        InfiniteDatabase.getDatabase(getContext()).taskDAO().update(task);

                    });
                    Navigation.findNavController(v).navigateUp();
                });


            });

        });

    }
}