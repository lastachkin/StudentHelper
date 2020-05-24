package com.example.studentapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studentapp.Model.Student;
import com.example.studentapp.Remote.RestAPI;
import com.example.studentapp.Remote.RetrofitClient;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class Login extends AppCompatActivity {
    String userId;
    EditText loginView, passwordView;
    CompositeDisposable disposables;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        disposables = new CompositeDisposable();

        loginView = findViewById(R.id.login);
        passwordView = findViewById(R.id.password);

        //Purging tables
        //App.getInstance().getDatabase().studentDao().nukeTable();
        //App.getInstance().getDatabase().memberDao().nukeTable();
        //App.getInstance().getDatabase().courseDao().nukeTable();
    }

    boolean login() {
        String login = loginView.getText().toString();
        String password = Registration.generateMD5Hash(passwordView.getText().toString());

        Student student = App.getInstance().getDatabase().studentDao().authorization(login, password);

        disposables.add(App.getInstance().getRestAPI().loginUser(student)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if(response == "User not exists" || response == "Wrong password")
                        Toast.makeText(this, getResources().getString(R.string.login_fail), Toast.LENGTH_SHORT).show();
                        Log.i(App.tag, response);
                    }, throwable -> {
                        Log.e(App.tag, throwable.getMessage());
                    })
            );

        if(student != null) {
            App.userId = student.Id;
            return true;
        }

        return false;
    }

    public void startRegistrationPage(View view) {
        Intent intent = new Intent(this, Registration.class);
        startActivity(intent);
    }

    public void startHomePage(View view) {
        if(login()) {
            Intent intent = new Intent(this, Home.class);
            //intent.putExtra("USER_ID", userId);
            startActivity(intent);
        }
        else {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.login_fail), Toast.LENGTH_SHORT).show();
            return;
        }
    }
}
