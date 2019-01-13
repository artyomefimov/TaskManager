package com.a_team.taskmanager.backup.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class UnpackUtil {
    public static void unpackFromStreamToFile(InputStream streamOfZipFile, File destinationFile) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(destinationFile)){
            byte[] buffer = new byte[1024];
            int length;
            while ((length = streamOfZipFile.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
        }
    }
}
