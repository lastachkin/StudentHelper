package com.example.studentapp;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

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
import com.example.studentapp.Remote.RestAPI;
import com.example.studentapp.Remote.RetrofitClient;
import com.google.android.material.navigation.NavigationView;

import io.reactivex.disposables.CompositeDisposable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UnregCourse extends AppCompatActivity {

    TextView tvDate;
    EditText titleView, descriptionView;
    String courseId, title, description, date;

    ActionBarDrawerToggle toggle = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unreg_course);

        courseId = getIntent().getStringExtra("COURSE_ID");
        title = getIntent().getStringExtra("TITLE");
        description = getIntent().getStringExtra("DESCRIPTION");
        date = getIntent().getStringExtra("DATE");

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

    public void unsubscribeCourseHandler(View view) {
        //Delete from local DB
        Member member = App.getInstance().getDatabase().memberDao().getById(courseId, App.userId);
        App.getInstance().getDatabase().memberDao().delete(member);
        Log.i(App.tag, "Delete from Member OK - Local DB");

        //Delete from remote DB
        Call<Void> call = App.getInstance().getRestAPI().deleteMember(courseId, App.userId);
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
        onBackPressed();
    }
}
