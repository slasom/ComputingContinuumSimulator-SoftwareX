package es.unex.infinitetime.ui.tabs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import es.unex.infinitetime.AppExecutors;
import es.unex.infinitetime.R;
import es.unex.infinitetime.databinding.FragmentItemTaskBinding;
import es.unex.infinitetime.persistence.InfiniteDatabase;
import es.unex.infinitetime.persistence.Task;
import es.unex.infinitetime.persistence.TaskState;
import es.unex.infinitetime.ui.login.PersistenceUser;

public class ListTasksStateAdapter extends RecyclerView.Adapter<ListTasksStateAdapter.ViewHolder> {

    private List<Task> mItems = new ArrayList<>();
    Context mContext;

    public interface OnItemClickListener {
        void onItemClick(Task item);
    }

    private final ListTasksStateAdapter.OnItemClickListener listener;

    public ListTasksStateAdapter(Context context, ListTasksStateAdapter.OnItemClickListener listener) {
        mContext = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ListTasksStateAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item_task, parent, false);

        return new ListTasksStateAdapter.ViewHolder(mContext,v);
    }

    @Override
    public void onBindViewHolder(ListTasksStateAdapter.ViewHolder holder, int position) {
        holder.bind(mItems.get(position),listener);

    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void add(Task item) {

        mItems.add(item);
        notifyDataSetChanged();

    }

    public void clear(){

        mItems.clear();
        notifyDataSetChanged();

    }

    public void load(List<Task> items){

        mItems.clear();
        mItems = items;
        notifyDataSetChanged();

    }

    public Object getItem(int pos) { return mItems.get(pos); }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private Context mContext;

        private FragmentItemTaskBinding binding;


        public ViewHolder(Context context, View itemView) {
            super(itemView);

            mContext = context;
            binding = FragmentItemTaskBinding.bind(itemView);

        }

        public void bind(final Task task, final ListTasksStateAdapter.OnItemClickListener listener) {

            binding.nameTaskItem.setText(task.getName());
            InfiniteDatabase db = InfiniteDatabase.getDatabase(mContext);
            itemView.setOnClickListener(v -> listener.onItemClick(task));

            Button delete = binding.deleteButtonTask;


            delete.setOnClickListener(v -> {
                AppExecutors.getInstance().diskIO().execute(() -> {
                    db.taskDAO().delete(task);
                    Snackbar.make(v, "Tarea "+ task.getName()+"- borrada", Snackbar.LENGTH_SHORT).show();
                });
            });



            AppExecutors.getInstance().diskIO().execute(() -> {
                boolean isFavorite;
                if(db.userDAO().getFavorite(PersistenceUser.getInstance().getUserId(), task.getId()) == null){
                    isFavorite = false;
                }
                else{
                    isFavorite = true;
                }

                AppExecutors.getInstance().mainThread().execute(() -> {
                    binding.checkboxFavorite.setChecked(isFavorite);
                    binding.checkboxFavorite.setOnCheckedChangeListener((compoundButton, isChecked) -> {
                        if(isChecked){
                            AppExecutors.getInstance().diskIO().execute(()->{
                                db.taskDAO().addFavorite(PersistenceUser.getInstance().getUserId(), task.getId());
                            });

                        }
                        else {
                            AppExecutors.getInstance().diskIO().execute(()->{
                                db.taskDAO().removeFavorite(PersistenceUser.getInstance().getUserId(), task.getId());
                            });
                        }
                    });
                });
            });

        }
    }


}
