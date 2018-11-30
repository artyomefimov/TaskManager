package com.a_team.taskmanager.ui.tasklist.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.a_team.taskmanager.R;
import com.a_team.taskmanager.ui.FragmentActivity;
import com.a_team.taskmanager.ui.tasklist.tasklistfragment.TaskListFragment;

import static com.a_team.taskmanager.utils.RequestCodeStorage.READ_PERMISSION_REQUEST_CODE;
import static com.a_team.taskmanager.utils.RequestCodeStorage.WRITE_PERMISSION_REQUEST_CODE;

public class TaskListActivity extends FragmentActivity {
    private static final String TAG = "TaskListActivity";
    @Override
    protected Fragment createFragment() {
        return TaskListFragment.newInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },
                    WRITE_PERMISSION_REQUEST_CODE
            );
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    },
                    READ_PERMISSION_REQUEST_CODE
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case WRITE_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    Log.i(TAG, "Write permission granted");
                else
                    Log.i(TAG, "Write permission denied");
                break;
            case READ_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    Log.i(TAG, "Read permission granted");
                else
                    Log.i(TAG, "Read permission denied");
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        TaskListFragment taskListFragment = (TaskListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (taskListFragment != null) {
            taskListFragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}