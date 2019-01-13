package com.a_team.taskmanager.backup;

import com.a_team.taskmanager.backup.utils.FileRecognizer;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BackupUtilTest {

    @Test
    public void isTaskFile() {
        String taskFilename = "task.txt";
        String photoFilename = "photo.jpg";

        assertTrue(FileRecognizer.isTaskFile(taskFilename));
        assertFalse(FileRecognizer.isTaskFile(photoFilename));
    }
}