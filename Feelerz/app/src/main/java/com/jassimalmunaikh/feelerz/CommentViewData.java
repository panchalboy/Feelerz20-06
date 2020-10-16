package com.jassimalmunaikh.feelerz;

import android.text.Html;
import android.util.Log;

import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class CommentViewData {

    String commentId;
    String comment;
    String timeStamp;
    String ownerName;
    String ownerUserName;
    String ownerId;
    String profilePicId;
    long timeStampInMS;

    public CommentViewData()
    {
        this.timeStampInMS = 0;
        this.commentId = "";
        this.comment = "";
        this.timeStamp = "";
        this.ownerName = "";
        this.ownerUserName = "";
        this.ownerId = "";
        this.profilePicId = "";
    }

    void SetData(JSONObject data,Boolean self)
    {
        try {
            this.comment = Html.fromHtml(StringEscapeUtils.unescapeJava(data.getString("comment"))).toString();

            this.ownerName = self? UserDataCache.GetInstance().name : data.getString("name");
            this.ownerUserName = self?UserDataCache.GetInstance().userName : data.getString("userName");
            this.ownerId = data.getString("user_id");
            this.profilePicId = self? UserDataCache.GetInstance().imageId : data.getString("profileimage");
            this.commentId = data.getString("id");

            String dateAndTime = data.getString("date_time");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date mDate = new Date();//2019-11-18 17:02:35
            try {
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                mDate = sdf.parse(dateAndTime);
                this.timeStampInMS = mDate.getTime();
            } catch (ParseException e) {
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
