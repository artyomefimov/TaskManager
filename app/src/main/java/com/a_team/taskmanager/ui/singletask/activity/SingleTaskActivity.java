package com.a_team.taskmanager.ui.singletask.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.a_team.taskmanager.entity.Task;
import com.a_team.taskmanager.ui.FragmentActivity;
import com.a_team.taskmanager.ui.singletask.fragments.AbstractTaskFragment;
import com.a_team.taskmanager.ui.singletask.managers.TaskOperationsManagerKeeper;

import static com.a_team.taskmanager.ui.singletask.SingleTaskConstants.ARG_CURRENT_TASK;

public class SingleTaskActivity extends FragmentActivity implements AbstractTaskFragment.OnChangedCallback {
    private boolean isDataChanged;

    @NonNull
    public static Intent newIntent(Context context, Task task) {
        return new Intent(context, SingleTaskActivity.class)
                .putExtra(ARG_CURRENT_TASK, (Parcelable) task);
    }

    @Override
    protected Fragment createFragment() {
        Task task = getIntent().getParcelableExtra(ARG_CURRENT_TASK);
        return AbstractTaskFragment.newInstance(task);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        saveIfNecessary();
        return true;
    }

    @Override
    public void onBackPressed() {
        saveIfNecessary();
    }

    @Override
    public void onDataChanged(boolean isChanged) {
        isDataChanged = isChanged;
    }

    private void saveIfNecessary() {
        if (isDataChanged)
            TaskOperationsManagerKeeper.getInstance().getTaskOperationsManager().updateOrInsertTask(this);
        finish();
    }
}
