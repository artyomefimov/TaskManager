package com.a_team.taskmanager.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.databinding.BaseObservable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.a_team.taskmanager.database.dao.DateConverter;

import java.io.File;
import java.sql.Date;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

@Entity(tableName = "Task")
@TypeConverters({DateConverter.class})
public class Task implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID")
    private long id;

    @ColumnInfo(name = "Title")
    private String title;

    @ColumnInfo(name = "Description")
    private String description;

    @ColumnInfo(name = "Notification")
    private String notificationDate;

    @ColumnInfo(name = "Uuid")
    private String fileUUID;

    @Ignore
    private File photoFile;

    @Deprecated
    /**
     * @deprecated use Task.emptyTask() instead
     */
    public Task() {}

    public static Task emptyTask() {
        return new Task();
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNotificationDate() {
        return notificationDate;
    }

    public void setNotificationDate(String notificationDate) {
        this.notificationDate = notificationDate;
    }

    public String getFileUUID() {
        return fileUUID;
    }

    public void setFileUUID(String fileUUID) {
        this.fileUUID = fileUUID;
    }

    public File getPhotoFile() {
        return photoFile;
    }

    public void setPhotoFile(File photoFile) {
        this.photoFile = photoFile;
    }

    public void setUUID() {
        fileUUID = FilenameGenerator.getUUID().toString();
    }

    public String getPhotoFilename() {
        return new StringBuilder("IMG_")
                .append(fileUUID)
                .append(".jpg")
                .toString();
    }

    public void removePhotoFile() {
        photoFile = null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(notificationDate);
        dest.writeString(fileUUID);
    }

    public static final Parcelable.Creator<Task> CREATOR = new Parcelable.Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel source) {
            Task task = emptyTask();
            task.setId(source.readLong());
            task.setTitle(source.readString());
            task.setDescription(source.readString());
            task.setNotificationDate(source.readString());
            task.setFileUUID(source.readString());
            return task;
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id &&
                Objects.equals(title, task.title) &&
                Objects.equals(description, task.description) &&
                Objects.equals(notificationDate, task.notificationDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, notificationDate);
    }
}
