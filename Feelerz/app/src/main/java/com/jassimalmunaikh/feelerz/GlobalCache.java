package com.jassimalmunaikh.feelerz;

import android.view.ViewGroup;

import com.jassimalmunaikh.feelerz.social.SocialLogin;

import java.util.ArrayList;

public class GlobalCache {

    SocialLogin currentSocialLogin;
    ViewGroup customLoaderParent = null;
    public int postIndex;
    public String selectedCommentId;
    PostViewAdapter adapter = null;
    ArrayList<PostViewData> dataList;
    public int SuggestFriendEntryType = -1;
    public int FeelingIndex;
    ArrayList<String> FeelingString ;

    public GlobalCache(){}

    static private GlobalCache instance = null;

    static public GlobalCache GetInstance()
    {
        if(instance == null)
            instance = new GlobalCache();
        return instance;
    }

    public ArrayList<String> getFeelingString() {
        return FeelingString;
    }
}
