package com.tasksapplication.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.room.Room;

import com.tasksapplication.R;
import com.tasksapplication.databinding.ActivityMainBinding;
import com.tasksapplication.db.AppDatabase;
import com.tasksapplication.db.Task;

/**
 * Main activity that sets up the navigation and database.
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Static reference to the AppDatabase.
     */
    public static AppDatabase db;
    private ActivityMainBinding binding;

    /**
     * Called when the activity is first created.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the database
        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "task_db").build();

        // Inflate the layout
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set up navigation
        setupNavigation();

        // Set up the floating action button to start AddEditActivity
        binding.fab.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Sets up the navigation components.
     */
    private void setupNavigation() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);

        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();

            AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.todoFragment,
                    R.id.doneFragment
            ).build();

            NavigationUI.setupWithNavController(binding.toolbar, navController, appBarConfiguration);
            NavigationUI.setupWithNavController(binding.bottomNav, navController);
        }
    }

    /**
     * Starts the PreviewActivity to preview the given task.
     * @param currentTask The task to preview.
     */
    public void previewTask(Task currentTask) {
        Intent intent = new Intent(MainActivity.this, PreviewActivity.class);
        intent.putExtra("taskId", currentTask.getTid());
        startActivity(intent);
    }
}