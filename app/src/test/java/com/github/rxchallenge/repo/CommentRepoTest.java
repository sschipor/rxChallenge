package com.github.rxchallenge.repo;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.github.rxchallenge.TestUtils;
import com.github.rxchallenge.db.AppDB;
import com.github.rxchallenge.db.dao.CommentDao;
import com.github.rxchallenge.db.entity.Comment;
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
public class CommentRepoTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private ApiClient apiClient;
    @Mock
    private AppDB appDB;
    @Mock
    private CommentDao commentDao;
    @Mock
    private Executor executor;

    private CommentRepo commentRepo;
    private TestScheduler testScheduler;

    public CommentRepoTest() {
        MockitoAnnotations.initMocks(this);

        commentRepo = new CommentRepo(apiClient, appDB);

        testScheduler = new TestScheduler();
    }

    @Before
    public void setup() {
        //set rx test scheduler for same thread of execution
        commentRepo.processScheduler = testScheduler;
        commentRepo.androidScheduler = testScheduler;
        commentRepo.ioExecutor = executor;

        //handle mock ioExecutor for db void methods
        doAnswer((Answer<Object>) invocation -> {
            ((Runnable) invocation.getArguments()[0]).run();
            return null;
        }).when(executor).execute(any(Runnable.class));

        when(appDB.getCommentDao()).thenReturn(commentDao);
    }

    @Test
    public void test_Get_Comments_DB_API() {
        int postId = 1;

        List<Comment> comments = TestUtils.mockCommentsList();

        when(commentDao.getComments(postId)).thenReturn(Flowable.just(comments));
        when(apiClient.getComments(postId)).thenReturn(Single.just(comments));

        commentRepo.getComments(postId, new CompositeDisposable());
        testScheduler.triggerActions();

        verify(apiClient, atLeastOnce()).getComments(postId);
        verify(commentDao).insertAll(comments);
        verify(commentDao, atLeastOnce()).getComments(postId);
    }

    @Test
    public void test_Get_Comments_DB_Null_API() {
        int postId = 1;

        List<Comment> comments = TestUtils.mockCommentsList();

        when(commentDao.getComments(postId)).thenReturn(Flowable.just(comments));
        when(apiClient.getComments(postId)).thenReturn(null);

        commentRepo.getComments(postId, new CompositeDisposable());
        testScheduler.triggerActions();

        verify(apiClient, only()).getComments(postId);
        verify(commentDao, never()).insertAll(comments);
        verify(commentDao, atLeastOnce()).getComments(postId);
    }

    @Test(expected = NullPointerException.class)
    public void test_Get_Comments_Null_DB() {
        int postId = 1;

        List<Comment> comments = TestUtils.mockCommentsList();

        when(commentDao.getComments(postId)).thenReturn(null);
        when(apiClient.getComments(postId)).thenReturn(null);

        commentRepo.getComments(postId, new CompositeDisposable());
        testScheduler.triggerActions();

        verify(apiClient, never()).getComments(postId);
        verify(commentDao, never()).insertAll(comments);
        verify(commentDao, atLeastOnce()).getComments(postId);
    }
}
