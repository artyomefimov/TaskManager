package com.a_team.taskmanager.entity;

import java.sql.Date;

public interface AbstractTask {
    long getId();
    void setId(long id);

    String getDescription();
    void setDescription(String description);

    String getTitle();
    void setTitle(String title);

    Date getNotificationDate();
    void setNotificationDate(Date notificationDate);
}
