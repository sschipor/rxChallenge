package com.github.rxchallenge.fragment;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.github.rxchallenge.TestUtils;
import com.github.rxchallenge.db.entity.Comment;
import com.github.rxchallenge.db.entity.Post;
import com.github.rxchallenge.network.utils.RepoResponse;
import com.github.rxchallenge.network.utils.Status;
import com.github.rxchallenge.repo.CommentRepo;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Sebastian Schipor
 */
public class CommentsViewModelTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
    @Mock
    private CommentRepo commentRepo;
    @Mock
    private PostRepo postRepo;
    @Mock
    private Observer<RepoResponse<Post>> postObserver;
    @Captor
    ArgumentCaptor<RepoResponse<Post>> postCaptor;
    @Mock
    private Observer<RepoResponse<List<Comment>>> commentsObserver;
    @Captor
    ArgumentCaptor<RepoResponse<List<Comment>>> commentsCaptor;

    private CommentsViewModel viewModel;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    int postId = 1;

    public CommentsViewModelTest() {
        MockitoAnnotations.initMocks(this);
        viewModel = new CommentsViewModel(commentRepo, postRepo);
    }

    @Before
    public void setUp() {
        viewModel.compositeDisposable = compositeDisposable;
    }

    @Test
    public void test_Get_Post_By_Id_Loading() {
        LiveData<RepoResponse<Post>> response = TestUtils.mockGetPostByIdLoading();

        when(postRepo.getPostById(postId, compositeDisposable)).thenReturn(response);

        //add observer
        viewModel.getPost(postId).observeForever(postObserver);

        verify(postObserver).onChanged(postCaptor.capture());
        assertEquals(Status.LOADING, postCaptor.getValue().getStatus());
        assertNull(postCaptor.getValue().getData());
        assertNull(postCaptor.getValue().getErrorMessage());
    }

    @Test
    public void test_Get_Post_By_Id_Success() {
        LiveData<RepoResponse<Post>> response = TestUtils.mockGetPostByIdSuccess();

        when(postRepo.getPostById(postId, compositeDisposable)).thenReturn(response);

        //add observer
        viewModel.getPost(postId).observeForever(postObserver);

        verify(postObserver).onChanged(postCaptor.capture());
        assertEquals(Status.SUCCESS, postCaptor.getValue().getStatus());
        assertEquals(response.getValue().getData(), postCaptor.getValue().getData());
        assertNull(postCaptor.getValue().getErrorMessage());
    }

    @Test
    public void test_Get_Post_By_Id_Error() {
        LiveData<RepoResponse<Post>> response = TestUtils.mockGetPostByIdError();

        when(postRepo.getPostById(postId, compositeDisposable)).thenReturn(response);

        //add observer
        viewModel.getPost(postId).observeForever(postObserver);

        verify(postObserver).onChanged(postCaptor.capture());
        assertEquals(Status.ERROR, postCaptor.getValue().getStatus());
        assertNull(postCaptor.getValue().getData());
        assertEquals(TestUtils.ERROR_RESPONSE, postCaptor.getValue().getErrorMessage());
    }

    @Test
    public void test_Get_Comments_Loading(){
        LiveData<RepoResponse<List<Comment>>> response = TestUtils.mockGetCommentsLoading();

        when(commentRepo.getComments(postId,compositeDisposable)).thenReturn(response);

        //add observer
        viewModel.getComments(postId).observeForever(commentsObserver);

        verify(commentsObserver).onChanged(commentsCaptor.capture());

        assertEquals(Status.LOADING, commentsCaptor.getValue().getStatus());
        assertNull(commentsCaptor.getValue().getData());
        assertNull(commentsCaptor.getValue().getErrorMessage());
    }

    @Test
    public void test_Get_Comments_Success(){
        LiveData<RepoResponse<List<Comment>>> response = TestUtils.mockGetCommentsSuccess();

        when(commentRepo.getComments(postId,compositeDisposable)).thenReturn(response);

        //add observer
        viewModel.getComments(postId).observeForever(commentsObserver);

        verify(commentsObserver).onChanged(commentsCaptor.capture());

        assertEquals(Status.SUCCESS, commentsCaptor.getValue().getStatus());
        assertEquals(response.getValue().getData(), commentsCaptor.getValue().getData());
        assertNull(commentsCaptor.getValue().getErrorMessage());
    }

    @Test
    public void test_Get_Comments_Error(){
        LiveData<RepoResponse<List<Comment>>> response = TestUtils.mockGetCommentsError();

        when(commentRepo.getComments(postId,compositeDisposable)).thenReturn(response);

        //add observer
        viewModel.getComments(postId).observeForever(commentsObserver);

        verify(commentsObserver).onChanged(commentsCaptor.capture());

        assertEquals(Status.ERROR, commentsCaptor.getValue().getStatus());
        assertNull(commentsCaptor.getValue().getData());
        assertEquals(TestUtils.ERROR_RESPONSE, commentsCaptor.getValue().getErrorMessage());
    }

    @Test
    public void test_Update_Favorite_Post() {
        boolean isFav = false;

        viewModel.updateFavoritePost(postId, isFav);

        verify(postRepo).updateFavorite(postId, isFav);
    }

}
