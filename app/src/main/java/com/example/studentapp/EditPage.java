package com.example.studentapp;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.studentapp.Model.Course;
import com.example.studentapp.Model.Member;
import com.example.studentapp.Model.Student;
import com.example.studentapp.Remote.RestAPI;
import com.example.studentapp.Remote.RetrofitClient;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditPage extends AppCompatActivity {

    TextView tvDate;
    ListView usersList;
    EditText titleView, descriptionView;

    private boolean errorFlag;
    private List<String> names = new ArrayList();
    String courseId, title, description, date;

    int currentYear = Calendar.getInstance().get(Calendar.YEAR);
    int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
    int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

    ActionBarDrawerToggle toggle = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_page);
        Log.i(App.tag, "EditPage userId = " + App.userId);

        tvDate = findViewById(R.id.tvDate);
        titleView = findViewById(R.id.title);
        descriptionView = findViewById(R.id.description);
        usersList = findViewById(R.id.users_listView);

        courseId = getIntent().getStringExtra("COURSE_ID");
        title = getIntent().getStringExtra("TITLE");
        description = getIntent().getStringExtra("DESCRIPTION");
        date = getIntent().getStringExtra("DATE");

        titleView.setText(title);
        descriptionView.setText(description);
        tvDate.setText(date);

        prepareListView();

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

    public void updateCourseHandler(View view) {
        validation(titleView.getText().toString(), descriptionView.getText().toString(), tvDate.getText().toString());

        if(!errorFlag) {
            //Update in local db
            Course course = App.getInstance().getDatabase().courseDao().getById(courseId);
            course.Title = titleView.getText().toString();
            course.Description = descriptionView.getText().toString();
            course.Date = tvDate.getText().toString();

            App.getInstance().getDatabase().courseDao().update(course);
            Log.i(App.tag, "Update OK - Local DB");

            //Update in remote db
            Call<Course> call = App.getInstance().getRestAPI().updateCourse(courseId, course);
            call.enqueue(new Callback<Course>() {
                @Override
                public void onResponse(Call<Course> call, Response<Course> response) {
                    Log.i(App.tag, "Update OK - Remote DB");
                }

                @Override
                public void onFailure(Call<Course> call, Throwable throwable) {
                    Log.e(App.tag, throwable.getMessage());
                }
            });
        }
        else
            return;
    }

    public void deleteCourseHandler(View view) {
        //Delete from local db
        Course course = App.getInstance().getDatabase().courseDao().getById(courseId);
        App.getInstance().getDatabase().courseDao().delete(course);
        App.getInstance().getDatabase().memberDao().deleteAllFromCourse(courseId);
        Log.i(App.tag, "Delete from Course OK - Local DB");
        Log.i(App.tag, "Delete from Member OK - Local DB");

        //Delete from remote db
        try {
            Call<Void> call = App.getInstance().getRestAPI().deleteCourse(courseId);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    Log.i(App.tag, "Delete from Course OK - Remote DB");
                }

                @Override
                public void onFailure(Call<Void> call, Throwable throwable) {
                    Log.e(App.tag, throwable.getMessage());
                }
            });

            //ToDo delete all members instead of single
            call = App.getInstance().getRestAPI().deleteCourseMembers(courseId);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    Log.i(App.tag, response.message());
                }

                @Override
                public void onFailure(Call<Void> call, Throwable throwable) {
                    Log.e(App.tag, throwable.getMessage());
                }
            });
        }
        catch (Exception ex) {
            Log.e(App.tag, ex.getMessage());
        }
        onBackPressed();
    }

    private void prepareListView() {
        names.clear();
        List<Student> students = App.getInstance().getDatabase().studentDao().getByCourseTitle(title);

        if(students != null)
            for(int i = 0; i < students.size(); i++)
                names.add(students.get(i).Name);

        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, names);
        usersList.setAdapter(adapter);

        usersList.setOnItemClickListener((parent, view, position, id) -> {
            String name = (String) parent.getItemAtPosition(position);
            Student student = App.getInstance().getDatabase().studentDao().getByName(name);

            Intent detailPage = new Intent(view.getContext(), DetailPage.class);
            detailPage.putExtra("NAME", name);
            detailPage.putExtra("LOGIN", student.Login);
            detailPage.putExtra("MAIL", student.Mail);
            startActivity(detailPage);
        });
    }

    private void validation(String title, String description, String date) {
        if(title.trim().length() == 0 || description.trim().length() == 0 || date.trim().length() == 0) {
            errorFlag = true;
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.empty_fields), Toast.LENGTH_SHORT).show();
        }
    }

    //region datePicker
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
            currentMonth = monthOfYear + 1;
            currentDay = dayOfMonth;
            tvDate.setText(currentDay + "." + currentMonth + "." + currentYear);
        }
    };
    //endregion
}
