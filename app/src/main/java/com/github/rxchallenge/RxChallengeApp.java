package com.github.rxchallenge;

import android.app.Application;

import com.github.rxchallenge.db.DatabaseClient;

/**
 * @author Sebastian Schipor
 */
public class RxChallengeApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //init db
        DatabaseClient.initDB(this);
    }
}
