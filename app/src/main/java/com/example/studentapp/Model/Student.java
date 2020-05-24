package com.example.studentapp.Model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Student {
    @PrimaryKey
    @NonNull
    public String Id;

    public String Login;

    public String Password;

    public String Name;

    public String Mail;

    public Student() {
    }

    public Student(@NonNull String id, String login, String password, String name, String mail) {
        Id = id;
        Login = login;
        Password = password;
        Name = name;
        Mail = mail;
    }
}
