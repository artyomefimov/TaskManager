package com.a_team.taskmanager.utils;

import android.support.annotation.NonNull;

import java.util.UUID;

public class FilenameGenerator {
    @NonNull
    public static String getTempName() {
        return new StringBuilder(UUID.randomUUID().toString())
                .append(".jpg")
                .toString();
    }
}
