package com.a_team.taskmanager.utils;

public class DateTimeKeeper {
    private static DateTimeKeeper instance;
    private String mPickedDateTime;
    private DateTimeKeeper() {}

    public static DateTimeKeeper getInstance() {
        if (instance == null)
            instance = new DateTimeKeeper();
        return instance;
    }

    public String getPickedDateTime() {
        return mPickedDateTime;
    }

    public void setPickedDateTime(String pickedDateTime) {
        mPickedDateTime = pickedDateTime;
    }
}
