package com.jassimalmunaikh.feelerz;

import android.app.Fragment;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class ChatUserData extends Fragment {
    String profilePicId;
    String userName;
    String lastMessage;
    String timeStamp;
    String userId;
    String deleteby;
    long message_count;

    long timeStampInMS;

    public ChatUserData(){
        this.profilePicId = "";
        this.userName = "";
        this.userId = "";
        this.deleteby = "";
        this.lastMessage = "";
        this.timeStampInMS = 0;
        this.timeStamp = "";
        this.message_count = 0;
    }
    void SetData(JSONObject data){

        try{
            this.userName = data.getString("name");
            this.profilePicId = data.getString("profileimage");
            this.lastMessage = data.getString("last_message");
            this.userId = data.getString("user_id");
            this.deleteby = data.getString("deleteby");
            this.message_count = data.getInt("message_count");

            String dateAndTime = data.getString("date_time");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date mDate = new Date();//2019-11-18 17:02:35
            try {
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                mDate = sdf.parse(dateAndTime);
                this.timeStampInMS = mDate.getTime();
            } catch (ParseException e){
                Log.e("ERROR_TIMESTAMP",e.getMessage());
            }

            PrettyTime p  = new PrettyTime();
            this.timeStamp = p.format(mDate);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
