package com.jassimalmunaikh.feelerz;

import android.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

public class BlockedUserData extends Fragment {

    String profilePicId;
    String userName;
    String userID;

    public BlockedUserData(){
        this.profilePicId = "";
        this.userName = "";
        this.userID = "";

    }

    void SetData(JSONObject data, Boolean self){

        try{
            this.userName = data.getString("name");
            this.profilePicId = data.getString("profileimage");
            this.userID = data.getString("id");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
