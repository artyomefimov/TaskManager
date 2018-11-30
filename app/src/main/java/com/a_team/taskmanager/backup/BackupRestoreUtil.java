package com.a_team.taskmanager.backup;

import android.content.Context;
import android.net.Uri;

import com.a_team.taskmanager.entity.Task;
import com.a_team.taskmanager.repository.TaskManagerRepository;

import java.io.IOException;
import java.util.List;

import io.reactivex.Observable;

import static com.a_team.taskmanager.backup.utils.BackupConstants.IO_EXCEPTION_READ;

public class BackupRestoreUtil {
    private FileDataReader fileDataReader;

    public BackupRestoreUtil() {
        fileDataReader = new FileDataReader();
    }

    public Observable<Task[]> restoreTasks(Context context, TaskManagerRepository repository, Uri backupUri) throws IOException {
        List<Task> tasksFromBackup = fileDataReader.readTasksFromBackup(context, repository, backupUri);

        if (tasksFromBackup == null)
            throw new IOException(IO_EXCEPTION_READ);

        Task[] tasks = getTaskArrayFrom(tasksFromBackup);
        return Observable.just(tasks);
    }

    private Task[] getTaskArrayFrom(List<Task> taskList) {
        return taskList.toArray(new Task[0]);
    }
}
