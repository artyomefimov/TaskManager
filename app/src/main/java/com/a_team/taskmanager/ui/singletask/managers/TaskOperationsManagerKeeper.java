package com.a_team.taskmanager.ui.singletask.managers;

public class TaskOperationsManagerKeeper {
    private static TaskOperationsManagerKeeper instance;
    private TaskOperationsManager mTaskOperationsManager;

    private TaskOperationsManagerKeeper() {}

    public static TaskOperationsManagerKeeper getInstance() {
        if (instance == null)
            instance = new TaskOperationsManagerKeeper();
        return instance;
    }

    public TaskOperationsManager getTaskOperationsManager() {
        return mTaskOperationsManager;
    }

    public void setTaskOperationsManager(TaskOperationsManager taskOperationsManager) {
        mTaskOperationsManager = taskOperationsManager;
    }
}
