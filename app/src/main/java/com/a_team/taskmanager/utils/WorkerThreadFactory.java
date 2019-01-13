package com.a_team.taskmanager.utils;

import android.support.annotation.NonNull;

import java.util.concurrent.ThreadFactory;

public class WorkerThreadFactory implements ThreadFactory {
    @Override
    public Thread newThread(@NonNull Runnable r) {
        Thread newThread = new Thread(r);
        newThread.setPriority(Thread.MIN_PRIORITY);
        return newThread;
    }
}
