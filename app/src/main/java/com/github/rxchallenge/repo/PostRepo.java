package com.github.rxchallenge.repo;

import androidx.lifecycle.LiveData;

import com.github.rxchallenge.db.AppDB;
import com.github.rxchallenge.db.DatabaseClient;
import com.github.rxchallenge.db.entity.Post;
import com.github.rxchallenge.fragment.PostsViewModel;
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
            PostsViewModel.ViewType viewType,
            CompositeDisposable disposable) {
        return new NetworkBoundResource<List<Post>>(disposable) {

            @Override
            public Flowable<List<Post>> loadFomDB() {
                if (viewType == PostsViewModel.ViewType.ALL) {
                    return appDB.getPostDao().getPosts(userId);
                } else {
                    return appDB.getPostDao().getFavoritePosts(userId);
                }
            }

            @Override
            public Boolean isApiCallRequired(List<Post> result) {
                //since fav is only in DB there is no need to make api call if this is selected
                return result == null || result.size() == 0 && viewType != PostsViewModel.ViewType.FAVORITE;
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

    public Completable updateFavorite(int postId, boolean isFavorite) {
        return appDB.getPostDao().updateFavorite(postId, isFavorite);
    }

    public LiveData<RepoResponse<Post>> getPostById(int postId, CompositeDisposable disposable) {
        return new NetworkBoundResource<Post>(disposable) {

            @Override
            public Flowable<Post> loadFomDB() {
                return appDB.getPostDao().getPostById(postId);
            }

            @Override
            public Boolean isApiCallRequired(Post result) {
                return result == null;
            }

            @Override
            public Single<Post> getApiCall() {
                return apiClient.getPostById(postId);
            }

            @Override
            public Completable saveResponse(final Post response) {
                return appDB.getPostDao().insert(response);
            }
        }.toLiveData();
    }
}
