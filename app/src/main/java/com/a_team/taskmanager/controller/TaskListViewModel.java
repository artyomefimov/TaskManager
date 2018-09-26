package com.a_team.taskmanager.controller;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.a_team.taskmanager.BasicApp;
import com.a_team.taskmanager.entity.Task;
import com.a_team.taskmanager.controller.repository.TaskManagerRepository;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TaskListViewModel extends AndroidViewModel {
    private MediatorLiveData<List<Task>> mTasks;
    private Executor mExecutor;
    private TaskManagerRepository mRepository;

    public TaskListViewModel(Application application, TaskManagerRepository repository) {
        super(application);
        mTasks = new MediatorLiveData<>();
        mTasks.setValue(null);

        LiveData<List<Task>> tasks = ((BasicApp) application).getRepository().getTasks();
        mTasks.addSource(tasks, tasks1 -> mTasks.setValue(tasks1));

        mExecutor = Executors.newSingleThreadExecutor();

        mRepository = repository;
    }

    public MutableLiveData<List<Task>> getTasks() {
        return mTasks;
    }

    public void deleteTasks(final Task... tasks) {
        mExecutor.execute(() -> mRepository.deleteTasks(tasks));
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {

        private final Application mApplication;
        private final TaskManagerRepository mRepository;

        public Factory(@NonNull Application application) {
            mApplication = application;
            mRepository = ((BasicApp) application).getRepository();
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            //noinspection unchecked
            return (T) new TaskListViewModel(mApplication, mRepository);
        }
    }
}
