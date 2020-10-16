package com.jassimalmunaikh.feelerz.social;

import android.app.Activity;
import android.widget.Button;


public interface SocialLogin {

    public static final int NONE = -1;
    public static final int GOOGLE = 0;
    public static final int FACEBOOK = 1;
    public static final int TWITTER = 2;
    public static final int SNAPCHAT = 3;


    int GetType();
    void Initialize(Activity activity, Button viewButton);
    void SignIn();
    void SignOut();
    void OnSignInSuccess();
    void OnSignInFailure();
    Boolean IsLoggedIn();
}
