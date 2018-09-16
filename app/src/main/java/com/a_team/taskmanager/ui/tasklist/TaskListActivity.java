package com.a_team.taskmanager.ui.tasklist;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.a_team.taskmanager.ui.SingleFragmentActivity;

public class TaskListActivity extends SingleFragmentActivity {

    @NonNull
    public static Intent newIntent(Context context) {
        return new Intent(context, TaskListActivity.class);
    }

    @Override
    protected Fragment createFragment() {
        return TaskListFragment.newInstance();
    }
}
