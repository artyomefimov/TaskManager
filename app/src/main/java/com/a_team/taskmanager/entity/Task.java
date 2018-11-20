package com.a_team.taskmanager.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.os.Parcel;
import android.os.Parcelable;

import com.a_team.taskmanager.database.dao.DateConverter;
import com.a_team.taskmanager.utils.NullStringProcessor;

import java.io.Serializable;
import java.sql.Date;
import java.util.Objects;

@Entity(tableName = "Task")
public class Task implements Parcelable, Serializable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID")
    private long id;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "description")
    private String description;

    @TypeConverters({DateConverter.class})
    @ColumnInfo(name = "notification_date")
    private Long notificationDate;

    @ColumnInfo(name = "filename")
    private String fileUUID;

    @Deprecated
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

    public Long getNotificationDate() {
        return notificationDate;
    }

    public void setNotificationDate(Long notificationDate) {
        this.notificationDate = notificationDate;
    }

    public String getFileUUID() {
        return fileUUID;
    }

    public void setFileUUID(String fileUUID) {
        this.fileUUID = fileUUID;
    }

    public String getPhotoFilename() {
        return fileUUID;
    }

    private void setNotificationDateFromString(String notificationDate) {
        this.notificationDate = getLongFromString(notificationDate);
    }

    private Long getLongFromString(String in) {
        if (in == null || in.isEmpty())
            return null;
        else
            return Long.parseLong(in);
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
        dest.writeString(NullStringProcessor.valueOf(notificationDate));
        dest.writeString(fileUUID);
    }

    public static final Parcelable.Creator<Task> CREATOR = new Parcelable.Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel source) {
            Task task = emptyTask();
            task.setId(source.readLong());
            task.setTitle(source.readString());
            task.setDescription(source.readString());
            task.setNotificationDateFromString(source.readString());
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
                ", notificationDate=" + notificationDate +
                ", fileUUID='" + fileUUID + '\'' +
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
                Objects.equals(notificationDate, task.notificationDate) &&
                Objects.equals(fileUUID, task.fileUUID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, notificationDate, fileUUID);
    }
}