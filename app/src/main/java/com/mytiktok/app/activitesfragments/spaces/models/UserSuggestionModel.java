package com.mytiktok.app.activitesfragments.spaces.models;

import com.mytiktok.app.models.UserModel;

import java.io.Serializable;

public class UserSuggestionModel implements Serializable {
    UserModel userModel;

    public UserSuggestionModel() {
    }

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }
}
