package com.jassimalmunaikh.feelerz.social;

import android.app.Activity;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.snapchat.kit.sdk.SnapLogin;
import com.snapchat.kit.sdk.core.controller.LoginStateController;
import com.snapchat.kit.sdk.login.models.MeData;
import com.snapchat.kit.sdk.login.models.UserDataResponse;
import com.snapchat.kit.sdk.login.networking.FetchUserDataCallback;

import java.util.HashMap;
import java.util.Map;

public class SnapChatLogin implements SocialLogin {

    LoginStateController.OnLoginStateChangedListener mLoginStateChangedListener = null;
    Activity mActivity;
    SocialLoginObserver mObserver;

    public SnapChatLogin(SocialLoginObserver observer)
    {
        this.mObserver = observer;
    }


    @Override
    public int GetType() {
        return SNAPCHAT;
    }

    @Override
    public void Initialize(Activity activity, Button viewButton) {

        this.mActivity = activity;

         mLoginStateChangedListener =
                new LoginStateController.OnLoginStateChangedListener() {
                    @Override
                    public void onLoginSucceeded() {

                        GetUserData();

                    }

                    @Override
                    public void onLoginFailed() {
                        Toast.makeText(mActivity.getApplicationContext(),"Login Error",Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onLogout() {
                        Toast.makeText(mActivity.getApplicationContext(),"Login Error",Toast.LENGTH_LONG).show();
                    }
                };


        SnapLogin.getLoginStateController(activity.getApplicationContext()).addOnLoginStateChangedListener(mLoginStateChangedListener);



    }


    public void GetUserData()
    {
        String query = "{me{bitmoji{avatar},displayName,externalId}}";
        Map<String,Object> variables = new HashMap<String,Object>();
        SnapLogin.fetchUserData(mActivity, query, variables, new FetchUserDataCallback() {
            @Override
            public void onSuccess(@Nullable UserDataResponse userDataResponse) {
                if (userDataResponse == null || userDataResponse.getData() == null) {
                    return;
                }

                MeData meData = userDataResponse.getData().getMe();

                Map<String, String> request = new HashMap<String, String>();
                request.put("fname", meData.getDisplayName());
                request.put("lname", "");
                request.put("email", "");
                request.put("profileimage",meData.getBitmojiData().getAvatar());
                request.put("mobile", "");
                request.put("country", "");
                request.put("password", "");
                request.put("social_id", meData.getExternalId());
                request.put("types", "SnapChat");

                mObserver.OnLoginSuccess(request);

            }

            @Override
            public void onFailure(boolean isNetworkError, int statusCode) {
                Toast.makeText(mActivity.getApplicationContext(),"Login Error",Toast.LENGTH_LONG).show();

            }
        });
    }
    @Override
    public void SignIn() {
        if(IsLoggedIn())
            GetUserData();
        else
            SnapLogin.getAuthTokenManager(mActivity).startTokenGrant();
    }

    @Override
    public void SignOut() {
        SnapLogin.getAuthTokenManager(mActivity).revokeToken();
        SnapLogin.getLoginStateController(mActivity).removeOnLoginStateChangedListener(mLoginStateChangedListener);
    }

    @Override
    public void OnSignInSuccess() {

    }

    @Override
    public void OnSignInFailure() {

    }

    @Override
    public Boolean IsLoggedIn() {
        boolean isUserLoggedIn = SnapLogin.isUserLoggedIn(mActivity);
        return isUserLoggedIn;
    }
}
