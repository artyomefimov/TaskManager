package com.a_team.taskmanager.controller.utils;

import com.a_team.taskmanager.entity.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TaskSearchUtil {
    private static final TaskSearchUtil ourInstance = new TaskSearchUtil();

    private Map<Long, Task> mTasks;
    private Map<Long, String> mStringData;
    private List<Task> mTasksFromSearch;

    private Executor mExecutor;
    private CountDownLatch mLatch;

    public static TaskSearchUtil getInstance() {
        return ourInstance;
    }

    private TaskSearchUtil() {
        mTasks = new LinkedHashMap<>();
        mStringData = new LinkedHashMap<>();

        mExecutor = Executors.newSingleThreadExecutor();
        mTasksFromSearch = new ArrayList<>();
        mLatch = new CountDownLatch(1);
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

    @SuppressWarnings("unchecked")
    public List<Task> performSearch(final String query) {
        performSearchInWorkerThread(query);
        try {
            waitForWorkerThread();
        } catch (InterruptedException e) {
            return Collections.EMPTY_LIST;
        }
        return isNoResults() ? Collections.EMPTY_LIST : mTasksFromSearch;
    }

    private boolean isNoResults() {
        return mTasksFromSearch == null || mTasksFromSearch.size() == 0;
    }

    private void waitForWorkerThread() throws InterruptedException {
        mLatch.await();
    }

    private void performSearchInWorkerThread(final String query) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Set<Map.Entry<Long, String>> entrySet = mStringData.entrySet();
                for (Map.Entry<Long, String> entry : entrySet) {
                    if (entry.getValue().matches(makeRegex(query))) {
                        Task task = mTasks.get(entry.getKey());
                        mTasksFromSearch.add(task);
                    }
                }
                mLatch.countDown();
            }
        });
    }

    private String makeRegex(String entry) {
        return "[^\\\\w\\\\d]*" + entry + "+" + "[^\\\\w\\\\d]*";
    }
}
