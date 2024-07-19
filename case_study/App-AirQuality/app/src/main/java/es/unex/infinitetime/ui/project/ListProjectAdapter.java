package es.unex.infinitetime.ui.project;


import static java.security.AccessController.getContext;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import es.unex.infinitetime.AppExecutors;
import es.unex.infinitetime.R;
//import es.unex.infinitetime.databinding.FragmentItemProjectBinding;
import es.unex.infinitetime.databinding.FragmentItemProjectBinding;
import es.unex.infinitetime.persistence.InfiniteDatabase;
import es.unex.infinitetime.persistence.Project;
import es.unex.infinitetime.persistence.User;

public class ListProjectAdapter extends RecyclerView.Adapter<ListProjectAdapter.ViewHolder> {
    private final OnItemClickListener listener;

    private List<Project> mItems = new ArrayList<>();
    Context mContext;
    private FragmentItemProjectBinding binding;

    public interface OnItemClickListener {
        void onItemClick(Project item);
    }



    public ListProjectAdapter(Context context, OnItemClickListener listener) {
        mContext = context;
        this.listener = listener;

    }


    @NonNull
    @Override
    public ListProjectAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
        binding = FragmentItemProjectBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new ViewHolder(mContext,binding.getRoot());
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(mItems.get(position),listener);

    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void add(Project item) {

        mItems.add(item);
        notifyDataSetChanged();

    }

    public void clear(){

        mItems.clear();
        notifyDataSetChanged();

    }

    public void load(List<Project> items){

        mItems.clear();
        mItems = items;
        notifyDataSetChanged();

    }

    public Object getItem(int pos) { return mItems.get(pos); }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private Context mContext;
        private FragmentItemProjectBinding binding;

        public ViewHolder(Context context, View itemView) {
            super(itemView);

            mContext = context;
            binding = FragmentItemProjectBinding.bind(itemView);

        }

        public void bind(final Project project, final OnItemClickListener listener) {

            binding.tvItemProjectName.setText(project.getName());

            InfiniteDatabase db = InfiniteDatabase.getDatabase(mContext);
            itemView.setOnClickListener(v -> listener.onItemClick(project));

            FloatingActionButton edit = binding.editProjectItemBtn;
            FloatingActionButton delete = binding.deleteProjectBtn;
            FloatingActionButton share = binding.shareProjectItemBtn;


            edit.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putLong("project_id", project.getId());
                Navigation.findNavController(v).navigate(R.id.action_projects_to_editProjectFragment, bundle);
            });

            delete.setOnClickListener(v -> {

                if(project.getName()!=null){
                    AppExecutors.getInstance().diskIO().execute(() -> {
                        db.projectDAO().delete(project);
                        Snackbar.make(v, "Proyecto "+ project.getName()+" borrado", Snackbar.LENGTH_SHORT).show();
                    });
                }
            });

            share.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putLong("project_id", project.getId());
                Navigation.findNavController(v).navigate(R.id.action_projects_to_sharedFragment, bundle);
            });

        }
    }

}
