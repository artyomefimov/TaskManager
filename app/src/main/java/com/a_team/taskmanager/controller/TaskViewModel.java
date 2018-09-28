package com.a_team.taskmanager.controller;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;

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

    public TaskViewModel(@NonNull Application application, TaskManagerRepository repository) {
        super(application);
        mRepository = repository;
        mLiveDataTask = new MutableLiveData<>();
        mExecutor = Executors.newSingleThreadExecutor();
    }

    public LiveData<Task> getTask() {
        return mLiveDataTask;
    }

    public void updateOrInsertTask(final Task task) {
        mExecutor.execute(() -> mRepository.updateOrInsertTask(task));
    }

    public File getPhotoFile(Task task) {
        return mRepository.getPhotoFile(task);
    }

    public void removePhotoFile(Uri uri) {
        mExecutor.execute(() -> mRepository.removePhotoFile(uri));
    }

    public void deleteTask(final Task task) {
        mExecutor.execute(() -> mRepository.deleteTasks(task));
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
            return (T) new TaskViewModel(mApplication, mRepository);
        }
    }
}
