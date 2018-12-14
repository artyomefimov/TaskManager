package com.a_team.taskmanager.ui.singletask.managers;

import android.app.Activity;

import com.a_team.taskmanager.R;
import com.a_team.taskmanager.utils.ToastMaker;

import static com.a_team.taskmanager.utils.ToastMaker.ToastPeriod.SHORT;

import com.a_team.taskmanager.ui.singletask.viewmodel.TaskViewModel;
import com.a_team.taskmanager.entity.Task;

public class TaskOperationsManager {

    private TaskViewModel mViewModel;
    private Task mTask;

    public TaskOperationsManager(TaskViewModel viewModel, Task task) {
        mViewModel = viewModel;
        mTask = task;
    }

    public void updateOrInsertTask(Activity activity) {
        FillingTitleHelper.fillTitleIfEmpty(mTask);
        mViewModel.updateOrInsertTask(mTask);
        ToastMaker.show(activity, R.string.saving_changes, SHORT);
    }

    public void deleteTask(Activity activity) {
        mViewModel.deleteTask(mTask);
        ToastMaker.show(activity, R.string.deleting_task, SHORT);
    }
}
