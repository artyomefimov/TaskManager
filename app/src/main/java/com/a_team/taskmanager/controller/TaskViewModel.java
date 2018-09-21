package com.a_team.taskmanager.controller;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.a_team.taskmanager.BasicApp;
import com.a_team.taskmanager.entity.Task;
import com.a_team.taskmanager.controller.repository.TaskManagerRepository;

import java.io.File;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TaskViewModel extends AndroidViewModel {
    private LiveData<Task> mLiveDataTask;
    private TaskManagerRepository mRepository;

    private Executor mExecutor;

    private long mTaskId;

    public TaskViewModel(@NonNull Application application, TaskManagerRepository repository, long taskId) {
        super(application);
        mRepository = repository;
        mTaskId = taskId;
        mLiveDataTask = new MutableLiveData<>();
        mExecutor = Executors.newSingleThreadExecutor();
    }

    public LiveData<Task> getTask() {
        return mLiveDataTask;
    }

    public void updateOrInsertTask(final Task task) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mRepository.updateOrInsertTask(task);
            }
        });
    }

    public File getPhotoFile(Task task) {
        return mRepository.getPhotoFile(task);
    }

    public void deleteTask(final Task task) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mRepository.deleteTasks(task);
            }
        });
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {

        private final Application mApplication;
        private final TaskManagerRepository mRepository;
        private final long mTaskId;
        
        public Factory(@NonNull Application application, long taskId) {
            mApplication = application;
            mTaskId = taskId;
            mRepository = ((BasicApp) application).getRepository();
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            //noinspection unchecked
            return (T) new TaskViewModel(mApplication, mRepository, mTaskId);
        }
    }
}
