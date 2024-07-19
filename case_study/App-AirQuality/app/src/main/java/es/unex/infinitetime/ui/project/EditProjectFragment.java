package es.unex.infinitetime.ui.project;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import java.text.SimpleDateFormat;
import java.util.Date;

import es.unex.infinitetime.AppExecutors;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import es.unex.infinitetime.persistence.InfiniteDatabase;
import es.unex.infinitetime.persistence.Project;
import es.unex.infinitetime.persistence.Task;
import es.unex.infinitetime.persistence.TaskState;
import es.unex.infinitetime.persistence.User;


import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import es.unex.infinitetime.databinding.FragmentEditProjectBinding;
import es.unex.infinitetime.R;
import es.unex.infinitetime.ui.login.PersistenceUser;


public class EditProjectFragment extends Fragment {

    private static final String ARG_PARAM1 = "project_id";

    private long projectId;

    private FragmentEditProjectBinding binding;




    FloatingActionButton confirmEditCheck;
    FloatingActionButton cancelEditCheck;

    public EditProjectFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentEditProjectBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        projectId = getArguments().getLong(ARG_PARAM1);

        confirmEditCheck = binding.checkEditBtn;
        cancelEditCheck = binding.cancelEditBtn;

        AppExecutors.getInstance().diskIO().execute(() -> {
            Project project = InfiniteDatabase.getDatabase(getContext()).projectDAO().getProject(projectId);

            AppExecutors.getInstance().mainThread().execute(() -> {
                binding.ProjectNameEdit.setText(project.getName());
                binding.ProjectDescriptionEdit.setText(project.getDescription());
            });
        });
        confirmEditCheck.setOnClickListener(v -> {
            String projectName= binding.ProjectNameEdit.getText().toString();
            String projectDescription=binding.ProjectDescriptionEdit.getText().toString();
            Project updateProject=new Project();
            updateProject.setId(projectId);
            updateProject.setUserId(PersistenceUser.getInstance().getUserId());
            updateProject.setName(projectName);
            updateProject.setDescription(projectDescription);
            AppExecutors.getInstance().diskIO().execute(() -> {

                if(InfiniteDatabase.getDatabase(getContext()).projectDAO().getProject(projectId) != null){
                    InfiniteDatabase.getDatabase(getContext()).projectDAO().update(updateProject);
                    Snackbar.make(view, "Proyecto actualizado", Snackbar.LENGTH_LONG).show();
                }
            });
            Navigation.findNavController(v).navigate(R.id.action_editProjectFragment_to_projects);

        });

        cancelEditCheck.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_editProjectFragment_to_projects);
        });
    }
}
