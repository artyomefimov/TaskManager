package com.a_team.taskmanager.ui.singletask.managers;

import android.app.Activity;

import com.a_team.taskmanager.R;
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
            mPhotoManager.removePhotoIfNecessary(activity);
        }
        FillingTitleHelper.fillTitleIfEmpty(mTask);
        mViewModel.updateOrInsertTask(mTask);
        ToastMaker.show(activity, R.string.saving_changes);
    }

    public void deleteTask(Activity activity) {
        mViewModel.deleteTask(mTask);
        ToastMaker.show(activity, R.string.deleting_task);
    }
}
