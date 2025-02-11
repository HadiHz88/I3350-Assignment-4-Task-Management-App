package com.tasksapplication.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.tasksapplication.R;
import com.tasksapplication.activities.MainActivity;
import com.tasksapplication.db.Task;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * RecyclerView Adapter for displaying tasks.
 */
public class TaskRecAdapter extends RecyclerView.Adapter<TaskRecAdapter.TaskViewHolder> {

    private List<Task> taskList;
    private final MainActivity activity;

    /**
     * Constructor for TaskRecAdapter.
     *
     * @param taskList List of tasks to display.
     * @param activity The activity context.
     */
    public TaskRecAdapter(List<Task> taskList, MainActivity activity) {
        this.taskList = taskList;
        this.activity = activity;
    }

    /**
     * ViewHolder class for task items.
     */
    public static class TaskViewHolder extends RecyclerView.ViewHolder {

        public TextView titleTv, descTv;
        public SwitchMaterial sw;

        /**
         * Constructor for TaskViewHolder.
         *
         * @param itemView The view of the task item.
         */
        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTv = itemView.findViewById(R.id.titleTv);
            descTv = itemView.findViewById(R.id.descTv);
            sw = itemView.findViewById(R.id.switchId);
        }
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_row, parent, false);
        return new TaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task currentTask = taskList.get(position);

        holder.titleTv.setText(currentTask.getTitle());

        // Trim the description if it's longer than 20 characters
        String description = currentTask.getDesc();
        if (description.length() > 20) {
            holder.descTv.setText(description.substring(0, 20) + "...");
        } else {
            holder.descTv.setText(description);
        }

        holder.sw.setChecked(currentTask.isDone());

        holder.sw.setOnCheckedChangeListener((buttonView, isChecked) -> {
            currentTask.setDone(isChecked);
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(() -> MainActivity.db.taskDao().updateTask(currentTask));
        });

        holder.titleTv.setOnClickListener(v -> activity.previewTask(currentTask));
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }
}