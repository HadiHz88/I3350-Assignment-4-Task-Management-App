package com.tasksapplication.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TaskDao {

    @Query("SELECT * FROM task WHERE is_done = :isDone")
    LiveData<List<Task>> getTasks(boolean isDone);

    @Query("SELECT * FROM task WHERE tid = :taskId LIMIT 1")
    Task getTaskById(int taskId);

    @Insert
    void insertAll(Task ... tasks);

    @Delete
    void deleteTask(Task task);

    @Update
    void updateTask(Task task);
}
