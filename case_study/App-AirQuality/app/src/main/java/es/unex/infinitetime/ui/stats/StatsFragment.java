package es.unex.infinitetime.ui.stats;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;


import java.util.Date;

import es.unex.infinitetime.AppExecutors;
import es.unex.infinitetime.R;
import es.unex.infinitetime.databinding.FragmentStatsBinding;
import es.unex.infinitetime.persistence.InfiniteDatabase;
import es.unex.infinitetime.persistence.Project;
import es.unex.infinitetime.persistence.Task;
import es.unex.infinitetime.persistence.TaskState;
import es.unex.infinitetime.persistence.User;
import es.unex.infinitetime.ui.login.PersistenceUser;


public class StatsFragment extends Fragment {

    private TextView todoTv, doingTv, doneTv;
    private PieChart pieChart;

    private FragmentStatsBinding binding;

    public StatsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        binding = FragmentStatsBinding.inflate(inflater, container, false);

        todoTv = binding.TareasPorHacer;
        doingTv = binding.TareasEmpezadas;
        doneTv = binding.TareasHechas;

        pieChart = binding.piechart;
        setData();


        return binding.getRoot();
    }


    private void setData()
    {



        PersistenceUser persistenceUser = PersistenceUser.getInstance();
        InfiniteDatabase db = InfiniteDatabase.getDatabase(getContext());

        AppExecutors.getInstance().diskIO().execute(() ->{
            int numTodo= db.taskDAO().getTasksNum(PersistenceUser.getInstance().getUserId(), TaskState.TODO.ordinal());
            int numDoing= db.taskDAO().getTasksNum(PersistenceUser.getInstance().getUserId(), TaskState.DOING.ordinal());
            int numDone= db.taskDAO().getTasksNum(PersistenceUser.getInstance().getUserId(), TaskState.DONE.ordinal());

            Log.d("Depurando", "numTodo:" + numTodo);
            Log.d("Depurando", "numDoing:" + numDoing);
            Log.d("Depurando", "numDone:" + numDone);


            todoTv.setText(Integer.toString(numTodo));
            doingTv.setText(Integer.toString(numDoing));
            doneTv.setText(Integer.toString(numDone));

            pieChart.addPieSlice(
                    new PieModel(
                            "Tareas por hacer",
                            Integer.parseInt(todoTv.getText().toString()),
                            Color.parseColor("#FFA726")));
            pieChart.addPieSlice(
                    new PieModel(
                            "Tareas en progreso",
                            Integer.parseInt(doingTv.getText().toString()),
                            Color.parseColor("#66BB6A")));
            pieChart.addPieSlice(
                    new PieModel(
                            "Tareas terminadas",
                            Integer.parseInt(doneTv.getText().toString()),
                            Color.parseColor("#EF5350")));

        });

        pieChart.startAnimation();
    }


    @Override
    public void onResume() {
        super.onResume();
    }






}