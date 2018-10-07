package com.a_team.taskmanager.ui.singletask.managers;

import android.widget.EditText;
import android.widget.TextView;

import com.a_team.taskmanager.entity.Task;

public class UIUpdateManager {
    UIUpdateManager() {
    }

    void updateUI(Task task, EditText titleField, EditText descriptionField, TextView notificationTextView) {
        if (task != null) {
            titleField.setText(task.getTitle());
            descriptionField.setText(task.getDescription());
            //mNotificationTimestamp.setText(task.getNotificationDate().toString()); // todo add notification feature
        }
    }
}
