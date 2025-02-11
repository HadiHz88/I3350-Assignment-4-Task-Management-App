package com.tasksapplication.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.tasksapplication.R;
import com.tasksapplication.databinding.ActivityPreviewBinding;
import com.tasksapplication.db.Task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Activity for previewing a task.
 */
public class PreviewActivity extends AppCompatActivity {

    private ActivityPreviewBinding binding;
    private int currentTaskId;
    private Task currentTask;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private final ActivityResultLauncher<Intent> editTaskLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    int updatedTaskId = result.getData().getIntExtra("updatedTaskId", -1);
                    if (updatedTaskId != -1) {
                        fetchAndDisplay();
                    }
                }
            }
    );

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPreviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.previewToolbar);

        currentTaskId = getIntent().getIntExtra("taskId", -1);

        if (currentTaskId != -1) {
            fetchAndDisplay();
        }
    }

    /**
     * Initializes the contents of the Activity's standard options menu.
     *
     * @param menu The options menu in which you place your items.
     * @return boolean You must return true for the menu to be displayed; if you return false it will not be shown.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.preview_menu, menu);
        return true;
    }

    /**
     * This hook is called whenever an item in your options menu is selected.
     *
     * @param item The menu item that was selected.
     * @return boolean Return false to allow normal menu processing to proceed, true to consume it here.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_edit) {
            Intent intent = new Intent(PreviewActivity.this, AddEditActivity.class);
            intent.putExtra("taskId", currentTaskId);
            editTaskLauncher.launch(intent);
        } else if (id == R.id.action_delete) {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Confirmation")
                    .setMessage("Are you sure you want to delete this task?")
                    .setPositiveButton("Delete", (dialog, which) -> deleteTheTask())
                    .setNegativeButton("Cancel", null)
                    .show();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Deletes the current task from the database.
     */
    private void deleteTheTask() {
        executorService.execute(() -> {
            MainActivity.db.taskDao().deleteTask(currentTask);
            runOnUiThread(this::finish);
        });
    }

    /**
     * Fetches the current task from the database and displays it.
     */
    private void fetchAndDisplay() {
        executorService.execute(() -> {
            currentTask = MainActivity.db.taskDao().getTaskById(currentTaskId);
            runOnUiThread(() -> {
                binding.previewTitleTv.setText(currentTask.getTitle());
                binding.previewDescTv.setText(currentTask.getDesc());
            });
        });
    }
}