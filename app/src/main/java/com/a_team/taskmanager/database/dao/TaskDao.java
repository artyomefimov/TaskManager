package com.a_team.taskmanager.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.a_team.taskmanager.model.Task;

import java.util.List;

@Dao
public interface TaskDao {

    @Insert
    void insert(Task task);

    @Query("delete from task")
    void deleteAll();

    @Delete
    int deleteTasks(Task... tasks);

    @Update
    int update(Task... tasks);

    @Query("select * from task")
    LiveData<List<Task>> getAllTasks();
}
