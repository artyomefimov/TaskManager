package com.a_team.taskmanager.ui.singletask;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.a_team.taskmanager.R;

public class TaskEditFragment extends AbstractTaskFragment {

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        performPhotoUpdating();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_task_edit, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.task_edit_menu_save:
                performSave();
                return true;
            case R.id.task_edit_menu_delete:
                performDelete();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
