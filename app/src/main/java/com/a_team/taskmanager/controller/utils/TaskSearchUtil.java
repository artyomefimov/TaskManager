package com.a_team.taskmanager.controller.utils;

import com.a_team.taskmanager.entity.Task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class TaskSearchUtil {
    private static final TaskSearchUtil ourInstance = new TaskSearchUtil();

    private Map<Long, Task> mTasks;
    private Map<Long, String> mStringData;

    public static TaskSearchUtil getInstance() {
        return ourInstance;
    }

    private TaskSearchUtil() {
        mTasks = new LinkedHashMap<>();
        mStringData = new LinkedHashMap<>();
    }

    public Map<Long, Task> getTasks() {
        return mTasks;
    }

    public void setStringTaskData(List<Task> tasks) {
        if (tasks != null) {
            for (Task task : tasks) {
                mTasks.put(task.getId(), task);
                String stringData = task.getTitle() + " " + task.getDescription();
                mStringData.put(task.getId(), stringData);
            }
        }
    }

    public List<Task> performSearch(String query) {
        if (isQueryIncorrect(query)) {
            return Collections.emptyList();
        }
        List<Task> tasksFromSearch = new ArrayList<>();
        Set<Map.Entry<Long, String>> entrySet = mStringData.entrySet();
        for (Map.Entry<Long, String> entry : entrySet) {
            if (entry.getValue().matches(makeRegex(query))) {
                Task task = mTasks.get(entry.getKey());
                tasksFromSearch.add(task);
            }
        }
        return tasksFromSearch;
    }

    private String makeRegex(String entry) {
        return "[^\\\\w\\\\d]*" + entry + "+" + "[^\\\\w\\\\d]*";
    }

    private boolean isQueryIncorrect(String query) {
        return query == null || query.length() == 0;
    }
}
