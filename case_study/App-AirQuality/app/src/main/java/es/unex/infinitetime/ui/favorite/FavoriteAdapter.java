package es.unex.infinitetime.ui.favorite;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import es.unex.infinitetime.AppExecutors;
import es.unex.infinitetime.R;
import es.unex.infinitetime.databinding.FragmentItemTaskBinding;
import es.unex.infinitetime.persistence.InfiniteDatabase;
import es.unex.infinitetime.persistence.Task;
import es.unex.infinitetime.ui.login.PersistenceUser;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder> {
    private final OnItemClickListener listener;

    private List<Task> mItems = new ArrayList<>();
    Context mContext;

    private FragmentItemTaskBinding binding;


    public interface OnItemClickListener {
        void onItemClick(Task item);
    }


    public FavoriteAdapter(Context context, OnItemClickListener listener) {
        mContext = context;
        this.listener = listener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        binding = FragmentItemTaskBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
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
        Log.d("Depurando", "load Adapter stats");

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

        public void bind(final Task task, OnItemClickListener listener) {

            binding.nameTaskItem.setText(task.getName());

            InfiniteDatabase db = InfiniteDatabase.getDatabase(mContext);
            PersistenceUser user = PersistenceUser.getInstance();

            itemView.setOnClickListener(v -> listener.onItemClick(task));

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
                                db.taskDAO().addFavorite(user.getUserId(), task.getId());
                            });

                        }
                        else {
                            AppExecutors.getInstance().diskIO().execute(()->{
                                db.taskDAO().removeFavorite(user.getUserId(), task.getId());
                            });
                        }
                    });
                });
            });

            binding.deleteButtonTask.setOnClickListener(v -> {
                AppExecutors.getInstance().diskIO().execute(() -> {
                    db.taskDAO().delete(task);
                    Snackbar.make(v, "Tarea -"+ task.getName()+"- borrada", Snackbar.LENGTH_SHORT).show();
                });
            });

        }
    }
}
