package com.example.studentapp;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.studentapp.Model.Member;
import com.google.android.material.navigation.NavigationView;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class RegCourse extends AppCompatActivity {
    TextView tvDate;
    EditText titleView, descriptionView;
    String courseId, title, description, date;

    private ActionBarDrawerToggle toggle = null;
    private CompositeDisposable disposables;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_course);
        disposables = new CompositeDisposable();

        courseId = getIntent().getStringExtra("AV_COURSE_ID");
        title = getIntent().getStringExtra("AV_TITLE");
        description = getIntent().getStringExtra("AV_DESCRIPTION");
        date = getIntent().getStringExtra("AV_DATE");

        tvDate = findViewById(R.id.tvDate);
        titleView = findViewById(R.id.title);
        descriptionView = findViewById(R.id.description);

        titleView.setText(title);
        descriptionView.setText(description);
        tvDate.setText(date);

        titleView.setEnabled(false);
        descriptionView.setEnabled(false);
        tvDate.setEnabled(false);

        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.navView);

        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView.setNavigationItemSelectedListener(
                item -> {
                    Intent intent;
                    switch (item.getItemId()) {
                        case R.id.home:
                            intent = new Intent(this, Home.class);
                            startActivity(intent);
                            break;
                        case R.id.miItem1:
                            intent = new Intent(this, CreationPage.class);
                            startActivity(intent);
                            break;
                        case R.id.miItem2:
                            intent = new Intent(this, RegisteredCourses.class);
                            startActivity(intent);
                            break;
                        case R.id.miItem3:
                            intent = new Intent(this, AvailableCourses.class);
                            startActivity(intent);
                            break;
                    }
                    return true;
                }
        );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void subscribeCourseHandler(View view) {
        //Insert into local DB
        App.getInstance().getDatabase().memberDao().insert(new Member(courseId, App.userId));
        Log.i(App.tag, "Insert into Member OK - Local DB");

        //Insert into remote DB
        disposables.add(App.getInstance().getRestAPI().addMember(new Member(courseId, App.userId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    Log.i(App.tag, response);
                }, throwable -> Log.e(App.tag, throwable.getMessage())));

        onBackPressed();
    }
}
