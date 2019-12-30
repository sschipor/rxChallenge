package com.github.rxchallenge.di;

import com.github.rxchallenge.db.AppDB;
import com.github.rxchallenge.db.DatabaseClient;
import com.github.rxchallenge.network.ApiClient;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author Sebastian Schipor
 */
public class InjectionHelper {

    private static ApiClient apiClient;
    private static InjectionHelper instance;

    private InjectionHelper() {
        apiClient = new Retrofit.Builder()
                .baseUrl("https://jsonplaceholder.typicode.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build().create(ApiClient.class);
    }

    public static InjectionHelper getInstance() {
        if (instance == null) {
            instance = new InjectionHelper();
        }
        return instance;
    }

    public ApiClient provideApiClient() {
        return apiClient;
    }

    public AppDB provideAppDatabase() {
        return DatabaseClient.getInstance().getApplicationDB();
    }
}
