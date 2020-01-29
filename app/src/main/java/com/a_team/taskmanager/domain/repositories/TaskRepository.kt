package com.a_team.taskmanager.domain.repositories

import android.arch.lifecycle.LiveData
import com.a_team.taskmanager.entity.Task

interface TaskRepository {
    fun getTasks(): LiveData<List<Task?>?>?
    fun getTasksByIds(ids: List<Long>): LiveData<List<Task?>?>?
    fun getTask(id: Long): LiveData<Task?>?
    fun updateTask(task: Task)
    fun insertTasks(tasks: List<Task>)
    fun deleteTasks(tasks: List<Task>)
}