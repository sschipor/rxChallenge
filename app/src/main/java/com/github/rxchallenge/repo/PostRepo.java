package com.github.rxchallenge.repo;

import androidx.lifecycle.LiveData;

import com.github.rxchallenge.db.AppDB;
import com.github.rxchallenge.db.DatabaseClient;
import com.github.rxchallenge.db.entity.Post;
import com.github.rxchallenge.network.ApiClient;
import com.github.rxchallenge.network.utils.NetworkBoundResource;
import com.github.rxchallenge.network.utils.RepoResponse;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;

/**
 * @author Sebastian Schipor
 */
public class PostRepo {
    private ApiClient apiClient;
    private AppDB appDB = DatabaseClient.getInstance().getApplicationDB();


    public PostRepo(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public LiveData<RepoResponse<List<Post>>> getPosts(
            final int userId,
            CompositeDisposable disposable) {
        return new NetworkBoundResource<List<Post>, List<Post>>(disposable) {

            @Override
            public Flowable<List<Post>> loadFomDB() {
                return appDB.getPostDao().getPosts(userId);
            }

            @Override
            public Boolean isApiCallRequired(List<Post> result) {
                return result == null || result.size() == 0;
            }

            @Override
            public Single<List<Post>> getApiCall() {
                return apiClient.getPosts(userId);
            }

            @Override
            public Completable saveResponse(final List<Post> response) {
                return appDB.getPostDao().insertAll(response);
            }
        }.toLiveData();
    }
}
