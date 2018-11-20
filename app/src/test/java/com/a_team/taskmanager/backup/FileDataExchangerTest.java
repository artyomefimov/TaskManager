package com.a_team.taskmanager.backup;

import android.app.Application;

import com.a_team.taskmanager.entity.Task;
import com.a_team.taskmanager.entity.TaskBuilder;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

public class FileDataExchangerTest {
    private FileDataWriter writer;
    private FileDataReader reader;
    private List<Task> tasks;

    private Application application;

    @Before
    public void setUp() {
        writer = new FileDataWriter();
        reader = new FileDataReader();

        Task task1 = new TaskBuilder()
                .setId(0L)
                .setTitle("t1")
                .setDescription("d1")
                .setNotificationDate(new Date().getTime())
                .setFileName("f1")
                .build();
        Task task2 = new TaskBuilder()
                .setId(1L)
                .setTitle("t2")
                .setDescription("d2")
                .setNotificationDate(new Date().getTime())
                .setFileName("f2")
                .build();
        tasks = new ArrayList<>();
        tasks.add(task1);
        tasks.add(task2);
    }

    @Test
    public void writeAndReadTasksFromBackup() throws IOException {
        File file = writer.writeTasksToBackup(null, tasks);
        List<Task> fromFile = reader.readTasksFromBackup(file);
        assertEquals(tasks, fromFile);
    }
}