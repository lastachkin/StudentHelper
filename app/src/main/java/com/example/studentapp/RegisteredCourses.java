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

public class RegisteredCourses extends AppCompatActivity {
    ListView coursesList;
    boolean isFirstLoad = true;
    private ActionBarDrawerToggle toggle = null;
    private ArrayList courseTitles = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registered_courses);
        Log.i(App.tag, "RegisteredCourses userId = " + App.userId);

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

        List<Course> courses = App.getInstance().getDatabase().courseDao().registeredCourses(App.userId);

        for(int i = 0; i < courses.size(); i++)
            courseTitles.add(courses.get(i).Title);

        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, courseTitles);
        coursesList.setAdapter(adapter);

        coursesList.setOnItemClickListener((parent, view, position, id) -> {
            String title = (String) parent.getItemAtPosition(position);
            Course course = App.getInstance().getDatabase().courseDao().getByTitle(title);
            if(course.CreatorId.equals(App.userId)) {
                Intent editPage = new Intent(view.getContext(), EditPage.class);
                editPage.putExtra("COURSE_ID", course.CourseId);
                editPage.putExtra("TITLE", course.Title);
                editPage.putExtra("DESCRIPTION", course.Description);
                editPage.putExtra("DATE", course.Date);
                startActivity(editPage);
            }
            else {
                Intent unregPage = new Intent(view.getContext(), UnregCourse.class);
                unregPage.putExtra("COURSE_ID", course.CourseId);
                unregPage.putExtra("TITLE", course.Title);
                unregPage.putExtra("DESCRIPTION", course.Description);
                unregPage.putExtra("DATE", course.Date);
                startActivity(unregPage);
            }
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
