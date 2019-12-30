package com.github.rxchallenge.repo;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.github.rxchallenge.AppExecutors;
import com.github.rxchallenge.db.AppDB;
import com.github.rxchallenge.db.DatabaseClient;
import com.github.rxchallenge.db.entity.Post;
import com.github.rxchallenge.fragment.PostsViewModel;
import com.github.rxchallenge.network.ApiClient;
import com.github.rxchallenge.network.utils.RepoBoundResource;
import com.github.rxchallenge.network.utils.RepoResponse;

import java.util.List;

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

    /**
     * Retrieve the posts for specific userId
     *
     * @param userId     the logged in user
     * @param viewType   handle API call and DB query based on selected ViewType
     *                   for ALL - make api call and query all DB for posts
     *                   for FAVORITE - make just DB query for favorite posts
     * @param disposable - the VM's composite disposable
     * @return livedata RepoResponse object
     */
    public LiveData<RepoResponse<List<Post>>> getPosts(
            final int userId,
            PostsViewModel.ViewType viewType,
            CompositeDisposable disposable) {
        return new RepoBoundResource<List<Post>>(disposable) {

            @Override
            public @NonNull
            Flowable<List<Post>> loadFomDB() {
                if (viewType == PostsViewModel.ViewType.ALL) {
                    return appDB.getPostDao().getPosts(userId);
                } else {
                    return appDB.getPostDao().getFavoritePosts(userId);
                }
            }

            @Override
            public Single<List<Post>> getApiCall() {
                if (viewType == PostsViewModel.ViewType.ALL) {
                    return apiClient.getPosts(userId);
                } else {
                    //for favorite posts return data only from DB
                    return null;
                }
            }

            @Override
            public void saveResponse(final List<Post> response) {
                AppExecutors.getInstance().ioThread(() -> appDB.getPostDao().insertAll(response));
            }
        }.toLiveData();
    }

    /**
     * Update the favorite post in DB. It will run in a global single thread executor
     *
     * @param postId     the id of the post
     * @param isFavorite boolean flag to be updated
     */
    public void updateFavorite(int postId, boolean isFavorite) {
        AppExecutors.getInstance().ioThread(() -> appDB.getPostDao().updateFavorite(postId, isFavorite));
    }

    /**
     * Return the post by ID from DB; no network call is required
     *
     * @param postId     the id of the post
     * @param disposable VM's disposable
     * @return livedata RepoResponse object
     */
    public LiveData<RepoResponse<Post>> getPostById(int postId, CompositeDisposable disposable) {
        return new RepoBoundResource<Post>(disposable) {

            @Override
            public @NonNull
            Flowable<Post> loadFomDB() {
                return appDB.getPostDao().getPostById(postId);
            }

            @Override
            public Single<Post> getApiCall() {
                return null; //return the post from DB
            }

            @Override
            public void saveResponse(final Post response) {
                AppExecutors.getInstance().ioThread(() -> appDB.getPostDao().insert(response));
            }
        }.toLiveData();
    }
}
