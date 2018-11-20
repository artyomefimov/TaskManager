package com.a_team.taskmanager.entity;

import com.a_team.taskmanager.utils.FilenameGenerator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

public class TaskBuilderTest {
    @Test
    public void testBuild() {
        long id = 0;
        String title = "title";
        String description = "desc";
        Long notificationDate = new Date().getTime();
        String fileName = FilenameGenerator.getTempName();

        Task task = new TaskBuilder()
                .setId(id)
                .setTitle(title)
                .setDescription(description)
                .setNotificationDate(notificationDate)
                .setFileName(fileName)
                .build();
        assertEquals(id, task.getId());
        assertEquals(title, task.getTitle());
        assertEquals(description, task.getDescription());
        assertEquals(notificationDate, task.getNotificationDate());
        assertEquals(fileName, task.getPhotoFilename());
    }
}