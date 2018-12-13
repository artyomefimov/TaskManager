package com.a_team.taskmanager.utils;

import android.app.Activity;
import android.support.design.widget.Snackbar;

import com.a_team.taskmanager.R;

public class SnackbarMaker {
    public static Snackbar makeUndoDeleteSnackbar(Activity activity) {
        if (activity != null) {
            return Snackbar.make(
                    activity.findViewById(R.id.task_list_layout),
                    R.string.task_removed,
                    Snackbar.LENGTH_LONG);
        }
        return null;
    }
}
