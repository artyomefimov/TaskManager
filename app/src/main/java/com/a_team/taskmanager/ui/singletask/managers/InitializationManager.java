package com.a_team.taskmanager.ui.singletask.managers;

import android.arch.lifecycle.ViewModelProviders;

import com.a_team.taskmanager.R;
import com.a_team.taskmanager.entity.Task;
import com.a_team.taskmanager.ui.singletask.fragments.AbstractTaskFragment;
import com.a_team.taskmanager.ui.singletask.viewmodel.TaskViewModel;

public class InitializationManager {
    private TaskViewModel mViewModel;
    private Task mTask;

    private UIUpdateManager mUIUpdateManager;

    public InitializationManager(Task task) {
        mUIUpdateManager = new UIUpdateManager();
        mTask = task;
    }

    public void initViewModel(AbstractTaskFragment fragment) {
        if (isReceivedTaskNotNew()) {
            createViewModelAndSubscribeUi(fragment);
            ActionBarTitleManager.setActionBarTitle(fragment.getActivity(), mTask.getTitle());
        } else {
            createViewModel(fragment);
            ActionBarTitleManager.setActionBarTitle(fragment.getActivity(),
                    fragment.getString(R.string.new_task_fragment_action_bar_title));
        }
    }

    private boolean isReceivedTaskNotNew() {
        return mTask != null && !mTask.equals(Task.emptyTask());
    }

    private void createViewModelAndSubscribeUi(AbstractTaskFragment fragment) {
        TaskViewModel.Factory factory = new TaskViewModel.Factory(
                fragment.getActivity().getApplication());
        mViewModel = ViewModelProviders.of(fragment, factory).get(TaskViewModel.class);
        subscribeUi(fragment);
        mUIUpdateManager.updateTitleAndDescription(mTask, fragment.getTitleField(), fragment.getDescriptionField());
    }

    private void createViewModel(AbstractTaskFragment fragment) {
        TaskViewModel.Factory factory = new TaskViewModel.Factory(
                fragment.getActivity().getApplication());
        mViewModel = ViewModelProviders.of(fragment, factory).get(TaskViewModel.class);
    }

    private void subscribeUi(AbstractTaskFragment fragment) {
        mViewModel.getTask().observe(fragment, task ->
                mUIUpdateManager.updateTitleAndDescription(mTask, fragment.getTitleField(), fragment.getDescriptionField()));
    }

    public TaskViewModel getViewModel() {
        return mViewModel;
    }
}
