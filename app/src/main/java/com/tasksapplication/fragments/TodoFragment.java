package com.tasksapplication.fragments;

    import android.os.Bundle;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;

    import androidx.annotation.NonNull;
    import androidx.annotation.Nullable;
    import androidx.fragment.app.Fragment;
    import androidx.lifecycle.Observer;
    import androidx.recyclerview.widget.RecyclerView;

    import com.tasksapplication.activities.MainActivity;
    import com.tasksapplication.adapters.TaskRecAdapter;
    import com.tasksapplication.databinding.FragmentTodoBinding;
    import com.tasksapplication.db.Task;

    import java.util.List;

    /**
     * Fragment for displaying tasks to be done.
     */
    public class TodoFragment extends Fragment {

        private FragmentTodoBinding binding;
        private RecyclerView todoRecView;
        private TaskRecAdapter adapter;

        /**
         * Required empty public constructor.
         */
        public TodoFragment() {
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            binding = FragmentTodoBinding.inflate(inflater, container, false);
            return binding.getRoot();
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            todoRecView = binding.todoRecView;

            MainActivity.db.taskDao().getTasks(false).observe(getViewLifecycleOwner(), new Observer<List<Task>>() {
                @Override
                public void onChanged(List<Task> taskList) {
                    adapter = new TaskRecAdapter(taskList, (MainActivity) getContext());
                    todoRecView.setAdapter(adapter);
                }
            });
        }
    }