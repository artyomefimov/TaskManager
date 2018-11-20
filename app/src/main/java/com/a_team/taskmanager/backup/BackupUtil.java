package com.a_team.taskmanager.backup;

public class BackupUtil {
    public static String getStringFromId(long id) {
        return id + "";
    }

    public static String getFolderName(String name) {
        return name + "/";
    }

    public static String getFileName(String name) {
        return name + ".txt";
    }
}
