package com.example.studentapp.Model;

import androidx.annotation.NonNull;
import androidx.room.Entity;

@Entity(primaryKeys = {"CourseId", "MemberId"})
public class Member {
    @NonNull
    public String CourseId;

    @NonNull
    public String MemberId;

    public Member() {
    }

    public Member(@NonNull String courseId, @NonNull String memberId) {
        CourseId = courseId;
        MemberId = memberId;
    }
}
