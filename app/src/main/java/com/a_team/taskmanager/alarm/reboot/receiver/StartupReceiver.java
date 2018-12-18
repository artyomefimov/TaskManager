package com.a_team.taskmanager.alarm.reboot.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.a_team.taskmanager.alarm.reboot.PropertiesReader;

public class StartupReceiver extends BroadcastReceiver {
    private static final String BOOT_COMPLETED_ACTION = "android.intent.action.BOOT_COMPLETED";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (BOOT_COMPLETED_ACTION.equals(intent.getAction())) {
            Log.i("StartupReceiver", "boot completed. Resetting notifications...");

            PropertiesReader.getInstance().resetNotificationsFromProperties(context);
        }
    }
}
