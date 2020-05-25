package com.example.studentapp;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.studentapp.Model.Course;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class AvailableCourses extends AppCompatActivity {
    boolean isFirstLoad = true;
    private ListView coursesList;
    private ActionBarDrawerToggle toggle = null;
    private ArrayList courseTitles = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_courses);
        Log.i(App.tag, "AvailableCourses userId = " + App.userId);

        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.navView);
        coursesList = findViewById(R.id.listView);

        prepareListView();

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

    void prepareListView() {
        courseTitles.clear();

        List<Course> courses = App.getInstance().getDatabase().courseDao().availableCourses(App.userId);

        if(courses != null)
            for(int i = 0; i < courses.size(); i++)
                courseTitles.add(courses.get(i).Title);

        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, courseTitles);
        coursesList.setAdapter(adapter);

        coursesList.setOnItemClickListener((parent, view, position, id) -> {
            String title = (String) parent.getItemAtPosition(position);
            Course course = App.getInstance().getDatabase().courseDao().getByTitle(title);

            Intent regPage = new Intent(view.getContext(), RegCourse.class);
            regPage.putExtra("AV_COURSE_ID", course.CourseId);
            regPage.putExtra("AV_TITLE", course.Title);
            regPage.putExtra("AV_DESCRIPTION", course.Description);
            regPage.putExtra("AV_DATE", course.Date);
            startActivity(regPage);
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        if(!isFirstLoad) {
            prepareListView();
        }
        isFirstLoad = false;
    }
}
