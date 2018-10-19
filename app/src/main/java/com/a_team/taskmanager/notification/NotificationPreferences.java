package com.a_team.taskmanager.notification;

import android.content.Context;
import android.preference.PreferenceManager;

public class NotificationPreferences {
    private static final String PREF_IS_ALARM_ON = "isAlarmOn";

    public static boolean isAlarmOn(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PREF_IS_ALARM_ON, false);
    }

    public static void setIsAlarmOn(Context context, boolean value) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(PREF_IS_ALARM_ON, value)
                .apply();
    }
}
