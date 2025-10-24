package com.example.fryserapp;

import android.app.Application;
import androidx.room.Room;
import com.example.fryserapp.data.AppDatabase;

public class App extends Application {
    public static AppDatabase db;
    @Override public void onCreate() {
        super.onCreate();
        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "fryser-db").build();
    }
}
