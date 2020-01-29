package com.a_team.taskmanager.data.db.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.a_team.taskmanager.entity.Task

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(task: Task?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTasks(tasks: List<Task?>)

    @Delete
    fun deleteTasks(tasks: List<Task?>)

    @Update
    fun updateTasks(vararg tasks: Task?)

    @Query("select * from Task")
    fun getAllTasks(): LiveData<List<Task?>?>?

    @Query("select * from Task where id = :id")
    fun getTask(id: Long): LiveData<Task?>?

    @Query("select * from Task where id IN (:ids)")
    fun getTasksByIds(ids: List<Long>): LiveData<List<Task?>?>?
}