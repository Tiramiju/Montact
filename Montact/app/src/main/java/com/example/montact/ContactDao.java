package com.example.montact;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface ContactDao {
    @Query("SELECT * FROM contact")
    LiveData<List<Contact>> getAll();

    @Query("SELECT * FROM contact WHERE id == :id")
    Contact selectById(int id);

    @Insert
    void insertAll(Contact... contacts);

    @Delete
    void deleteContact(Contact contact);

    @Update
    void updateContact(Contact contact);

    @Query("SELECT timeStamp FROM contact")
    List<String> getDates();

    @Query("SELECT * FROM contact WHERE timeStamp == :timeStamp")
    List<Contact> selectByDate(String timeStamp);
}
