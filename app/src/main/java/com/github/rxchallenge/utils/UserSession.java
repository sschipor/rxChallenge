package com.github.rxchallenge.utils;

/**
 * @author Sebastian Schipor
 */
public class UserSession {

    private static UserSession instance;

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    private int userId = -1;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
