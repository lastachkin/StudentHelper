package com.example.studentapp.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.studentapp.Model.Student;

import java.util.List;

@Dao
public interface StudentDao {
    @Query("SELECT * FROM student")
    List<Student> getAll();

    @Query("SELECT * FROM student WHERE Id = :id")
    Student getById(String id);

    @Query("SELECT * FROM student WHERE Name = :name")
    Student getByName(String name);

    @Query("SELECT * FROM student WHERE Login = :login AND Password = :password")
    Student authorization(String login, String password);

    @Query("SELECT * FROM student WHERE Login = :login")
    Student loginUniqueness(String login);

    @Query("SELECT * FROM course JOIN member ON course.CourseId = member.CourseId JOIN student ON member.MemberId = student.Id WHERE course.Title = :title")
    List<Student> getByCourseTitle(String title);

    @Query("DELETE FROM Student")
    void nukeTable();

    @Insert
    void insert(Student student);

    @Update
    void update(Student student);

    @Delete
    void delete(Student student);
}
