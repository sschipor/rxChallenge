package com.github.rxchallenge.fragment;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LoginViewModel extends ViewModel {
    public enum AuthenticationState {
        UNAUTHENTICATED,        // Initial state, the user needs to authenticate
        AUTHENTICATED,          // The user has authenticated successfully
        INVALID_AUTHENTICATION  // Authentication failed
    }

    final MutableLiveData<AuthenticationState> authenticationState =
            new MutableLiveData<>();
    int userId;

    public LoginViewModel() {
        // In this example, the user is always unauthenticated when MainActivity is launched
        authenticationState.setValue(AuthenticationState.UNAUTHENTICATED);
    }

    public void authenticate(int userId) {
        if (userId > 0) {
            this.userId = userId;
            authenticationState.setValue(AuthenticationState.AUTHENTICATED);
        } else {
            authenticationState.setValue(AuthenticationState.INVALID_AUTHENTICATION);
        }
    }
}
