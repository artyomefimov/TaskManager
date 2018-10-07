package com.a_team.taskmanager.ui.tasklist.tasklistfragment.managers;

import android.support.v4.util.LongSparseArray;

public class PhotoNameContainer {
    private static PhotoNameContainer instance;

    private LongSparseArray<String> newPhotoNames;

    private PhotoNameContainer() {
        newPhotoNames = new LongSparseArray<>();
    }

    public static PhotoNameContainer getInstance() {
        if (instance == null)
            instance = new PhotoNameContainer();
        return instance;
    }

    public void putName(long key, String value) {
        newPhotoNames.put(key, value);
    }

    public String getName(long key) {
        return newPhotoNames.get(key);
    }
}