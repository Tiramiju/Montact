package com.example.montact;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface BizCardDao {
    @Query("SELECT * FROM bizcard")
    LiveData<List<BizCard>> getAll();

    @Query("SELECT * FROM bizcard WHERE id == :id")
    BizCard selectById(int id);

    @Insert
    void insertAll(BizCard... bizCards);

    @Delete
    void deleteContact(BizCard bizCard);

    @Update
    void updateContact(BizCard bizCard);
}
