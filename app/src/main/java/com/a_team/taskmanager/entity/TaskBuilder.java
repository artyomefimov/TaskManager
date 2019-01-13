package com.a_team.taskmanager.entity;

public class TaskBuilder {
    private Task task;

    public TaskBuilder() {
        task = Task.emptyTask();
    }

    public TaskBuilder setId(Long id) {
        task.setId(id);
        return this;
    }

    public TaskBuilder setTitle(String title) {
        task.setTitle(title);
        return this;
    }

    public TaskBuilder setDescription(String description) {
        task.setDescription(description);
        return this;
    }

    public TaskBuilder setNotificationDate(Long notificationDate) {
        task.setNotificationDate(notificationDate);
        return this;
    }

    public TaskBuilder setFileName(String fileName) {
        task.setFileUUID(fileName);
        return this;
    }

    public Task build() {
        return task;
    }
}
