package com.a_team.taskmanager.data.repositories

import android.arch.lifecycle.LiveData
import com.a_team.taskmanager.data.db.TaskManagerDatabase
import com.a_team.taskmanager.domain.repositories.TaskRepository
import com.a_team.taskmanager.entity.Task

// todo di
class TaskRepositoryImpl(private val database: TaskManagerDatabase): TaskRepository {
    override fun getTasks(): LiveData<List<Task?>?>? =
        database.taskDao().getAllTasks()

    override fun getTasksByIds(ids: List<Long>): LiveData<List<Task?>?>? =
        database.taskDao().getTasksByIds(ids)

    override fun getTask(id: Long): LiveData<Task?>? =
        database.taskDao().getTask(id)

    override fun updateTask(task: Task) =
        database.taskDao().insert(task)

    override fun insertTasks(tasks: List<Task>) =
        database.taskDao().insertTasks(tasks)

    override fun deleteTasks(tasks: List<Task>) =
        database.taskDao().deleteTasks(tasks)
}