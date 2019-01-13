package com.a_team.taskmanager.ui.tasklist.viewmodel;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.a_team.taskmanager.BasicApp;
import com.a_team.taskmanager.R;
import com.a_team.taskmanager.backup.BackupRestoreUtil;
import com.a_team.taskmanager.backup.FileDataWriter;
import com.a_team.taskmanager.entity.Task;
import com.a_team.taskmanager.repository.TaskManagerRepository;
import com.a_team.taskmanager.utils.IntentBuilder;
import com.a_team.taskmanager.utils.ToastMaker;
import com.a_team.taskmanager.utils.WorkerThreadFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import static com.a_team.taskmanager.utils.ToastMaker.ToastPeriod;

public class TaskListViewModel extends AndroidViewModel {
    private MediatorLiveData<List<Task>> mTasks;

    private Executor mExecutor;
    private TaskManagerRepository mRepository;
    private BackupRestoreUtil mRestoreUtil;
    private FileDataWriter mFileDataWriter;

     public TaskListViewModel(Application application) {
        super(application);
        mRepository = ((BasicApp) application).getRepository();

        mTasks = new MediatorLiveData<>();
        mTasks.setValue(null);

        LiveData<List<Task>> tasks = mRepository.getTasks();
        mTasks.addSource(tasks, tasks1 -> mTasks.setValue(tasks1));

        mExecutor = Executors.newSingleThreadExecutor(new WorkerThreadFactory());

        mRestoreUtil = new BackupRestoreUtil();

        mFileDataWriter = new FileDataWriter();
    }

    public MutableLiveData<List<Task>> getTasks() {
        return mTasks;
    }

    public void deleteTasks(final Task... tasks) {
        mExecutor.execute(() -> mRepository.deleteTasks(tasks));
    }

    public void addTasksFromBackupToDatabase(Activity activity, Uri backupUri) throws IOException {
        mRestoreUtil.restoreTasks(activity, mRepository, backupUri)
                .subscribe(new DisposableObserver<Task[]>() {
                    @Override
                    public void onNext(Task[] tasks) {
                        if (tasks != null) {
                            mExecutor.execute(() -> mRepository.insertTasks(tasks));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastMaker.show(activity, e.getMessage(), ToastPeriod.LONG);
                    }

                    @Override
                    public void onComplete() {
                        ToastMaker.show(activity, R.string.restore_successful, ToastPeriod.SHORT);
                    }
                });
    }

    public void storeTasksToBackup(Activity activity, List<Task> tasks) throws IOException {
        mFileDataWriter.writeTasksToBackup(activity, mRepository, tasks)
                .subscribe(new DisposableObserver<File>() {
                    @Override
                    public void onNext(File file) {
                        Intent intentForSharingBackup = IntentBuilder.buildIntentForSharingBackupFile(activity, file);
                        if (intentForSharingBackup.resolveActivity(activity.getPackageManager()) != null) {
                            activity.startActivity(intentForSharingBackup);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastMaker.show(activity, e.getMessage(), ToastPeriod.LONG);
                    }

                    @Override
                    public void onComplete() {
                        ToastMaker.show(activity, R.string.store_successful, ToastPeriod.SHORT);
                    }
                });
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {

        private final Application mApplication;

        public Factory(@NonNull Application application) {
            mApplication = application;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            //noinspection unchecked
            return (T) new TaskListViewModel(mApplication);
        }
    }
}
