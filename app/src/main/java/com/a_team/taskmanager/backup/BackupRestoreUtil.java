package com.a_team.taskmanager.backup;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.a_team.taskmanager.entity.Task;
import com.a_team.taskmanager.repository.TaskManagerRepository;

import java.io.IOException;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.a_team.taskmanager.backup.utils.BackupConstants.IO_EXCEPTION_READ;

public class BackupRestoreUtil {
    private FileDataReader fileDataReader;

    public BackupRestoreUtil() {
        fileDataReader = new FileDataReader();
    }

    public Observable<Task[]> restoreTasks(Context context, TaskManagerRepository repository, Uri backupUri) throws IOException {
        return Observable.fromCallable(() -> {
            List<Task> tasksFromBackup = fileDataReader.readTasksFromBackup(context, repository, backupUri);

            Log.i("Backup", "tasks restored. Thread: " + Thread.currentThread().getName());

            if (tasksFromBackup == null)
                throw new IOException(IO_EXCEPTION_READ);

            return getTaskArrayFrom(tasksFromBackup);
        })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Task[] getTaskArrayFrom(List<Task> taskList) {
        return taskList.toArray(new Task[0]);
    }
}
