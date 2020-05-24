package com.example.studentapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class Home extends AppCompatActivity {
    public static String userId;
    private ActionBarDrawerToggle toggle = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userId = getIntent().getStringExtra("USER_ID");

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
}
