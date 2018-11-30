package com.a_team.taskmanager.backup.utils;

import java.io.BufferedReader;
import java.io.IOException;

public class JsonFileContentToStringParser {
    public static String getJsonString(BufferedReader reader) throws IOException {
        StringBuilder builder = new StringBuilder();
        String s;
        while ((s = reader.readLine()) != null) {
            builder.append("\n")
                    .append(s);
        }
        return builder.toString();
    }
}
