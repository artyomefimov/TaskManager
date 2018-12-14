package com.a_team.taskmanager.ui.tasklist.managers;

import android.os.AsyncTask;
import android.util.Log;

import com.a_team.taskmanager.entity.Task;
import com.a_team.taskmanager.ui.tasklist.viewmodel.TaskListViewModel;

public class SwipeDeleteAsyncTask extends AsyncTask<Void, Void, Void> {
    private static final long CANCEL_DELETE_DELAY = 4000;
    private static final String TAG = "SwipeDeleteAsyncTask";

    private TaskListViewModel mViewModel;
    private Task mTaskToDelete;

    public SwipeDeleteAsyncTask(TaskListViewModel viewModel, Task taskToDelete) {
        mViewModel = viewModel;
        mTaskToDelete = taskToDelete;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            Thread.sleep(CANCEL_DELETE_DELAY);
        } catch (InterruptedException e) {
            Log.i(TAG, "Undo button was pressed. Task "+ mTaskToDelete + " was not deleted.");
            return null;
        }
        if (mViewModel != null) {
            Log.i(TAG, "undo was not pressed. Deleting task: " + mTaskToDelete);
            mViewModel.deleteTasks(mTaskToDelete);
        }

        return null;
    }
}
