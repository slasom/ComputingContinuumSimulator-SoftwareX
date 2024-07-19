package es.unex.infinitetime.ui.tabs;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import es.unex.infinitetime.ui.project.ListProjectAdapter;
import es.unex.infinitetime.R;
import es.unex.infinitetime.persistence.TaskState;
import es.unex.infinitetime.ui.stats.StatsFragment;
import es.unex.infinitetime.databinding.FragmentListTasksBinding;


public class ListTasksFragment extends Fragment {

    private static final String Tag = "ListTasksFragment";

    public static final String ARG_PARAM1 = "project_id";

    private long projectId;

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private FragmentListTasksBinding binding;

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private ListProjectAdapter mAdapter;

    private FloatingActionButton addNewTask;


    public ListTasksFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(Tag, "onCreate");
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            projectId = getArguments().getLong(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(Tag, "onCreateView");


        binding = FragmentListTasksBinding.inflate(inflater, container, false);

        tabLayout = binding.tabLayout;
        viewPager = binding.viewPager;

        TabsTasksListAdapter tabsTasksListAdapter = new TabsTasksListAdapter(this);
        viewPager.setAdapter(tabsTasksListAdapter);

        String namesTabs[] = {"Sin empezar", "En progreso", "Hechas"};

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(namesTabs[position])
        ).attach();

        Bundle bundle = getArguments();
        addNewTask= binding.AddProject;
        addNewTask.setOnClickListener(view -> {
            Navigation.findNavController(view).navigate(R.id.action_listTasksFragment_to_addTaskFragment, bundle);
            Log.d("Depurando", "Se ha pulsado el botón de añadir tarea");
        });


        return binding.getRoot();
    }
}