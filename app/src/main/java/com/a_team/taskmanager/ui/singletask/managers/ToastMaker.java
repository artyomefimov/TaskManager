package com.a_team.taskmanager.ui.singletask.managers;

import android.content.Context;
import android.widget.Toast;

class ToastMaker {
    static void show(Context context, int operation) {
        Toast.makeText(context, operation, Toast.LENGTH_SHORT)
                .show();
    }
}