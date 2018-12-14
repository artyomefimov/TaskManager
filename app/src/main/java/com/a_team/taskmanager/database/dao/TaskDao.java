package com.a_team.taskmanager.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.Update;

import com.a_team.taskmanager.entity.Task;

import java.util.List;

@Dao
public interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Task task);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTasks(Task... tasks);

    @Delete
    void deleteTasks(Task... tasks);

    @Update
    void updateTasks(Task... tasks);

    @Query("select * from Task")
    LiveData<List<Task>> getAllTasks();

    @Query("select * from Task where id = :id")
    LiveData<Task> getTask(long id);

    @Query("select * from Task where id = :id")
    Task getTaskObject(long id);

    @Query("select * from Task where id IN (:ids)")
    LiveData<List<Task>> getTasksByIds(Long... ids);
}