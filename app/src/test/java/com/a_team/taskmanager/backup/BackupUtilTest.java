package com.a_team.taskmanager.backup;

import com.a_team.taskmanager.backup.utils.FileRecognizer;

import org.junit.Test;

import static org.junit.Assert.*;

public class BackupUtilTest {

    @Test
    public void isTaskFile() {
        String taskFilename = "task.txt";
        String photoFilename = "photo.jpg";

        assertTrue(FileRecognizer.isTaskFile(taskFilename));
        assertFalse(FileRecognizer.isTaskFile(photoFilename));
    }
}