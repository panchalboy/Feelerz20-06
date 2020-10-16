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

public class NotificationData extends Fragment {
    String notificationId;
    String fromId ;
    String toId;
    String message;
    String Type;
    String postId;
    String ownerName;
    String profilePicId;
    String timeStamp;


    long timeStampInMS;

    public NotificationData()
    {
        this.timeStampInMS = 0;
        this.timeStamp = "";
        this.ownerName = "";
        this.toId = "";
        this.fromId="";
        this.message = "";
        this.postId ="";
        this.Type="";
        this.profilePicId = "";

    }


    void SetData(JSONObject data, Boolean self)
    {
        try {
            this.message = data.getString("message");
            this.ownerName = self? "You" : data.getString("name");
            this.toId = data.getString("to_id");
            this.fromId = data.getString("from_id");
            this.profilePicId = data.getString("profileimage");
            this.notificationId = data.getString("id");
            this.Type = data.getString("type");
            this.postId = data.getString("post_id");

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


        }
        catch(JSONException e)
        {

        }
    }
}
