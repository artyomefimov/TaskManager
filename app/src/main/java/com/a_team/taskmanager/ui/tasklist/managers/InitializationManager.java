package com.a_team.taskmanager.ui.tasklist.managers;

import android.arch.lifecycle.ViewModelProviders;

import com.a_team.taskmanager.entity.Task;
import com.a_team.taskmanager.ui.tasklist.tasklistfragment.TaskListFragment;
import com.a_team.taskmanager.utils.TaskSearchUtil;
import com.a_team.taskmanager.viewmodel.TaskListViewModel;

import java.util.List;

public class InitializationManager {
    private TaskListViewModel mViewModel;
    private List<Task> mTasks;

    private TaskSearchUtil mSearchUtil;

    public InitializationManager() {
        mSearchUtil = TaskSearchUtil.getInstance();
    }

    public void createViewModelAndSubscribeUI(TaskListFragment fragment) {
        TaskListViewModel.Factory factory =
                new TaskListViewModel.Factory(fragment.getActivity().getApplication());
        mViewModel = ViewModelProviders.of(fragment, factory).get(TaskListViewModel.class);
        subscribeUi(fragment);
    }

    private void subscribeUi(TaskListFragment fragment) {
        mViewModel.getTasks().observe(fragment, tasks -> {
            if (tasks != null) {
                mTasks = tasks;
                mSearchUtil.setStringTaskData(tasks);
                fragment.updateRecyclerViewAdapter(mTasks);
            }
        });
    }

    public TaskListViewModel getViewModel() {
        return mViewModel;
    }

    public List<Task> getTasks() {
        return mTasks;
    }
}
