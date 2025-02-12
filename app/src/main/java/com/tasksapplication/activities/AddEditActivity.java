package com.tasksapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.tasksapplication.databinding.ActivityAddEditBinding;
import com.tasksapplication.db.Task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Activity for adding or editing a task.
 */
public class AddEditActivity extends AppCompatActivity {

    private int currentTaskId;
    private Task currentTask;

    private ActivityAddEditBinding binding;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.addEditToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get the task ID from the intent
        currentTaskId = getIntent().getIntExtra("taskId", -1);

        if (currentTaskId != -1) {
            loadFromDatabase();
            // Set the button text to "Update Task" if the task ID is valid
            binding.btn.setText("Update Task");
            binding.addEditToolbar.setTitle("Edit Task");
        } else {
            binding.btn.setText("Add Task");
            binding.addEditToolbar.setTitle("Add Task");
        }

        binding.btn.setOnClickListener(v -> {
            saveTask();
        });
    }

    /**
     * Loads the task from the database if the task ID is valid.
     */
    private void loadFromDatabase() {
        executor.execute(() -> {
            currentTask = MainActivity.db.taskDao().getTaskById(currentTaskId);
            runOnUiThread(() -> {
                if (currentTask != null) {
                    binding.titleEt.setText(currentTask.getTitle());
                    binding.descEt.setText(currentTask.getDesc());
                }
            });
        });
    }

    /**
     * Saves the task to the database. If the task ID is valid, it updates the existing task.
     * Otherwise, it creates a new task.
     */
    private void saveTask() {
        String title = binding.titleEt.getText().toString().trim();
        String desc = binding.descEt.getText().toString().trim();

        if (title.isEmpty() || desc.isEmpty()) {
            return;
        }

        executor.execute(() -> {
            Intent resultIntent = new Intent();
            if (currentTaskId != -1 && currentTask != null) {
                currentTask.setTitle(title);
                currentTask.setDesc(desc);
                MainActivity.db.taskDao().updateTask(currentTask);
                resultIntent.putExtra("updatedTaskId", currentTaskId);
            } else {
                Task newTask = new Task(title, desc);
                MainActivity.db.taskDao().insertAll(newTask);
                resultIntent.putExtra("updatedTaskId", newTask.getTid());
            }

            setResult(RESULT_OK, resultIntent);
            runOnUiThread(this::finish);
        });
    }
}