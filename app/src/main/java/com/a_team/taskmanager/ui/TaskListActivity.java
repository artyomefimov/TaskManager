package com.a_team.taskmanager.ui;

import android.support.v4.app.Fragment;

public class TaskListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return TaskListFragment.newInstance();
    }
}
