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

import com.a_team.taskmanager.database.dao.DateConverter;

import java.sql.Date;
import java.util.Objects;

@Entity(tableName = "Task")
@TypeConverters({DateConverter.class})
public class Task extends BaseObservable implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID")
    private long id;

    @NonNull
    @ColumnInfo(name = "Title")
    private String title;

    @ColumnInfo(name = "Description")
    private String description;

    @ColumnInfo(name = "Notification")
    private Date notificationDate;

    @Deprecated
    /**
     * @deprecated use Task.emptyTask() instead
     */
    public Task() {
    }

    public static Task emptyTask() {
        return new Task();
    }

    @Ignore
    public Task(@NonNull String name) {
        this.title = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getId() {
        return this.id;
    }

    @NonNull
    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    public Date getNotificationDate() {
        return notificationDate;
    }

    public void setNotificationDate(@NonNull Date notificationDate) {
        this.notificationDate = notificationDate;
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
        dest.writeSerializable(notificationDate);
    }

    public static final Parcelable.Creator<Task> CREATOR = new Parcelable.Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel source) {
            Task task = emptyTask();
            task.setId(source.readLong());
            task.setTitle(source.readString());
            task.setDescription(source.readString());
            task.setNotificationDate((Date) source.readSerializable());
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
