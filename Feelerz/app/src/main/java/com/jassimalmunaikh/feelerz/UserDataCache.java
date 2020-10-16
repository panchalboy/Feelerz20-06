package com.jassimalmunaikh.feelerz;

import org.json.JSONException;
import org.json.JSONObject;

public class UserDataCache {

    //Login response

    String latestFCMToken = "";
    String currentFCMToken = "";

    String id;
    String name;
    String email;
    String imageId;
    String userName;
    String[] followers;
    String[] following;
    int age;

    String gender;
    String country;
    String contact;
    String dob;
    String bio;
    int totalRefeels;

    String Cid;
    String user_id;
    String date_time;
    String privacy_policy_content;



    private UserDataCache() {
        this.gender = "";
        int age = 0;
    }

    static public UserDataCache instance = null;

    static public UserDataCache GetInstance()
    {
        if(instance == null)
            instance = new UserDataCache();
        return instance;
    }

    void setforgetdata(JSONObject data) throws JSONException {
        this.email = (String) email;
        this.id = (String) data.get("id");
    }
    void setRegistrationdata(JSONObject data) throws JSONException{
        this.email = (String)email;
    }

    ////////////////////////////////////////////////////////
    void SetLoginData(JSONObject data) {
        try {
            this.id = (String) data.get("id");
            this.name = (String)data.get("name");
            this.email = (String)data.get("email");
            this.imageId = data.get("profileimage").toString();
            this.userName = data.get("userName").toString();

            this.gender = (String)data.get("gender").toString();
            this.country = (String)data.get("country").toString();
            this.contact = (String)data.get("contact").toString();
            this.dob = (String)data.get("dob").toString();
            this.bio = (String)data.get("bio").toString();

            String parsedData = data.getString("follower");
            this.followers = parsedData.split(",");
            this.followers = Utilities.getInstance().RemoveEmptyStrings(this.followers);

            parsedData = data.getString("following");
            this.following = parsedData.split(",");
            this.following = Utilities.getInstance().RemoveEmptyStrings(this.following);

            this.gender = data.getString("gender");
            this.bio = data.getString("bio");

            this.totalRefeels = data.getInt("total_post");
        }
        catch(JSONException exception)
        {

        }
    }

}
