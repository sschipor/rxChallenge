package com.github.rxchallenge.repo;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.github.rxchallenge.TestUtils;
import com.github.rxchallenge.db.AppDB;
import com.github.rxchallenge.db.dao.PostDao;
import com.github.rxchallenge.db.entity.Post;
import com.github.rxchallenge.fragment.PostsViewModel;
import com.github.rxchallenge.network.ApiClient;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;

import java.util.List;
import java.util.concurrent.Executor;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.TestScheduler;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Sebastian Schipor
 */
public class PostRepoTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private ApiClient apiClient;
    @Mock
    private AppDB appDB;
    @Mock
    private PostDao postDao;
    @Mock
    private Executor executor;

    private PostRepo postRepo;
    private TestScheduler testScheduler;

    public PostRepoTest() {
        MockitoAnnotations.initMocks(this);

        postRepo = new PostRepo(apiClient, appDB);

        testScheduler = new TestScheduler();
    }

    @Before
    public void setup() {
        //set rx test scheduler for same thread of execution
        postRepo.processScheduler = testScheduler;
        postRepo.androidScheduler = testScheduler;
        postRepo.ioExecutor = executor;

        //handle mock ioExecutor for db void methods
        doAnswer((Answer<Object>) invocation -> {
            ((Runnable) invocation.getArguments()[0]).run();
            return null;
        }).when(executor).execute(any(Runnable.class));

        when(appDB.getPostDao()).thenReturn(postDao);
    }

    @Test
    public void test_Get_Posts_ALL_DB_API() {
        int userId = 1;
        //test for all posts
        PostsViewModel.ViewType viewType = PostsViewModel.ViewType.ALL;
        CompositeDisposable disposable = new CompositeDisposable();
        List<Post> postList = TestUtils.mockPostList();

        when(postDao.getPosts(userId)).thenReturn(Flowable.just(postList));
        when(apiClient.getPosts(userId)).thenReturn(Single.just(postList));

        postRepo.getPosts(userId, viewType, disposable);
        testScheduler.triggerActions();

        verify(apiClient, atLeastOnce()).getPosts(userId);
        verify(postDao).insertAll(postList);
        verify(postDao).getPosts(userId);
    }

    @Test
    public void test_Get_Posts_ALL_DB_Null_API() {
        int userId = 1;
        PostsViewModel.ViewType viewType = PostsViewModel.ViewType.ALL;
        CompositeDisposable disposable = new CompositeDisposable();
        List<Post> postList = TestUtils.mockPostList();

        when(postDao.getPosts(userId)).thenReturn(Flowable.just(postList));
        //return null as api call
        when(apiClient.getPosts(userId)).thenReturn(null);

        postRepo.getPosts(userId, viewType, disposable);
        testScheduler.triggerActions();

        //for null api call, there will be no db insert
        verify(apiClient, only()).getPosts(userId);
        verify(postDao, never()).insertAll(postList);
        verify(postDao).getPosts(userId);
    }

    @Test(expected = NullPointerException.class)
    public void test_Get_Posts_ALL_Null_DB_Call() {
        int userId = 1;
        PostsViewModel.ViewType viewType = PostsViewModel.ViewType.ALL;
        CompositeDisposable disposable = new CompositeDisposable();
        List<Post> postList = TestUtils.mockPostList();

        //return null as db query
        when(postDao.getPosts(userId)).thenReturn(null);
        when(apiClient.getPosts(userId)).thenReturn(null);

        postRepo.getPosts(userId, viewType, disposable);
        testScheduler.triggerActions();

        //for null db query, a NPE will be thrown
        verify(apiClient, only()).getPosts(userId);
        verify(postDao, never()).insertAll(postList);
        verify(postDao, only()).getPosts(userId);
    }

    @Test
    public void test_Get_Posts_FAVORITE_DB() {
        int userId = 1;
        PostsViewModel.ViewType viewType = PostsViewModel.ViewType.FAVORITE;
        CompositeDisposable disposable = new CompositeDisposable();
        List<Post> postList = TestUtils.mockPostList();

        when(postDao.getFavoritePosts(userId)).thenReturn(Flowable.just(postList));

        postRepo.getPosts(userId, viewType, disposable);
        testScheduler.triggerActions();

        verify(apiClient, never()).getPosts(userId);
        verify(postDao, never()).insertAll(any());
        verify(postDao).getFavoritePosts(userId);
    }

    @Test
    public void test_Update_Favorite_Post() {
        int postId = 1;

        postRepo.updateFavorite(postId, true);

        verify(postDao).updateFavorite(postId, true);
    }

    @Test
    public void test_Get_Post_By_Id_DB_Only() {
        int postId = 1;

        Post post = new Post();
        when(postDao.getPostById(postId)).thenReturn(Flowable.just(post));

        postRepo.getPostById(postId, new CompositeDisposable());
        testScheduler.triggerActions();

        verify(postDao).getPostById(postId);
        verify(apiClient, never()).getPostById(1);
        verify(postDao, never()).insert(post);
    }
}
