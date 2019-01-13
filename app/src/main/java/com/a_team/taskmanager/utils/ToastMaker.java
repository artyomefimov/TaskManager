package com.a_team.taskmanager.utils;

import android.content.Context;
import android.support.annotation.StringRes;
import android.widget.Toast;

public class ToastMaker {
    public enum ToastPeriod {
        SHORT(Toast.LENGTH_SHORT),
        LONG(Toast.LENGTH_LONG);

        int period;
        ToastPeriod(int period) {
            this.period = period;
        }
        public int getPeriod() {
            return period;
        }
    }

    public static void show(Context context, @StringRes int message, ToastPeriod period) {
        Toast.makeText(context, message, period.getPeriod())
                .show();
    }

    public static void show(Context context, String message, ToastPeriod period) {
        Toast.makeText(context, message, period.getPeriod())
                .show();
    }
}