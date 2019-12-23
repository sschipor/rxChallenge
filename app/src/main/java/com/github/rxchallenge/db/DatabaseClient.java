package com.github.rxchallenge.db;

import android.content.Context;

import androidx.room.Room;

/**
 * @author Sebastian Schipor
 */
public class DatabaseClient {

    private static DatabaseClient instance = null;
    private AppDB applicationDB;

    private DatabaseClient(Context context) {
        synchronized (this) {
            applicationDB = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDB.class,
                    "rxChallenge"
            ).build();
        }
    }

    public static DatabaseClient initDB(Context context) {
        if (instance == null) {
            instance = new DatabaseClient(context);
        }
        return instance;
    }

    public static DatabaseClient getInstance(){
        if (instance == null) {
            throw new IllegalStateException("Database Client is not initialized");
        }
        else return instance;
    }

    public AppDB getApplicationDB() {
        if (applicationDB == null) {
            throw new IllegalStateException("Database is not initialized");
        }
        return applicationDB;
    }
}
