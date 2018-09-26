package com.a_team.taskmanager.ui.singletask;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.a_team.taskmanager.R;

public class ConfirmationDialog extends AlertDialog {

    private AlertDialog.Builder mBuilder;

    protected ConfirmationDialog(@NonNull Context context) {
        super(context);
        mBuilder = new AlertDialog.Builder(context);
        mBuilder.setTitle(R.string.confirm_title)
                .setMessage(R.string.confirm_message)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setNegativeButton(R.string.no, null)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    AppCompatActivity activity = ((AppCompatActivity) context);
                    activity.finish();
                }).show();
    }
}
