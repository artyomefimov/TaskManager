package com.a_team.taskmanager.ui.singletask.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.a_team.taskmanager.R;
import com.a_team.taskmanager.utils.FilenameGenerator;
import com.a_team.taskmanager.utils.ToastMaker;

import static com.a_team.taskmanager.utils.ToastMaker.ToastPeriod.SHORT;

public class NewTaskFragment extends AbstractTaskFragment {
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        receiveArgsFromBundle();
        setUniqueNameForPhotoFile();
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    protected void configureSetNotificationButton() {
        mSetNotificationButton.setOnClickListener((view) ->
                ToastMaker.show(getActivity(), R.string.set_notification_on_not_created_task, SHORT));
    }

    @Override
    protected void configureDeleteNotificationButton() {
        mDeleteNotificationButton.setOnClickListener((view) ->
                ToastMaker.show(getActivity(), R.string.set_notification_on_not_created_task, SHORT));
    }

    private void setUniqueNameForPhotoFile() {
        mTask.setFileUUID(FilenameGenerator.getTempName());
    }

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
