package com.example.studentapp.Database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.studentapp.DAO.CourseDao;
import com.example.studentapp.DAO.MemberDao;
import com.example.studentapp.DAO.StudentDao;
import com.example.studentapp.Model.Course;
import com.example.studentapp.Model.Member;
import com.example.studentapp.Model.Student;

@Database(entities = {Student.class, Course.class, Member.class}, version = 4)
public abstract class AppDatabase extends RoomDatabase {
    public abstract StudentDao studentDao();
    public abstract CourseDao courseDao();
    public abstract MemberDao memberDao();
}
