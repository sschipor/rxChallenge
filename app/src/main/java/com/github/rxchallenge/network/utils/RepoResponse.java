package com.github.rxchallenge.network.utils;

/**
 * @author Sebastian Schipor
 */
@SuppressWarnings({"unchecked"})
public class RepoResponse<T> {

    private Status status;
    private T data;
    private String errorMessage;

    private RepoResponse(Status status, T data, String errorMessage) {
        this.status = status;
        this.data = data;
        this.errorMessage = errorMessage;
    }

    public Status getStatus() {
        return status;
    }

    public T getData() {
        return data;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public static <T> RepoResponse loading() {
        return new RepoResponse(Status.LOADING, null, null);
    }

    public static <T> RepoResponse success(T data) {
        return new RepoResponse(Status.SUCCESS, data, null);
    }

    public static <T> RepoResponse error(String errorMessage) {
        return new RepoResponse(Status.ERROR, null, errorMessage);
    }
}
