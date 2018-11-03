package com.a_team.taskmanager.ui.singletask.managers;

import android.widget.EditText;
import android.widget.TextView;

import com.a_team.taskmanager.entity.Task;
import com.a_team.taskmanager.utils.DateFormatter;

public class UIUpdateManager {

    UIUpdateManager() { }

    void updateTitleAndDescription(Task task, EditText titleField, EditText descriptionField) {
        if (task != null) {
            titleField.setText(task.getTitle());
            descriptionField.setText(task.getDescription());
        }
    }

    public static void setNotificationText(TextView notificationTextView, Long notificationDate) {
        String formattedDate = DateFormatter.formatToString(notificationDate);
        notificationTextView.setText(formattedDate);
    }

    public static void removeNotificationText(TextView notificationTextView) {
        notificationTextView.setText("");
    }
}
