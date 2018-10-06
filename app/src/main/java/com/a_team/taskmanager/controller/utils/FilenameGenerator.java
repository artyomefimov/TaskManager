package com.a_team.taskmanager.controller.utils;

import java.util.UUID;

public class FilenameGenerator {
    public static UUID getUUID() {
        return UUID.randomUUID();
    }

    public static String getTempName() {
        return new StringBuilder(UUID.randomUUID().toString())
                .append(".jpg")
                .toString();
    }
}
