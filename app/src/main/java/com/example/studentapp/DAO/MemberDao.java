package com.example.studentapp.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.studentapp.Model.Member;

@Dao
public interface MemberDao {
    @Query("SELECT * FROM member WHERE CourseId = :courseId AND MemberId = :id")
    Member getById(String courseId, String id);

    @Query("DELETE FROM Member")
    void nukeTable();

    @Insert
    void insert(Member member);

    @Update
    void update(Member member);

    @Delete
    void delete(Member member);
}
