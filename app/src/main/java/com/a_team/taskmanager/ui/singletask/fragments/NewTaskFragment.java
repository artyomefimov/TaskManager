package com.a_team.taskmanager.ui.singletask.fragments;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.a_team.taskmanager.R;

public class NewTaskFragment extends AbstractTaskFragment {

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_task_new, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.task_single_menu_save:
                performSave();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
