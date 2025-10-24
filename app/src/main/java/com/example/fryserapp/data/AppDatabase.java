package com.example.fryserapp.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {FreezerItem.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract FreezerDao freezerDao();
}
