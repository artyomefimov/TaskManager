package com.a_team.taskmanager.utils;

public class NullStringProcessor {
    public static String valueOf(Object object) {
        return object == null ? "" : object.toString();
    }
}
