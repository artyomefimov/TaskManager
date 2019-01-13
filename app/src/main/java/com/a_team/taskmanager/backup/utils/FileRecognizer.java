package com.a_team.taskmanager.backup.utils;

public class FileRecognizer {
    public static boolean isTaskFile(String filename) {
        String extension = filename.substring(filename.indexOf('.') + 1, filename.length());
        return "json".equals(extension);
    }
}
