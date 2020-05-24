package com.example.studentapp;

import android.app.Application;

import androidx.room.Room;

import com.example.studentapp.Database.AppDatabase;
import com.example.studentapp.Remote.RestAPI;
import com.example.studentapp.Remote.RetrofitClient;
import com.facebook.stetho.Stetho;

public class App extends Application {
    public static String userId;
    public static final String tag = "appLogs";

    public static App instance;
    private AppDatabase database;
    private RestAPI restAPI;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        database = Room.databaseBuilder(this, AppDatabase.class, "study_db")
                       .allowMainThreadQueries()
                       .build();
        restAPI = RetrofitClient.getInstance().create(RestAPI.class);

        Stetho.InitializerBuilder initializerBuilder =
                Stetho.newInitializerBuilder(this);
        initializerBuilder.enableWebKitInspector(
                Stetho.defaultInspectorModulesProvider(this)
        );
        initializerBuilder.enableDumpapp(
                Stetho.defaultDumperPluginsProvider(this)
        );
        Stetho.Initializer initializer = initializerBuilder.build();
        Stetho.initialize(initializer);
    }

    public static App getInstance() {
        return instance;
    }

    public AppDatabase getDatabase() {
        return database;
    }

    public RestAPI getRestAPI() {
        return restAPI;
    }
}
