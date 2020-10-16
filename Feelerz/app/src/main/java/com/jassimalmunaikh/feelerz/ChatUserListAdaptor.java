package com.jassimalmunaikh.feelerz;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.squareup.picasso.Picasso;

import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatUserListAdaptor extends ArrayAdapter<ChatUserData> implements ServerCallObserver, TopLevelFrag {

    Context mContext;
    private ArrayList<ChatUserData> mChat = null;
    private ArrayList<ChatUserData> mChatFiltered = null;
    FragmentManager fragmentManager;
    ChatFragment owner;
    RequestHandler requestHandler;

    public ChatUserListAdaptor(Context context, ArrayList<ChatUserData> mList, FragmentManager supportFragmentManager, ChatFragment chatFragment) {
        super(context, 0, mList);
        mContext = context;
        mChat = mList;
        mChatFiltered = mChat;
        this.fragmentManager = supportFragmentManager;
        this.owner = chatFragment;
    }


    public int getCount() {
        return mChatFiltered.size();//note the change
    }

    public ChatUserData getItem(int position) {
        return mChatFiltered.get(position);//note the change
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();

                if (constraint == null || constraint.length() == 0) {
                    //no constraint given, just return all the data. (no search)
                    results.count = mChat.size();
                    results.values = mChat;
                } else {//do the search
                    List<ChatUserData> resultsData = new ArrayList<>();
                    for(ChatUserData data : mChat)
                    {
                        if(data.userName.toLowerCase().contains(constraint.toString().toLowerCase()))
                        {
                            resultsData.add(data);
                        }
                    }
                    results.count = resultsData.size();
                    results.values = resultsData;
                }

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mChatFiltered = (ArrayList<ChatUserData>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    @androidx.annotation.NonNull
    public View getView(int position, @Nullable View convertView, @androidx.annotation.NonNull ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.chat_user_list, parent, false);

        ChatUserData data = mChatFiltered.get(position);
        owner.pullToRefresh.setRefreshing(false);


//        if (data.deleteby.equals(UserDataCache.GetInstance().id) || data.deleteby.equals("null")){
//            if (!data.profilePicId.isEmpty()) {
//                CircleImageView imageView = (CircleImageView) view.findViewById(R.id.ChatUserPic);
//                String url = mContext.getResources().getString(R.string.ImageURL) + data.profilePicId;
//                Picasso.get().load(url).into(imageView);
//                imageView.setVisibility(View.INVISIBLE);
//            }else {
                if (!data.profilePicId.isEmpty()) {
                    CircleImageView imageView = (CircleImageView) view.findViewById(R.id.ChatUserPic);
                    String url = mContext.getResources().getString(R.string.ImageURL) + data.profilePicId;
                    Picasso.get().load(url).into(imageView);
                }
//            }
//        }







// for notification post

        requestHandler = new RequestHandler(getContext(), this);

//            if (data.deleteby.equals(UserDataCache.GetInstance().id) || data.deleteby.equals("null")){
//                TextView UserName = (TextView) view.findViewById(R.id.ChatUserName);
//                UserName.setText(Html.fromHtml(StringEscapeUtils.unescapeJava(data.userName)));
//                UserName.setText("");
//                UserName.setVisibility(View.INVISIBLE);
//                Typeface Font = Typeface.createFromAsset(mContext.getAssets(), "fonts/Nunito-Bold.ttf");
//                UserName.setTypeface(Font);
//            }else {
                TextView UserName = (TextView) view.findViewById(R.id.ChatUserName);
                UserName.setText(Html.fromHtml(StringEscapeUtils.unescapeJava(data.userName)));
                Typeface Font = Typeface.createFromAsset(mContext.getAssets(), "fonts/Nunito-Bold.ttf");
                UserName.setTypeface(Font);
//            }



//        if (data.deleteby.equals(UserDataCache.GetInstance().id) || data.deleteby.equals("null")){
//            TextView UserLastMessage = (TextView) view.findViewById(R.id.UserLastMessage);
//            UserLastMessage.setText(data.lastMessage.contains("null") ? "" : Html.fromHtml(StringEscapeUtils.unescapeJava(data.lastMessage)));
//            UserLastMessage.setText("");
//            UserLastMessage.setVisibility(View.INVISIBLE);
            Typeface Font1 = Typeface.createFromAsset(mContext.getAssets(), "fonts/Nunito-Bold.ttf");
//            UserLastMessage.setTypeface(Font1);
//        }else {
            TextView UserLastMessage = (TextView) view.findViewById(R.id.UserLastMessage);
            UserLastMessage.setText(data.lastMessage.contains("null") ? "" : Html.fromHtml(StringEscapeUtils.unescapeJava(data.lastMessage)));
            Typeface Font33 = Typeface.createFromAsset(mContext.getAssets(), "fonts/Nunito-Bold.ttf");
            UserLastMessage.setTypeface(Font33);
//        }


//        if (data.deleteby.equals(UserDataCache.GetInstance().id) || data.deleteby.equals("null")){
//            TextView timeStamp = (TextView) view.findViewById(R.id.LastTimeofMessage);
//            timeStamp.setText(data.timeStamp);
//            timeStamp.setText("");
//            timeStamp.setVisibility(View.INVISIBLE);
//            Typeface Font2 = Typeface.createFromAsset(mContext.getAssets(), "fonts/Nunito-Bold.ttf");
//            timeStamp.setTypeface(Font2);
//        }
//        else {
            TextView timeStamp = (TextView) view.findViewById(R.id.LastTimeofMessage);
            timeStamp.setText(data.timeStamp);
            Typeface Font2 = Typeface.createFromAsset(mContext.getAssets(), "fonts/Nunito-Bold.ttf");
            timeStamp.setTypeface(Font2);
//        }


        SetupChatView(view, data);

        return view;
    }



    void SetupChatView(View view, final ChatUserData data1) {
        final ChatUserListAdaptor self = this;

        View r1 = view.findViewById(R.id.openChatOfUser);


            r1.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Get Chating from Chat list
                            Bundle data = new Bundle();
                            data.putString("userId", data1.userId);
                            data.putString("name", data1.userName);
                            data.putString("imageId", data1.profilePicId);
                            Fragment fragment = null;
                            fragment = new Chating_UserFragment();
                            fragment.setArguments(data);
                            FragmentManager fragmentManager = self.fragmentManager;
                            fragmentManager.beginTransaction().add(R.id.Main_Layout, fragment).addToBackStack(null).commit();
                        }
                    }
            );
            r1.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    //        SettextSytle();
                    // custom dialog
                    final Dialog dialog = new Dialog(getContext());
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.setContentView(R.layout.delete_message_list_popup);
                    dialog.setTitle("Message Delete");

                    TextView dialog_Title = dialog.findViewById(R.id.MsgDlt_Heading_txt);
                    TextView dialog_Msg = dialog.findViewById(R.id.MsgDlt_Confirm_txt);
                    TextView dialog_Cancel_btn = dialog.findViewById(R.id.cancel_BTN);
                    TextView dialog_Delete_btn = dialog.findViewById(R.id.Delete_BTN);

//        dialog_Title.setTypeface(myCustomFontt4);
//        dialog_Msg.setTypeface(myCustomFontt2);
//        dialog_Cancel_btn.setTypeface(myCustomFontt3);
//        dialog_Delete_btn.setTypeface(myCustomFontt3);

                    dialog_Msg.setText("Are you sure you want to delete " + Html.fromHtml(StringEscapeUtils.unescapeJava(data1.userName)) + " Messages ?");

                    dialog_Cancel_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            dialog.dismiss();
                        }
                    });
                    dialog_Delete_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Map<String, String> request = new HashMap<String, String>();
                            request.put("user_id", UserDataCache.GetInstance().id);
                            request.put("friend_id", data1.userId);
                            requestHandler.MakeRequest(request, "delete_all_chat");
//                OnManualClose();

                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                    return false;
                }
            });
        }





    @Override
    public void OnSuccess(JSONObject response, String apiName) {

        if (apiName == "delete_all_chat"){
            owner.populateChatFriendSection();

            /*try{
                Toast.makeText(mContext,"Messages deleted Successfully.", Toast.LENGTH_LONG).show();
            }catch (Exception e){}*/

        }

    }

    @Override
    public void OnFailure(String errorMessage, String apiName) {
        try{
            Toast.makeText(mContext,"Please Retry !!!", Toast.LENGTH_LONG).show();
        }catch (Exception e){}
    }

    @Override
    public void OnNetworkError() {

    }

    @Override
    public void OnManualClose() {

    }

    @Override
    public void Close() {

    }
}