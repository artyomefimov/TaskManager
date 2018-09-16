package com.a_team.taskmanager.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.a_team.taskmanager.model.Task;

import java.util.List;

@Dao
public interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Task task);

    @Query("delete from Task")
    void deleteAll();

    @Delete
    int deleteTasks(Task... tasks);

    @Update
    int updateTasks(Task... tasks);

    @Query("select * from Task")
    LiveData<List<Task>> getAllTasks();

    @Query("select * from Task where id = :id")
    LiveData<Task> getTask(long id);

    @Query("select * from Task where id = :id")
    List<Task> isTaskExists(long id);
}
