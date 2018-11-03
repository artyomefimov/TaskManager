package com.a_team.taskmanager.utils;

import com.a_team.taskmanager.ui.singletask.managers.notifications.NotificationDateTimeController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateFormatter {
    private static SimpleDateFormat mDateFormat =
            new SimpleDateFormat("d MMM yyyy, EEE, H:mm", Locale.getDefault());

    public static String formatToString(Long time) {
        if (time != null) {
            Date date = new Date(time);
            String s = mDateFormat.format(date);
            if (NotificationDateTimeController.isValidDateTime(new Date(), new Date(time)))
                return mDateFormat.format(new Date(time));
        }
        return "";
    }
}