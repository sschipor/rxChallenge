package com.github.rxchallenge.fragment;

import androidx.activity.OnBackPressedCallback;
import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.rxchallenge.R;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;

import rx.subscriptions.CompositeSubscription;


public class LoginFragment extends Fragment {

    private LoginViewModel mViewModel;
    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.login_fragment, container, false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mViewModel = ViewModelProviders.of(requireActivity()).get(LoginViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //set toolbar title
        getActivity().setTitle(R.string.title_login);

        final EditText userIdField = view.findViewById(R.id.userIdInput);
        Button btnLogin = view.findViewById(R.id.btnLogin);

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        requireActivity().finish(); // if user presses back, exit the app
                    }
                });

        compositeSubscription.add(
                RxView.focusChanges(userIdField).subscribe(hasFocus -> {
                    if (!hasFocus) {
                        //if user focused anywhere else on screen just close keyboard
                        hideKeyboard();
                    }
                })
        );

        compositeSubscription.add(
                RxTextView.textChanges(userIdField).subscribe(charSequence ->
                        btnLogin.setEnabled(charSequence.length() >= 1))
        );

        compositeSubscription.add(
                RxView.clicks(btnLogin).subscribe(aVoid -> {
                    String input = userIdField.getText().toString();
                    if (validateInput(input)) {
                        mViewModel.authenticate(Integer.valueOf(input));
                    }
                })
        );

        mViewModel.authenticationState.observe(getViewLifecycleOwner(), authenticationState -> {
            if (authenticationState == LoginViewModel.AuthenticationState.AUTHENTICATED) {
                Navigation.findNavController(view).popBackStack();
            } else if (authenticationState == LoginViewModel.AuthenticationState.INVALID_AUTHENTICATION) {
                Toast.makeText(getActivity(), R.string.invalid_credentials, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateInput(String input) {
        try {
            int id = Integer.valueOf(input);
            return id > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void hideKeyboard() {
        InputMethodManager im = ((InputMethodManager) getActivity()
                .getSystemService(Activity.INPUT_METHOD_SERVICE));
        if (im != null) {
            im.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeSubscription.unsubscribe();
    }
}
