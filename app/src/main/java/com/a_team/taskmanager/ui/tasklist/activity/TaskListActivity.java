package com.a_team.taskmanager.ui.tasklist.activity;

import android.support.v4.app.Fragment;

import com.a_team.taskmanager.ui.FragmentActivity;
import com.a_team.taskmanager.ui.tasklist.tasklistfragment.TaskListFragment;

public class TaskListActivity extends FragmentActivity {
    @Override
    protected Fragment createFragment() {
        return TaskListFragment.newInstance();
    }
}