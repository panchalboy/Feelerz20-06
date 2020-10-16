package com.jassimalmunaikh.feelerz;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Placeholder;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.apache.commons.text.StringEscapeUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatUserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    Context mContext;
    private ArrayList<ChatUserData> mChat = null;
    private ArrayList<ChatUserData> mChatFiltered = null;
    FragmentManager fragmentManager;
    ChatFragment owner;
    RequestHandler requestHandler;
    private final int VIEW_TYPE_ITEM = 3;
    private final int VIEW_TYPE_LOADING = 4;

    public ChatUserAdapter(Context context, ArrayList<ChatUserData> mList, FragmentManager supportFragmentManager, ChatFragment chatFragment) {
        mContext = context;
        mChat = mList;
        mChatFiltered = mChat;
        this.fragmentManager = supportFragmentManager;
        this.owner = chatFragment;
    }



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
                    for (ChatUserData data : mChat) {
                        if (data.userName.toLowerCase().contains(constraint.toString().toLowerCase())) {
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


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_user_list, parent, false);
            return new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_layout_progress_bar, parent, false);
            return new LoadingViewHolder(view);
        }
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            ViewHolder postViewHolder = (ViewHolder) holder;
            LinearLayout.LayoutParams pm = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            View postView = postViewHolder.postView;
            postView.setLayoutParams(pm);
            ChatUserData data = mChatFiltered.get(position);

            CircleImageView imageView = (CircleImageView) postView.findViewById(R.id.ChatUserPic);

            if (!data.profilePicId.isEmpty()) {

                String url = mContext.getResources().getString(R.string.ImageURL) + data.profilePicId;
                Picasso.get().load(url).into(imageView);
            }else {
                Picasso.get().load(R.drawable.default_profile).into(imageView);

            }
// for notification post

          //  requestHandler = new RequestHandler(mContext, this);

            TextView UserName = (TextView) postView.findViewById(R.id.ChatUserName);
            UserName.setText(Html.fromHtml(StringEscapeUtils.unescapeJava(data.userName)));
          //  Typeface Font = Typeface.createFromAsset(mContext.getAssets(), "fonts/Nunito-Bold.ttf");
           // UserName.setTypeface(Font);

            TextView UserLastMessage = (TextView) postView.findViewById(R.id.UserLastMessage);
            UserLastMessage.setText(data.lastMessage.contains("null") ? "" : Html.fromHtml(StringEscapeUtils.unescapeJava(data.lastMessage)));
           // Typeface Font1 = Typeface.createFromAsset(mContext.getAssets(), "fonts/Nunito-Bold.ttf");
            //UserLastMessage.setTypeface(Font1);


            TextView timeStamp = (TextView) postView.findViewById(R.id.LastTimeofMessage);
            timeStamp.setText(data.timeStamp);
           // Typeface Font2 = Typeface.createFromAsset(mContext.getAssets(), "fonts/Nunito-Bold.ttf");
            //UserLastMessage.setTypeface(Font2);

            SetupChatView(postView, data);


        }

    }

    void SetupChatView(View view, final ChatUserData data1) {
        final ChatUserAdapter self = this;

        View r1 = view.findViewById(R.id.openChatOfUser);
        r1.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Get Chating from Chat list
                        Bundle data = new Bundle();
                        data.putString("userId",data1.userId);
                        data.putString("name",data1.userName);
                        data.putString("imageId",data1.profilePicId);
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

                final Dialog dialog = new Dialog(mContext);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setContentView(R.layout.delete_message_list_popup);
                dialog.setTitle("Message Delete");

                TextView dialog_Title = dialog.findViewById(R.id.MsgDlt_Heading_txt);
                TextView dialog_Msg = dialog.findViewById(R.id.MsgDlt_Confirm_txt);
                TextView dialog_Cancel_btn = dialog.findViewById(R.id.cancel_BTN);
                TextView dialog_Delete_btn = dialog.findViewById(R.id.Delete_BTN);


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
                        dialog.dismiss();
                    }
                });

                dialog.show();
                return false;
            }
        });


    }

    @Override
    public int getItemCount() {
        return mChatFiltered.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mChatFiltered.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        View postView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.postView = itemView;
        }
    }


    class LoadingViewHolder extends RecyclerView.ViewHolder {

        public LoadingViewHolder(View itemView) {
            super(itemView);
        }
    }

}
