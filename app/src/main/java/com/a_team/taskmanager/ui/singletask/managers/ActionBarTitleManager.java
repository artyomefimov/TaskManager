package com.a_team.taskmanager.ui.singletask.managers;

import android.app.Activity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

public class ActionBarTitleManager {
    public static void setActionBarTitle(Activity activity, String title) {
        AppCompatActivity appCompatActivity = ((AppCompatActivity) activity);
        ActionBar actionBar = appCompatActivity.getSupportActionBar();
        actionBar.setTitle(title);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }
}
