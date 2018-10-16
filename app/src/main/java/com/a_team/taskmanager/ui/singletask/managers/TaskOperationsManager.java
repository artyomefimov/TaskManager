package com.a_team.taskmanager.ui.singletask.managers;

import android.app.Activity;

import com.a_team.taskmanager.viewmodel.TaskViewModel;
import com.a_team.taskmanager.entity.Task;

public class TaskOperationsManager {

    private TaskViewModel mViewModel;
    private Task mTask;
    private PhotoManager mPhotoManager;

    public TaskOperationsManager(TaskViewModel viewModel, Task task, PhotoManager photoManager) {
        mViewModel = viewModel;
        mTask = task;
        mPhotoManager = photoManager;
    }

    public void updateTask(Activity activity) {
        if (mPhotoManager != null) {
            mPhotoManager.updatePhotoFileForTask();
            mPhotoManager.removePhotoIfNecessary(activity);
        }
        mViewModel.updateOrInsertTask(mTask);
    }

    public void deleteTask() {
        mViewModel.deleteTask(mTask);
    }
}
