package com.a_team.taskmanager.data.db

import android.arch.lifecycle.MutableLiveData
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.a_team.taskmanager.data.db.dao.TaskDao
import com.a_team.taskmanager.entity.Task

@Database(entities = [Task::class], version = 1)
abstract class TaskManagerDatabase : RoomDatabase() {
    private val isDatabaseCreated = MutableLiveData<Boolean>()

    private fun updateIsDatabaseCreated(context: Context) {
        if (context.getDatabasePath(DB_NAME).exists()) {
            setIsDatabaseCreated()
        }
    }

    private fun setIsDatabaseCreated() {
        isDatabaseCreated.postValue(true)
    }

    abstract fun taskDao(): TaskDao

    companion object {
        private const val DB_NAME = "task_manager_db"
        private var instance: TaskManagerDatabase? = null

        fun getDatabase(context: Context): TaskManagerDatabase? {
            if (instance == null) {
                synchronized(TaskManagerDatabase::class.java) {
                    if (instance == null) {
                        instance = Room.databaseBuilder(
                            context.applicationContext,
                            TaskManagerDatabase::class.java,
                            DB_NAME
                        )
                            .build()
                        instance!!.updateIsDatabaseCreated(context.applicationContext)
                    }
                }
            }
            return instance
        }
    }
}