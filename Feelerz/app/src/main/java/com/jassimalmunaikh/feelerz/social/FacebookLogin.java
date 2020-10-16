package com.jassimalmunaikh.feelerz.social;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FacebookLogin implements SocialLogin {

    private static final String EMAIL = "email";

    Activity mActivity;
    SocialLoginObserver mObserver = null;

    private CallbackManager callbackManager;


    public FacebookLogin(SocialLoginObserver observer)
    {
        mObserver = observer;
    }

    public CallbackManager GetCallbackManager(){return callbackManager;}
    @Override
    public int GetType() {
        return FACEBOOK;
    }

    public void GetUserData()
    {
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.v("LoginActivity", response.toString());

                        // Application code
                        try {
                            String email="";
                            if (object.has("email")) {
                                 email = object.getString("email");
                            }

                            Map<String, String> request = new HashMap<String, String>();
                            request.put("fname", Profile.getCurrentProfile().getFirstName());
                            request.put("lname", Profile.getCurrentProfile().getLastName());
                            request.put("email", email);
                            request.put("profileimage","");
                            request.put("mobile", "");
                            request.put("country", "");
                            request.put("password", "");
                            request.put("social_id", Profile.getCurrentProfile().getId());
                            request.put("types", "Facebook");

                            mObserver.OnLoginSuccess(request);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,gender,birthday");
        request.setParameters(parameters);
        request.executeAsync();


    }

    @Override
    public void Initialize(Activity activity, Button viewButton) {
            this.mActivity = activity;
            FacebookSdk.sdkInitialize(mActivity.getApplicationContext());
        //    AppEventsLogger.activateApp(mActivity);

        callbackManager = CallbackManager.Factory.create();

      LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        GetUserData();
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(mActivity.getApplicationContext(),"Login cancelled",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(mActivity.getApplicationContext(),"Login error : " + exception.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public void SignIn() {
        if(!IsLoggedIn())
            LoginManager.getInstance().logInWithReadPermissions(mActivity, Arrays.asList("public_profile","email"));
        else
            GetUserData();
    }

    @Override
    public void SignOut() {
        LoginManager.getInstance().logOut();
        LoginManager.getInstance().unregisterCallback(callbackManager);
        callbackManager = null;
    }

    @Override
    public void OnSignInSuccess() {

    }

    @Override
    public void OnSignInFailure() {

    }

    @Override
    public Boolean IsLoggedIn() {
            AccessToken accessToken = AccessToken.getCurrentAccessToken();
            return accessToken != null;
    }
}
