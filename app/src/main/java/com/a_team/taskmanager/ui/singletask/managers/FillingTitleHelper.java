package com.a_team.taskmanager.ui.singletask.managers;

import com.a_team.taskmanager.entity.Task;

class FillingTitleHelper {
    private static final String NO_TITLE = "No title";

    static void fillTitleIfEmpty(Task task) {
        if (isNotHaveTitle(task)) {
            task.setTitle(NO_TITLE);
        }
    }

    private static boolean isNotHaveTitle(Task task) {
        return task.getTitle() == null || task.getTitle().isEmpty();
    }
}
