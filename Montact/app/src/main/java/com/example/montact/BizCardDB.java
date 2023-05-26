package com.example.montact;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {BizCard.class}, version = 1)
public abstract class BizCardDB extends RoomDatabase {
    private static BizCardDB INSTANCE = null;

    public abstract BizCardDao bizCardDao();

    public static BizCardDB getInstance(Context context) {
        if(INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    BizCardDB.class, "bizcard.db").build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}