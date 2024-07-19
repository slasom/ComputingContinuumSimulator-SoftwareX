package es.unex.infinitetime.ui.shared;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import es.unex.infinitetime.AppExecutors;
import es.unex.infinitetime.databinding.FragmentItemSharedBinding;
import es.unex.infinitetime.databinding.FragmentItemTaskBinding;
import es.unex.infinitetime.persistence.InfiniteDatabase;
import es.unex.infinitetime.persistence.Project;
import es.unex.infinitetime.persistence.SharedProject;
import es.unex.infinitetime.persistence.User;

public class SharedAdapter extends RecyclerView.Adapter<SharedAdapter.ViewHolder> {
    private List<User> mItems = new ArrayList<>();
    Context mContext;

    private FragmentItemSharedBinding binding;
    private long projectId;


    public interface OnItemClickListener {
        void onItemClick(User item);
    }


    public SharedAdapter(Context context, long projectId) {
        mContext = context;
        this.projectId = projectId;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        binding = FragmentItemSharedBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(mContext, binding.getRoot(), projectId);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(mItems.get(position));

    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void add(User item) {

        mItems.add(item);
        notifyDataSetChanged();

    }

    public void clear() {

        mItems.clear();
        notifyDataSetChanged();

    }

    public void load(List<User> items) {

        mItems.clear();
        mItems = items;
        notifyDataSetChanged();

    }

    public Object getItem(int pos) {
        return mItems.get(pos);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private Context mContext;

        private FragmentItemSharedBinding binding;

        private long projectId;

        public ViewHolder(Context context, View itemView, long projectId) {
            super(itemView);

            mContext = context;
            binding = FragmentItemSharedBinding.bind(itemView);
            this.projectId = projectId;

        }

        public void bind(final User item) {

            binding.textUsernameShared.setText(item.getUsername());
            binding.sharedCheckBox.setChecked(false);

            AppExecutors.getInstance().diskIO().execute(() -> {
                SharedProject sharedProject = InfiniteDatabase.getDatabase(mContext).projectDAO().getSharedProject(projectId, item.getId());
                AppExecutors.getInstance().mainThread().execute(() -> {
                    if (sharedProject != null) {
                        binding.sharedCheckBox.setChecked(true);
                    }
                });
            });

            binding.sharedCheckBox.setOnCheckedChangeListener((v, isChecked) -> {
                if (isChecked) {
                    AppExecutors.getInstance().diskIO().execute(() -> {
                        Project project = new Project();
                        project.setId(projectId);
                        project.setDescription("Prueba");
                        project.setName("Prueba");
                        project.setUserId(item.getId());
                        if(InfiniteDatabase.getDatabase(mContext).projectDAO().getProject(projectId) == null) {
                            InfiniteDatabase.getDatabase(mContext).projectDAO().insert(project);
                        }
                        // Borrar las líneas anteriores al hacer la integración
                        if(InfiniteDatabase.getDatabase(mContext).projectDAO().getSharedProject(projectId, item.getId()) == null) {
                            InfiniteDatabase.getDatabase(mContext).projectDAO().shareProject(projectId, item.getId());
                        }
                    });
                }
                else {
                    AppExecutors.getInstance().diskIO().execute(() -> {
                        InfiniteDatabase.getDatabase(mContext).projectDAO().stopSharingProject(projectId, item.getId());
                    });
                }
            });


        }

    }
}
