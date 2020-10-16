package com.jassimalmunaikh.feelerz;

import android.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

public class SuggestedUserData extends Fragment {

    String profilePicId;
    String userName;
    String userID;

    public SuggestedUserData(){
        this.profilePicId = "";
        this.userName = "";
        this.userID = "";

    }

    void SetData(JSONObject data, Boolean self){

        try{
            this.userID = data.getString("user_id");
            this.userName = data.getString("name");
            this.profilePicId = data.getString("profileimage");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
