package com.a_team.taskmanager.ui.singletask.managers;

import com.a_team.taskmanager.controller.TaskViewModel;
import com.a_team.taskmanager.entity.Task;

public class TaskOperationsManager {
    private static TaskOperationsManager ourInstance;

    private TaskViewModel mViewModel;
    private Task mTask;

    public static TaskOperationsManager getInstance(TaskViewModel viewModel) {
        if (ourInstance == null) {
            ourInstance = new TaskOperationsManager(viewModel);
        }
        return ourInstance;
    }

    private TaskOperationsManager(TaskViewModel viewModel) {
        mViewModel = viewModel;
    }

    public void updateTask() {
        mViewModel.updateOrInsertTask(mTask);
    }

    public void deleteTask() {
        mViewModel.deleteTask(mTask);
    }

    public void setTask(Task task) {
        mTask = task;
    }
}
