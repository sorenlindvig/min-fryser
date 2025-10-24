package com.example.fryserapp.data;

import androidx.room.*;
import java.util.List;

@Dao
public interface FreezerDao {
    @Insert long insert(FreezerItem item);
    @Update void update(FreezerItem item);
    @Delete void delete(FreezerItem item);
    @Query("SELECT * FROM items ORDER BY drawer ASC, addedDate DESC")
    List<FreezerItem> getAll();
}
