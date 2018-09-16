package com.a_team.taskmanager.controller;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;

import com.a_team.taskmanager.BasicApp;
import com.a_team.taskmanager.model.Task;

import java.util.List;

public class TaskListViewModel extends AndroidViewModel {
    private MediatorLiveData<List<Task>> mTasks;

    public TaskListViewModel(Application application) {
        super(application);
        mTasks = new MediatorLiveData<>();
        mTasks.setValue(null);

        LiveData<List<Task>> tasks = ((BasicApp) application).getRepository().getTasks();
        mTasks.addSource(tasks, new Observer<List<Task>>() {
            @Override
            public void onChanged(@Nullable List<Task> tasks) {
                mTasks.setValue(tasks);
            }
        });
    }

    public MutableLiveData<List<Task>> getTasks() {
        return mTasks;
    }
}
