package com.github.rxchallenge.repo;

import androidx.lifecycle.LiveData;

import com.github.rxchallenge.db.AppDB;
import com.github.rxchallenge.db.DatabaseClient;
import com.github.rxchallenge.db.entity.Comment;
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
public class CommentRepo {

    private ApiClient apiClient;
    private AppDB appDB = DatabaseClient.getInstance().getApplicationDB();

    public CommentRepo(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public LiveData<RepoResponse<List<Comment>>> getComments(
            final int postId,
            CompositeDisposable disposable) {
        return new NetworkBoundResource<List<Comment>, List<Comment>>(disposable) {

            @Override
            public Flowable<List<Comment>> loadFomDB() {
                return appDB.getCommentDao().getComments(postId);
            }

            @Override
            public Boolean isApiCallRequired(List<Comment> result) {
                return result == null || result.size() == 0;
            }

            @Override
            public Single<List<Comment>> getApiCall() {
                return apiClient.getComments(postId);
            }

            @Override
            public Completable saveResponse(final List<Comment> response) {
                return appDB.getCommentDao().insertAll(response);
            }
        }.toLiveData();
    }
}
