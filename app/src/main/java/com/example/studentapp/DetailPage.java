package com.example.studentapp;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

public class DetailPage extends AppCompatActivity {
    ActionBarDrawerToggle toggle = null;
    EditText nameView, loginView;
    private String name, login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_page);

        name = getIntent().getStringExtra("NAME");
        login = getIntent().getStringExtra("LOGIN");

        nameView = findViewById(R.id.name);
        loginView = findViewById(R.id.login);

        nameView.setText(name);
        loginView.setText(login);

        nameView.setEnabled(false);
        loginView.setEnabled(false);

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
}
