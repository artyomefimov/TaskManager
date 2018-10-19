package com.a_team.taskmanager.ui.singletask.managers;

import com.a_team.taskmanager.entity.Task;

class FillingTitleHelper {
    private static final String ONLY_PHOTO = "Photo task";

    static void fillTitleIfEmpty(Task task) {
        if (isNotHaveTitle(task)) {
            if (isHaveDescription(task)) {
                task.setTitle(task.getDescription());
            } else if (isHavePhoto(task)) {
                task.setTitle(ONLY_PHOTO);
            }
        }
    }

    private static boolean isNotHaveTitle(Task task) {
        return task.getTitle() == null || task.getTitle().isEmpty();
    }

    private static boolean isHaveDescription(Task task) {
        return task.getDescription() != null && !task.getDescription().isEmpty();
    }

    private static boolean isHavePhoto(Task task) {
        return task.getPhotoFilename() != null && !task.getPhotoFilename().isEmpty();
    }
}
