package com.a_team.taskmanager.ui.singletask.managers;

import android.app.Activity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

class ActionBarTitleManager {
    static void setActionBarTitle(Activity activity, String title) {
        AppCompatActivity appCompatActivity = ((AppCompatActivity) activity);
        ActionBar actionBar = appCompatActivity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
}
