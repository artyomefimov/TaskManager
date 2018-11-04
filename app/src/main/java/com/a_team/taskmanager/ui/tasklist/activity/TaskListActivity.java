package com.a_team.taskmanager.ui.tasklist.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.a_team.taskmanager.ui.FragmentActivity;
import com.a_team.taskmanager.ui.tasklist.tasklistfragment.TaskListFragment;

public class TaskListActivity extends FragmentActivity {
    @Override
    protected Fragment createFragment() {
        return TaskListFragment.newInstance();
    }

    @NonNull
    public static Intent newIntent(Context context) {
        return new Intent(context, TaskListActivity.class);
    }
}