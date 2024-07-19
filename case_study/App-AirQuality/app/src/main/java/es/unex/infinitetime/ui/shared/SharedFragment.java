package es.unex.infinitetime.ui.shared;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import es.unex.infinitetime.AppExecutors;
import es.unex.infinitetime.R;
import es.unex.infinitetime.databinding.FragmentFavoriteBinding;
import es.unex.infinitetime.databinding.FragmentSharedBinding;
import es.unex.infinitetime.datosEjemplo.ExampleData;
import es.unex.infinitetime.persistence.InfiniteDatabase;
import es.unex.infinitetime.persistence.User;


public class SharedFragment extends Fragment {

    public static final String ARGS_PARAM1 = "project_id";

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private SharedAdapter mAdapter;

    private FragmentSharedBinding binding;

    private long projectId;

    public SharedFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d("Depurando", "onCreateView favorite");

        binding = FragmentSharedBinding.inflate(inflater, container, false);
        projectId = getArguments().getLong(ARGS_PARAM1);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView = binding.recyclerView;
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new SharedAdapter(getActivity().getApplicationContext(), projectId);

        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadItems();
    }


    private void loadItems() {

        AppExecutors.getInstance().diskIO().execute(() -> {
            List<User> users = InfiniteDatabase.getDatabase(getActivity().getApplicationContext()).userDAO().getAllUsers();
            AppExecutors.getInstance().mainThread().execute(() -> {
                mAdapter.load(users);
            });
        });
    }

}