package com.a_team.taskmanager.ui.singletask;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

import com.a_team.taskmanager.R;

public class ConfirmationDialog extends AlertDialog {

    private AlertDialog.Builder mBuilder;

    public ConfirmationDialog(@NonNull Activity activity) {
        super(activity);
        mBuilder = new AlertDialog.Builder(activity);
        mBuilder.setTitle(R.string.confirm_title)
                .setMessage(R.string.confirm_message)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setNegativeButton(R.string.no, null)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    activity.finish();
                });
    }

    public void showDialog() {
        mBuilder.show();
    }
}