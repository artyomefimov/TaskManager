package com.a_team.taskmanager.ui.singletask.managers.alarms;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.a_team.taskmanager.R;
import com.a_team.taskmanager.utils.ToastMaker;
import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import static com.a_team.taskmanager.utils.ToastMaker.ToastPeriod;

public class AlarmDateTimePicker {
    private static final String TAG_DATETIME_FRAGMENT = "TAG_DATETIME_FRAGMENT";
    private static final String TAG = "NotificationPicker";

    private SwitchDateTimeDialogFragment mDateTimeDialogFragment;
    private Calendar mCalendar;
    private OnChangedNotificationDateCallback mCallback;

    public AlarmDateTimePicker(OnChangedNotificationDateCallback callback) {
        Date current = new Date();
        mCalendar = Calendar.getInstance();
        mCalendar.setTime(current);
        mCallback = callback;
    }

    public void showDateTimePicker(Fragment fragment) {
        configureDateTimePicker(fragment);
        prepareForShowing(fragment.getFragmentManager());
    }

    private void configureDateTimePicker(Fragment fragment) {
        if (fragment != null && fragment.getFragmentManager() != null) {
            mDateTimeDialogFragment = (SwitchDateTimeDialogFragment) fragment.getFragmentManager().findFragmentByTag(TAG_DATETIME_FRAGMENT);
            if (mDateTimeDialogFragment == null) {
                mDateTimeDialogFragment = SwitchDateTimeDialogFragment.newInstance(
                        fragment.getString(R.string.alarm_date_time),
                        fragment.getString(R.string.ok),
                        fragment.getString(R.string.cancel)
                );
            }
            configureTimeSettings();
            configureButtons(fragment.getContext());
        } else {
            Log.e(TAG, "Incoming parameters are incorrect. Fragment: " + fragment);
        }
    }

    private void configureTimeSettings() {
        mDateTimeDialogFragment.setTimeZone(TimeZone.getDefault());
        mDateTimeDialogFragment.set24HoursMode(true);

        int currentYear = mCalendar.get(Calendar.YEAR);
        int currentMonth = mCalendar.get(Calendar.MONTH);
        int currentDay = mCalendar.get(Calendar.DAY_OF_MONTH);

        mDateTimeDialogFragment.setMinimumDateTime(new GregorianCalendar(currentYear, currentMonth, currentDay).getTime());

        int yearDecadeAfter = currentYear + 10;

        mDateTimeDialogFragment.setMaximumDateTime(new GregorianCalendar(yearDecadeAfter, Calendar.DECEMBER, 31).getTime());

        try {
            mDateTimeDialogFragment
                    .setSimpleDateMonthAndDayFormat(new SimpleDateFormat("MMMM dd", Locale.getDefault()));
        } catch (SwitchDateTimeDialogFragment.SimpleDateMonthAndDayFormatException e) {
            Log.e(TAG, "Could not set simple date, month and day format: " + e.getLocalizedMessage());
        }
    }

    private void configureButtons(Context context) {
        mDateTimeDialogFragment.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Date date) {
                boolean isPickedTimeValid = AlarmDateTimeController.isValidDateTime(mCalendar.getTime(), date);
                if (isPickedTimeValid) {
                    mCallback.onNotificationDateChanged(date);
                    mDateTimeDialogFragment.dismiss();
                } else {
                    ToastMaker.show(context, R.string.invalid_date_time, ToastPeriod.LONG);
                }
            }

            @Override
            public void onNegativeButtonClick(Date date) {
                mDateTimeDialogFragment.dismiss();
            }
        });
    }

    private void prepareForShowing(FragmentManager fragmentManager) {
        if (fragmentManager != null) {
            mDateTimeDialogFragment.startAtCalendarView();
            setDefaultDateTime();
            mDateTimeDialogFragment.show(fragmentManager, TAG_DATETIME_FRAGMENT);
        } else {
            Log.e(TAG, "Incoming fragment manager is null");
        }
    }

    private void setDefaultDateTime() {
        int currentYear = mCalendar.get(Calendar.YEAR);
        int currentMonth = mCalendar.get(Calendar.MONTH);
        int nextDay = mCalendar.get(Calendar.DAY_OF_MONTH);
        int currentHour = mCalendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = mCalendar.get(Calendar.MINUTE);
        mDateTimeDialogFragment.setDefaultDateTime(
                new GregorianCalendar(currentYear, currentMonth, nextDay, currentHour, currentMinute)
                        .getTime());
    }

    public interface OnChangedNotificationDateCallback {
        void onNotificationDateChanged(Date newDate);
    }
}
