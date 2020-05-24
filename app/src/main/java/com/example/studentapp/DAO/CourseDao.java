package com.example.studentapp.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.studentapp.Model.Course;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface CourseDao {
    @Query("SELECT * FROM course INNER JOIN member ON course.CourseId = member.CourseId WHERE member.memberId = :id")
    List<Course> registeredCourses(String id);

    @Query("SELECT * FROM course WHERE CourseId NOT IN (SELECT DISTINCT CourseId FROM member WHERE MemberId = :id)")
    List<Course> availableCourses(String id);

    @Query("SELECT * FROM course WHERE CourseId = :id")
    Course getById(String id);

    @Query("SELECT * FROM course WHERE Title = :title")
    Course getByTitle(String title);

    @Query("DELETE FROM Course")
    void nukeTable();

    @Insert
    void insert(Course course);

    @Update
    void update(Course course);

    @Delete
    void delete(Course course);
}
