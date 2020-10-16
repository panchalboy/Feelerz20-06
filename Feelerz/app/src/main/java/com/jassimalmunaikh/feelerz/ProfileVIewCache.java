package com.jassimalmunaikh.feelerz;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProfileVIewCache {
    //Login response
    String id;
    String name;
    String email;
    String imageId;
    String userName;
    String[] followers;
    String[] following;
    String[] blockedByOthers;
    String gender;
    int age;
    String bio;
    String dob;
    int type;
    int totalFeels;


    public ProfileVIewCache() {
        this.gender = "";
        int age = 0;
        this.type = -1;
    }


    void UnfollowThisUser()
    {
        List<String> list = new ArrayList<String>(Arrays.asList(this.followers));
        list.remove(UserDataCache.GetInstance().id);
        this.followers = list.toArray(new String[0]);

    }

    void UnblockThisUser()
    {
        List<String> list = new ArrayList<String>(Arrays.asList(this.blockedByOthers));
        list.remove(UserDataCache.GetInstance().id);
        this.blockedByOthers = list.toArray(new String[0]);
    }

    void setforgetdata(JSONObject data) throws JSONException {
        this.email = (String) email;
        this.id = (String) data.get("id");
    }

    ////////////////////////////////////////////////////////
    void SetLoginData(JSONObject data)
    {
        try {
            this.id = (String) data.get("id");
            this.name = (String)data.get("name");
            this.email = (String)data.get("email");
            this.imageId = data.get("profileimage").toString();
            this.userName = data.get("userName").toString();

            String parsedData = data.getString("follower");
            this.followers = parsedData.split(",");
            this.followers = Utilities.getInstance().RemoveEmptyStrings(this.followers);

            parsedData = data.getString("following");
            this.following = parsedData.split(",");
            this.following = Utilities.getInstance().RemoveEmptyStrings(this.following);

            parsedData = data.getString("block_by_other");
            this.blockedByOthers = parsedData.split(",");
            this.blockedByOthers = Utilities.getInstance().RemoveEmptyStrings(this.blockedByOthers);

            this.gender = data.getString("gender");
            this.bio = data.getString("bio");

            this.dob = data.getString("dob");

            this.type = data.getInt("type");

            this.totalFeels = data.getInt("total_post");
        }
        catch(JSONException exception)
        {

        }
    }
}
