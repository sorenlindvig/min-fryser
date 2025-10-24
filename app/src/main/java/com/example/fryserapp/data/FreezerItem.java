package com.example.fryserapp.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "items")
public class FreezerItem {
    @PrimaryKey(autoGenerate = true) public long id;
    public String name;
    public String quantity;
    public Long addedDate;
    public Long expiryDate;
    public int drawer;
    public FreezerItem(long id, String name, String quantity, Long addedDate, Long expiryDate, int drawer) {
        this.id=id; this.name=name; this.quantity=quantity; this.addedDate=addedDate; this.expiryDate=expiryDate; this.drawer=drawer;
    }

    public int getDrawer() {
        return drawer;
    }
}
