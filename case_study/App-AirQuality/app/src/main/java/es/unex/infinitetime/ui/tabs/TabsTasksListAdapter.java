package es.unex.infinitetime.ui.tabs;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

import es.unex.infinitetime.persistence.TaskState;

public class TabsTasksListAdapter extends FragmentStateAdapter {

    Fragment fragment;

    public TabsTasksListAdapter(@NonNull Fragment fragment) {
        super(fragment);
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        long projectId = fragment.getArguments().getLong(ListTasksFragment.ARG_PARAM1);
        return ListTasksStateFragment.newInstance(TaskState.values()[position], projectId);
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
