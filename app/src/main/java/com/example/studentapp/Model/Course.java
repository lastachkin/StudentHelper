package com.example.studentapp.Model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class Course implements Serializable {
    @PrimaryKey
    @NonNull
    public String CourseId;

    public String CreatorId;

    public String Title;

    public String Description;

    public String Date;

    public Course() {
    }

    public Course(@NonNull String courseId, String creatorId, String title, String description, String date) {
        CourseId = courseId;
        CreatorId = creatorId;
        Title = title;
        Description = description;
        Date = date;
    }
}
