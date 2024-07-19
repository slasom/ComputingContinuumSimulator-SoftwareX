package es.unex.infinitetime.ui.tabs;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import es.unex.infinitetime.AppExecutors;
import es.unex.infinitetime.R;
import es.unex.infinitetime.databinding.FragmentTaskBinding;
import es.unex.infinitetime.databinding.FragmentTasksBinding;
import es.unex.infinitetime.datosEjemplo.ExampleData;
import es.unex.infinitetime.persistence.InfiniteDatabase;
import es.unex.infinitetime.persistence.Project;
import es.unex.infinitetime.persistence.Task;
import es.unex.infinitetime.persistence.TaskState;
import es.unex.infinitetime.persistence.User;
import es.unex.infinitetime.ui.login.PersistenceUser;


public class ListTasksStateFragment extends Fragment {

    public static final String ARG_PARAM1 = "taskState";
    public static final String ARG_PARAM2 = "projectId";

    private TaskState state;
    private long projectId;

    ListTasksStateAdapter adapter;
    private FragmentTasksBinding binding;

    @Override
    public void onResume() {
        super.onResume();
        loadTasks();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        Bundle bundle = getArguments();

        state = TaskState.values()[bundle.getInt("taskState")];
        projectId = bundle.getLong("projectId");

        binding = FragmentTasksBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView mRecyclerView = binding.recyclerView;
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        adapter = new ListTasksStateAdapter(getActivity().getApplicationContext(), item -> {
            Bundle bundleTask = new Bundle();
            bundleTask.putLong("task_id", item.getId());
            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_listTasksFragment_to_editTaskFragment, bundleTask);
        });

        mRecyclerView.setAdapter(adapter);
    }

    public static ListTasksStateFragment newInstance(TaskState taskState, long projectId) {
        ListTasksStateFragment fragment = new ListTasksStateFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, taskState.ordinal());
        args.putLong(ARG_PARAM2, projectId);
        fragment.setArguments(args);
        return fragment;
    }

    private void loadTasks() {

        AppExecutors.getInstance().diskIO().execute(() -> {
            List<Task> tasks = InfiniteDatabase.getDatabase(getActivity().getApplicationContext()).projectDAO().getTasks(projectId);
            tasks.removeIf(task -> task.getState() != state);
            AppExecutors.getInstance().mainThread().execute(() -> {
                adapter.load(tasks);
            });
        });
    }
}