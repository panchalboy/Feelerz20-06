package com.jassimalmunaikh.feelerz;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Ankit on 20 Jan 2018 020.
 */

public class SessionHandler
{
    private static final String PREF_NAME = "UserSession";
    private static final String KEY_DATA = "key_data";
    private static final String KEY_LOGIN_STATUS = "login_status";
    private static final String KEY_SOCIAL_LOGIN_TYPE = "social_login_type";

    private Context mContext = null;
    private SharedPreferences.Editor mEditor;
    private SharedPreferences mPreferences;
    private static SessionHandler instance = null;


    private SessionHandler()
    {

    }

    public static SessionHandler GetInstance()
    {
        if(instance == null)
            instance = new SessionHandler();
        return instance;
    }


    public void InitForCurrentContext(Context mContext) {
        this.mContext = mContext;
        mPreferences = mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.mEditor = mPreferences.edit();
    }


    public void onSuccessfullLogin(JSONObject object) {
        mEditor.putString(KEY_DATA,object.toString());
        mEditor.putBoolean(KEY_LOGIN_STATUS,true);
        if(GlobalCache.GetInstance().currentSocialLogin != null)
            mEditor.putInt(KEY_SOCIAL_LOGIN_TYPE,GlobalCache.GetInstance().currentSocialLogin.GetType());
        else
            mEditor.putInt(KEY_SOCIAL_LOGIN_TYPE,-1);
        mEditor.commit();
    }

    /**
     * Checks whether user is logged in
     *
     * @return
     */
    public boolean isLoggedIn() {
        boolean loginStatus = false;
        loginStatus = mPreferences.getBoolean(KEY_LOGIN_STATUS,false);
        return loginStatus;
    }

    public int GetSocialLoginType()
    {
        int loginStatus = -1;
        loginStatus = mPreferences.getInt(KEY_SOCIAL_LOGIN_TYPE,-1);
        return loginStatus;
    }

    public JSONObject getData()
    {
        String json = mPreferences.getString(KEY_DATA,"");
        JSONObject result = null;

        if(json != null)
        {
            try {
            result =  new JSONObject(json);
            }
            catch (JSONException e){}
        }
        return result;
    }

    public void logoutUser(){
        mEditor.putString(KEY_DATA,"");
        mEditor.putBoolean(KEY_LOGIN_STATUS,false);
        mEditor.putInt(KEY_SOCIAL_LOGIN_TYPE,-1);
        mEditor.commit();
    }

}
