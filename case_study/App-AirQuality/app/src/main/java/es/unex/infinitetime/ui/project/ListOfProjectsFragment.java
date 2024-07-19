package es.unex.infinitetime.ui.project;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import es.unex.infinitetime.AppExecutors;
import es.unex.infinitetime.R;
import es.unex.infinitetime.databinding.FragmentListOfProjectsBinding;
import es.unex.infinitetime.datosEjemplo.ExampleData;
import es.unex.infinitetime.persistence.InfiniteDatabase;
import es.unex.infinitetime.persistence.Project;
import es.unex.infinitetime.persistence.User;
import es.unex.infinitetime.ui.login.PersistenceUser;
import es.unex.infinitetime.ui.tabs.ListTasksFragment;


public class ListOfProjectsFragment extends Fragment {

    private static final String ARG_PARAM1 = "user_id";

    private long user_id;

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private ListProjectAdapter mAdapter;
    private FloatingActionButton addNewProject;

    private FragmentListOfProjectsBinding binding;

    public ListOfProjectsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        binding = FragmentListOfProjectsBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadItems();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = binding.recyclerView;
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new ListProjectAdapter(getActivity().getApplicationContext(), item -> {
            Bundle bundle = new Bundle();
            bundle.putLong(ListTasksFragment.ARG_PARAM1, item.getId());
            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_projects_to_listTasksFragment, bundle);
        });

        addNewProject = binding.AddProject;
        addNewProject.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_projects_to_addProjectFragment);
        });

        user_id = PersistenceUser.getInstance().getUserId();

        mRecyclerView.setAdapter(mAdapter);
    }

    private void loadItems() {
        List<Project> mItems = new ArrayList<>();

        AppExecutors.getInstance().diskIO().execute(() -> {

            mItems.addAll(InfiniteDatabase.getDatabase(getActivity().getApplicationContext()).userDAO().getProjectsCreated(user_id));
            mItems.addAll(InfiniteDatabase.getDatabase(getActivity().getApplicationContext()).userDAO().getProjectsShared(user_id));

            AppExecutors.getInstance().mainThread().execute(() -> {
                mAdapter.load(mItems);
            });
        });

    }
}