package com.a_team.taskmanager.data.models

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.TypeConverters
import android.os.Parcel
import android.os.Parcelable
import com.a_team.taskmanager.database.dao.DateConverter
import java.io.Serializable
import java.util.*

@Entity(tableName = "task")
class Task : Serializable, Parcelable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = -1L

    @ColumnInfo(name = "title")
    var title: String = ""

    @ColumnInfo(name = "description")
    var description: String = ""

    @TypeConverters(DateConverter::class)
    @ColumnInfo(name = "notification_date")
    var notificationDate: Long = -1L

    @ColumnInfo(name = "file_name")
    var fileName: String = UUID.randomUUID().toString()

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeLong(id)
        dest?.writeString(title)
        dest?.writeString(description)
        dest?.writeString(notificationDate.toString())
        dest?.writeString(fileName)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Task> {
        override fun createFromParcel(parcel: Parcel): Task {
            val task = Task()
            task.id = parcel.readLong()
            task.title = parcel.readString()
            task.description = parcel.readString()
            task.notificationDate = parcel.readString()?.toLong() ?: -1L
            task.fileName = parcel.readString()
            return task
        }

        override fun newArray(size: Int): Array<Task?> {
            return arrayOfNulls(size)
        }
    }
}