package com.github.rxchallenge.fragment;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.github.rxchallenge.TestUtils;
import com.github.rxchallenge.db.entity.Post;
import com.github.rxchallenge.network.utils.RepoResponse;
import com.github.rxchallenge.network.utils.Status;
import com.github.rxchallenge.repo.PostRepo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Sebastian Schipor
 */
public class PostsViewModelTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private PostRepo postRepo;
    @Mock
    private Observer<PostsViewModel.ViewType> viewTypeObserver;
    @Mock
    private Observer<RepoResponse<List<Post>>> postListObserver;
    @Captor
    ArgumentCaptor<RepoResponse<List<Post>>> postsCaptor;

    private PostsViewModel viewModel;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private int userId = 11;

    public PostsViewModelTest() {
        MockitoAnnotations.initMocks(this);

        viewModel = new PostsViewModel(postRepo);
    }

    @Before
    public void setUp() {
        //set fields in VM
        viewModel.compositeDisposable = compositeDisposable;
        viewModel.setUserId(userId);

        viewModel.getViewType().observeForever(viewTypeObserver);
        viewModel.postsResult.observeForever(postListObserver);
    }

    @Test
    public void test_ViewModel_UserID() {
        assertEquals(userId, viewModel.userId);
    }

    @Test
    public void test_ViewTypeChanged_All() {
        PostsViewModel.ViewType viewType = PostsViewModel.ViewType.ALL;

        viewModel.onViewTypeChanged(viewType);

        verify(viewTypeObserver, atLeastOnce()).onChanged(PostsViewModel.ViewType.ALL);
    }

    @Test
    public void test_ViewTypeChanged_Fav() {
        PostsViewModel.ViewType viewType = PostsViewModel.ViewType.FAVORITE;

        viewModel.onViewTypeChanged(viewType);

        verify(viewTypeObserver, atLeastOnce()).onChanged(PostsViewModel.ViewType.FAVORITE);
    }

    @Test
    public void test_Update_Favorite_Post() {
        int postId = 1;
        boolean isFav = false;

        viewModel.updateFavoritePost(postId, isFav);

        verify(postRepo).updateFavorite(postId, isFav);
    }

    @Test
    public void test_Get_Posts_Loading() {
        PostsViewModel.ViewType viewType = PostsViewModel.ViewType.ALL;
        LiveData<RepoResponse<List<Post>>> response = TestUtils.mockGetPostsResponseLoading();

        when(postRepo.getPosts(userId, viewType, compositeDisposable)).thenReturn(response);


        viewModel.onViewTypeChanged(viewType);

        verify(postRepo, atLeastOnce()).getPosts(userId, viewType, compositeDisposable);
        verify(postListObserver, atLeastOnce()).onChanged(postsCaptor.capture());

        assertEquals(Status.LOADING, postsCaptor.getValue().getStatus());
        assertNull(postsCaptor.getValue().getData());
        assertNull(postsCaptor.getValue().getErrorMessage());
    }

    @Test
    public void test_Get_Posts_Success() {
        PostsViewModel.ViewType viewType = PostsViewModel.ViewType.FAVORITE;
        LiveData<RepoResponse<List<Post>>> response = TestUtils.mockGetPostsResponseSuccess();

        when(postRepo.getPosts(userId, viewType, compositeDisposable)).thenReturn(response);


        viewModel.onViewTypeChanged(viewType);

        verify(postRepo, atLeastOnce()).getPosts(userId, viewType, compositeDisposable);
        verify(postListObserver, atLeastOnce()).onChanged(postsCaptor.capture());

        assertEquals(Status.SUCCESS, postsCaptor.getValue().getStatus());
        //mocked data has no favorite posts
        assertEquals(0, postsCaptor.getValue().getData().size());
        assertNull(postsCaptor.getValue().getErrorMessage());
    }

    @Test
    public void test_Get_Posts_Error() {
        PostsViewModel.ViewType viewType = PostsViewModel.ViewType.ALL;
        LiveData<RepoResponse<List<Post>>> response = TestUtils.mockGetPostsResponseError();

        when(postRepo.getPosts(userId, viewType, compositeDisposable)).thenReturn(response);


        viewModel.onViewTypeChanged(viewType);

        verify(postRepo, atLeastOnce()).getPosts(userId, viewType, compositeDisposable);
        verify(postListObserver, atLeastOnce()).onChanged(postsCaptor.capture());

        assertEquals(Status.ERROR, postsCaptor.getValue().getStatus());
        assertNull(postsCaptor.getValue().getData());
        assertEquals(TestUtils.ERROR_RESPONSE, postsCaptor.getValue().getErrorMessage());
    }
}
