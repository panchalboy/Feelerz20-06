package com.jassimalmunaikh.feelerz;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONObject;
import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;


/*

"id": "1407",
            "message": "Hiii",
            "send_by": "541",
            "status": "1",
            "date_time": "2020/01/28 11:43:01",
            "send_to": "426"

 */

class ChatData
{
    String id;
    String message;
    String send_by;
    String send_to;
    String date_time;
    String delete_by;
    String delete_from;
    int status;

    void SetId(String newId)
    {
        this.id = newId;
    }

    public void SetInfo(JSONObject data, Boolean hasId)
    {
        try {
            this.id = hasId ?  data.getString("id") : "-1";
            this.message = Html.fromHtml(StringEscapeUtils.unescapeJava(data.getString("message"))).toString();
            this.send_by = data.getString("send_by");
            this.send_to = data.getString("send_to");
            this.delete_by = data.getString("delete_by");
            this.delete_from = data.getString("delete_from");
            String date = data.getString("date_time");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date mDate = new Date();//2019-11-18 17:02:35
            long time = 0;
            try {
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                mDate = sdf.parse(date);
                time = mDate.getTime();
            } catch (ParseException e) {
                Log.e("ERROR_TIMESTAMP",e.getMessage());
            }

            PrettyTime p  = new PrettyTime();
            this.date_time = p.format(mDate);

            if(date_time.contains("moments")) {
                date_time = date_time.replace("moments ago", "now");
            }
            if(date_time.contains("hours") || date_time.contains("hour")) {
                date_time = date_time.replace("hours ago", "H");
                date_time = date_time.replace("hour ago", "H");
            }

            if(date_time.contains("minutes") || date_time.contains("minute")) {
                date_time = date_time.replace("minutes ago", "Min");
                date_time = date_time.replace("minute ago", "Min");
            }

            if(date_time.contains("days") || date_time.contains("day")) {
                date_time = date_time.replace("days ago", "D");
                date_time = date_time.replace("day ago", "D");
            }


            if(date_time.contains("weeks") || date_time.contains("week")) {
                date_time = date_time.replace("weeks ago", "Wk");
                date_time = date_time.replace("week ago", "Wk");
            }

            if(date_time.contains("months") || date_time.contains("month")) {
                date_time = date_time.replace("months ago", "Mth");
                date_time = date_time.replace("month ago", "Mth");
            }

            this.status = data.getInt("status");
        }
        catch (Exception e) {

        }
    }
}

public class ChatListAdapter extends ArrayAdapter<ChatData> implements DeleteConfirmListener {

    Context mContext;
    LayoutInflater mLayoutInflator;
    ArrayList<ChatData> mDataList;
    FragmentManager fragmentManager;
    CustomLoader loader;

    public ChatListAdapter(@NonNull Context context, @NonNull ArrayList<ChatData> objects,FragmentManager manager) {
        super(context, 0, objects);
        this.mContext = context;
        this.mDataList = objects;
        mLayoutInflator = LayoutInflater.from(mContext);
        this.fragmentManager = manager;
    }

    public void CreateLoader(Fragment owner, View view)
    {
        this.loader = new CustomLoader(owner.getActivity(),(ViewGroup)view);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = null;
        ChatData data = mDataList.get(position);
        String delete_from = data.delete_from;
        String delete_by = data.delete_by;
      /*  if (
                delete_from.equals("") && delete_by.equals("") ||
                delete_from.isEmpty() && delete_by.isEmpty())
        {*/
            if(UserDataCache.GetInstance().id.equals(data.send_by))
            {
                view = mLayoutInflator.inflate(R.layout.chat_right_layout, parent, false);
                SetupDeleteComment(view,data,position);
            }
            else
            {
                view = mLayoutInflator.inflate(R.layout.chat_left_layout, parent, false);
            }
        /*}
        else if(delete_by.equals(UserDataCache.GetInstance().id) || delete_from.equals("2")){
            view = mLayoutInflator.inflate(R.layout.chat_right_layout, parent, false);
//            SetupDeleteComment(view,data,position);
            view.setVisibility(View.INVISIBLE);
            view = mLayoutInflator.inflate(R.layout.chat_left_layout, parent, false);
            view.setVisibility(View.INVISIBLE);
        }else {
            if(UserDataCache.GetInstance().id.equals(data.send_by))
            {
                view = mLayoutInflator.inflate(R.layout.chat_right_layout, parent, false);
                SetupDeleteComment(view,data,position);
            }
            else
            {
                view = mLayoutInflator.inflate(R.layout.chat_left_layout, parent, false);
            }
        }*/




        TextView timeStamp = view.findViewById(R.id.time_stamp);
        timeStamp.setText(data.date_time);

        TextView message = view.findViewById(R.id.message);
        message.setText(StringEscapeUtils.unescapeJava(data.message));

        ImageView tick = view.findViewById(R.id.tick);

        if(data.status == 1)
            tick.setImageDrawable(mContext.getResources().getDrawable(R.drawable.single3));
        else if(data.status == 2)
            tick.setImageDrawable(mContext.getResources().getDrawable(R.drawable.double_tick2));

        return view;
    }

    void SetupDeleteComment(final View view, final ChatData data, int position)
    {
        RelativeLayout rl = view.findViewById(R.id.right_chat_rl);
        rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteConfirmDialog dialog = new DeleteConfirmDialog();
                Bundle args = new Bundle();
                args.putString("contentId",data.id);
                args.putInt("type",DeleteConfirmDialog.TYPE_DELETE_MESSAGE);
                args.putInt("position",position);
                dialog.setArguments(args);
                dialog.SetListener(ChatListAdapter.this);
                dialog.show(fragmentManager,"delete bs dialog");
            }
        });
    }

    @Override
    public void OnListItemDeletion(int position) {
        mDataList.remove(position);
        notifyDataSetChanged();
        this.loader.hide();
    }

    @Override
    public void OnDeletionInProgress() {
        this.loader.show();
    }

    @Override
    public void OnDeletionFailed() {
        this.loader.hide();
    }
}


