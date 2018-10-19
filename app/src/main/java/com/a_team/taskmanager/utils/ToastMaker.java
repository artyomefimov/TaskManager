package com.a_team.taskmanager.utils;

import android.content.Context;
import android.support.annotation.StringRes;
import android.widget.Toast;

public class ToastMaker {
    public enum ToastPeriod {
        Short(Toast.LENGTH_SHORT),
        Long(Toast.LENGTH_LONG);

        int period;
        ToastPeriod(int period) {
            this.period = period;
        }
        public int getPeriod() {
            return period;
        }
    }

    public static void show(Context context, @StringRes int operation, ToastPeriod period) {
        Toast.makeText(context, operation, period.getPeriod())
                .show();
    }

    public static void show(Context context, String operation, ToastPeriod period) {
        Toast.makeText(context, operation, period.getPeriod())
                .show();
    }
}