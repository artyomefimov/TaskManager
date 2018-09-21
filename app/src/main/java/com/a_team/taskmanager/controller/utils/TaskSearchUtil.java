package com.a_team.taskmanager.controller.utils;

import com.a_team.taskmanager.entity.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TaskSearchUtil {
    private static final TaskSearchUtil ourInstance = new TaskSearchUtil();

    private Map<Long, Task> mTasks;
    private Map<Long, String> mStringData;
    private List<Task> mTasksFromSearch;

    public static TaskSearchUtil getInstance() {
        return ourInstance;
    }

    private TaskSearchUtil() {
        mTasks = new LinkedHashMap<>();
        mStringData = new LinkedHashMap<>();

        mTasksFromSearch = new ArrayList<>();
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

    @SuppressWarnings("unchecked")
    public List<Task> performSearch(final String query) {
        mTasksFromSearch = new ArrayList<>();
        performSearch0(query);
        return isNoResults() ? Collections.emptyList() : Collections.unmodifiableList(mTasksFromSearch);
    }

    private boolean isNoResults() {
        return mTasksFromSearch == null || mTasksFromSearch.size() == 0;
    }

    private void performSearch0(final String query) {
        Set<Map.Entry<Long, String>> entrySet = mStringData.entrySet();
        for (Map.Entry<Long, String> entry : entrySet) {
            if (entry.getValue().matches(makeRegex(query))) {
                Task task = mTasks.get(entry.getKey());
                mTasksFromSearch.add(task);
            }
        }
    }

    private String makeRegex(String entry) {
        return "[^\\\\w\\\\d]*" + entry + "+" + "[^\\\\w\\\\d]*";
    }
}
