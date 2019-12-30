package com.github.rxchallenge.repo;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.github.rxchallenge.AppExecutors;
import com.github.rxchallenge.db.AppDB;
import com.github.rxchallenge.db.entity.Comment;
import com.github.rxchallenge.network.ApiClient;
import com.github.rxchallenge.network.utils.RepoBoundResource;
import com.github.rxchallenge.network.utils.RepoResponse;

import java.util.List;
import java.util.concurrent.Executor;

import io.reactivex.Flowable;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Sebastian Schipor
 */
public class CommentRepo {

    private ApiClient apiClient;
    private AppDB appDB;

    Scheduler processScheduler = Schedulers.io();
    Scheduler androidScheduler = AndroidSchedulers.mainThread();
    Executor ioExecutor = AppExecutors.getInstance().ioThread();

    public CommentRepo(ApiClient apiClient, AppDB appDB) {
        this.apiClient = apiClient;
        this.appDB = appDB;
    }

    /**
     * Retrieve the comments for specific post id
     *
     * @param postId     the id of the post
     * @param disposable VM's composite disposable
     * @return livedata RepoResponse object
     */
    public LiveData<RepoResponse<List<Comment>>> getComments(
            final int postId,
            CompositeDisposable disposable) {
        return new RepoBoundResource<List<Comment>>(disposable, processScheduler, androidScheduler) {

            @Override
            public @NonNull
            Flowable<List<Comment>> loadFomDB() {
                return appDB.getCommentDao().getComments(postId);
            }

            @Override
            public Single<List<Comment>> getApiCall() {
                return apiClient.getComments(postId);
            }

            @Override
            public void saveResponse(final List<Comment> response) {
                ioExecutor.execute(() -> appDB.getCommentDao().insertAll(response));
            }
        }.toLiveData();
    }
}
