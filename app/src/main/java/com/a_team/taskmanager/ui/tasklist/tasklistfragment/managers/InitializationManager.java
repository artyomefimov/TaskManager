package com.a_team.taskmanager.ui.tasklist.tasklistfragment.managers;

import android.arch.lifecycle.ViewModelProviders;

import com.a_team.taskmanager.controller.TaskListViewModel;
import com.a_team.taskmanager.controller.utils.TaskSearchUtil;
import com.a_team.taskmanager.entity.Task;
import com.a_team.taskmanager.ui.tasklist.tasklistfragment.TaskListFragment;

import java.util.List;

public class InitializationManager {
    private static final InitializationManager ourInstance = new InitializationManager();

    private TaskListViewModel mViewModel;
    private List<Task> mTasks;

    private TaskSearchUtil mSearchUtil;

    public static InitializationManager getInstance() {
        return ourInstance;
    }

    private InitializationManager() {
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
}
