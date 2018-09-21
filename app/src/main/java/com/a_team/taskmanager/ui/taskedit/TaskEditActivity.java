package com.a_team.taskmanager.ui.taskedit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.a_team.taskmanager.Constants;
import com.a_team.taskmanager.entity.Task;
import com.a_team.taskmanager.ui.SingleFragmentActivity;

public class TaskEditActivity extends SingleFragmentActivity {

    @NonNull
    public static Intent newIntent(Context context, Task task) {
        return new Intent(context, TaskEditActivity.class).putExtra(Constants.ARG_CURRENT_TASK, task);
    }

    @Override
    protected Fragment createFragment() {
        Task task = getIntent().getParcelableExtra(Constants.ARG_CURRENT_TASK);
        return TaskEditFragment.newInstance(task);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
