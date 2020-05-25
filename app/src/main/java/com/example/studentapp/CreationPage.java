package com.example.studentapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.studentapp.Model.Course;
import com.example.studentapp.Model.Member;
import com.example.studentapp.Remote.RestAPI;
import com.example.studentapp.Remote.RetrofitClient;
import com.google.android.material.navigation.NavigationView;

import java.util.Calendar;
import java.util.UUID;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class CreationPage extends AppCompatActivity {
    TextView tvDate;
    EditText titleView, descriptionView;

    int currentYear = Calendar.getInstance().get(Calendar.YEAR);
    int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
    int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

    private boolean errorFlag;

    private CompositeDisposable disposables;

    private ActionBarDrawerToggle toggle = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creation_page);
        disposables = new CompositeDisposable();
        Log.i(App.tag, "Creation userId = " + App.userId);

        tvDate = findViewById(R.id.tvDate);
        titleView = findViewById(R.id.title);
        descriptionView = findViewById(R.id.description);

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
                        case R.id.logout:
                            App.userId = null;
                            intent = new Intent(this, Login.class);
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

    public void createCourseHandler(View view) {
        errorFlag = false;

        validation(titleView.getText().toString(), descriptionView.getText().toString(), tvDate.getText().toString());

        if(!errorFlag){
            Course course = new Course(UUID.randomUUID().toString(),
                                       App.userId,
                                       titleView.getText().toString(),
                                       descriptionView.getText().toString(),
                                       tvDate.getText().toString());

            Member member = new Member(course.CourseId, App.userId);

            //Insert into local db
            App.getInstance().getDatabase().courseDao().insert(course);
            App.getInstance().getDatabase().memberDao().insert(member);
            Log.i(App.tag, "Insert into Course OK - Local DB");
            Log.i(App.tag, "Insert into Member OK - Local DB");

            //Insert into remote db
            disposables.add(App.getInstance().getRestAPI().addCourse(course)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(response -> {
                        Log.i(App.tag, response);
                    }, throwable -> Log.e(App.tag, throwable.getMessage())));

            disposables.add(App.getInstance().getRestAPI().addMember(member)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(response -> {
                        Log.i(App.tag, response);
                    }, throwable -> Log.e(App.tag, throwable.getMessage())));

            clearFields();
        }
        else
            return;
    }

    void clearFields() {
        titleView.getText().clear();
        descriptionView.getText().clear();
        tvDate.setText(null);
    }

    void validation(String title, String description, String date) {
        //Check the uniqueness of title
        Course course = App.getInstance().getDatabase().courseDao().getByTitle(title);
        if(course != null) {
            errorFlag = true;
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.unique_course), Toast.LENGTH_SHORT).show();
            return;
        }

        if(title.trim().length() == 0 || description.trim().length() == 0 || date.trim().length() == 0) {
            errorFlag = true;
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.empty_fields), Toast.LENGTH_SHORT).show();
            return;
        }
    }

    //region DatePicker
    public void datePickerHandler(View view) {
        showDialog(1);
    }

    protected Dialog onCreateDialog(int id) {
        if (id == 1) {
            DatePickerDialog tpd = new DatePickerDialog(this, myCallBack, currentYear, currentMonth, currentDay);
            return tpd;
        }
        return super.onCreateDialog(id);
    }

    DatePickerDialog.OnDateSetListener myCallBack = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            currentYear = year;
            currentMonth = monthOfYear;
            currentDay = dayOfMonth;
            tvDate.setText(currentDay + "." + currentMonth + "." + currentYear);
        }
    };
    //endregion
}
