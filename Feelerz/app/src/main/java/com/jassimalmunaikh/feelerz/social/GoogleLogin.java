package com.jassimalmunaikh.feelerz.social;

import android.app.Activity;
import android.content.Intent;
import android.widget.Button;

import com.jassimalmunaikh.feelerz.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class GoogleLogin implements SocialLogin {

    GoogleSignInClient mClient;
    Activity mActivity;

    @Override
    public int GetType() {
        return GOOGLE;
    }

    @Override
    public void Initialize(Activity activity,Button viewButton) {

        this.mActivity = activity;
        // Configure sign-in to request the user's ID, email address, and basic
// profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(mActivity.getResources().getString(R.string.web_client_id))
                .requestProfile()
                .build();

        mClient = GoogleSignIn.getClient(activity.getApplicationContext(), gso);
    }

    @Override
    public void SignIn() {
        Intent signInIntent = mClient.getSignInIntent();
        mActivity.startActivityForResult(signInIntent,GOOGLE);
    }

    @Override
    public void SignOut() {
        mClient.signOut();
    }

    @Override
    public void OnSignInSuccess() {

    }

    @Override
    public void OnSignInFailure() {

    }

    @Override
    public Boolean IsLoggedIn() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(mActivity.getApplicationContext());
        return account != null;
    }
}
