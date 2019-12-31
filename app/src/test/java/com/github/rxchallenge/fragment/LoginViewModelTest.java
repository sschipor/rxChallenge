package com.github.rxchallenge.fragment;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

/**
 * @author Sebastian Schipor
 */
public class LoginViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
    @Mock
    Observer<LoginViewModel.AuthenticationState> loginObserver;

    private LoginViewModel viewModel;

    public LoginViewModelTest() {
        MockitoAnnotations.initMocks(this);
    }

    @Before
    public void setUp() {
        viewModel = new LoginViewModel();
        viewModel.authenticationState.observeForever(loginObserver);
    }

    @Test
    public void test_Login_Attempt_Wrong() {
        //initial state is UNAUTHENTICATED
        assertEquals(LoginViewModel.AuthenticationState.UNAUTHENTICATED,
                viewModel.authenticationState.getValue());

        //attempt login
        viewModel.authenticate(-1);

        verify(loginObserver).onChanged(LoginViewModel.AuthenticationState.INVALID_AUTHENTICATION);
    }

    @Test
    public void test_Login_Attempt_Success() {
        //initial state is UNAUTHENTICATED
        assertEquals(LoginViewModel.AuthenticationState.UNAUTHENTICATED,
                viewModel.authenticationState.getValue());

        //attempt login
        viewModel.authenticate(1);

        verify(loginObserver).onChanged(LoginViewModel.AuthenticationState.AUTHENTICATED);
        assertEquals(1, viewModel.userId);
    }
}
