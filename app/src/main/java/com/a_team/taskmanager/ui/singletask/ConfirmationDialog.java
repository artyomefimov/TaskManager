package com.a_team.taskmanager.ui.singletask;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.a_team.taskmanager.R;
import com.a_team.taskmanager.ui.FragmentActivity;
import com.a_team.taskmanager.ui.singletask.managers.PhotoManager;

public class ConfirmationDialog extends AlertDialog {

    private AlertDialog.Builder mBuilder;

    public ConfirmationDialog(@NonNull Context context) {
        super(context);
        mBuilder = new AlertDialog.Builder(context);
        mBuilder.setTitle(R.string.confirm_title)
                .setMessage(R.string.confirm_message)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setNegativeButton(R.string.no, null)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    if (PhotoManager.getInstance() != null)
                        PhotoManager.getInstance().removePhotoIfNecessary((Activity) context);
                    Activity activity = ((Activity) context);
                    activity.finish();
                });
    }

    public void showDialog() {
        mBuilder.show();
    }
}
