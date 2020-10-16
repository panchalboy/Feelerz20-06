package com.jassimalmunaikh.feelerz;

import android.content.Context;
import android.graphics.Typeface;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SuggestedUserListAdaptor extends ArrayAdapter<SuggestedUserData>  implements ServerCallObserver{

    Context mContext;
    private ArrayList<SuggestedUserData> mSuggested = null;
    FragmentManager fragmentManager;
    Suggested_FriendFragment owner;
    RequestHandler requestHandler;

    int lastFollowPosition = -1;
    Boolean followingInProgress = false;

    public SuggestedUserListAdaptor(Context context, ArrayList<SuggestedUserData> mList, FragmentManager supportFragmentManager, Suggested_FriendFragment suggested_friendFragment) {
        super(context, 0, mList);
        mContext = context;
        mSuggested = mList;
        this.fragmentManager = supportFragmentManager;
        this.owner = owner;

    }


    @androidx.annotation.NonNull
    public View getView(int position, @Nullable View convertView, @androidx.annotation.NonNull ViewGroup parent)
    {
        View view = LayoutInflater.from(mContext).inflate(R.layout.suggested_user_list, parent, false);

        SuggestedUserData data = mSuggested.get(position);

        if (!data.profilePicId.isEmpty()) {
            CircleImageView imageView = (CircleImageView) view.findViewById(R.id.SuggestedUserPic);
            String url = mContext.getResources().getString(R.string.ImageURL) + data.profilePicId;
            Picasso.get().load(url).into(imageView);
        }
// for notification post

        requestHandler = new RequestHandler(getContext(), this);

        TextView UserName = (TextView) view.findViewById(R.id.SuggestedUserName);
        UserName.setText(Html.fromHtml(StringEscapeUtils.unescapeJava(data.userName)));
//        UserName.setText(data.userName);
        Typeface Font = Typeface.createFromAsset(mContext.getAssets(), "fonts/Nunito-Bold.ttf");
        UserName.setTypeface(Font);

        /*TextView timeStamp = (TextView) view.findViewById(R.id.notification_time);
        timeStamp.setText(data.timeStamp);
        timeStamp.setTypeface(Font);*/
/*
        ImageView more_option = (ImageView) view.findViewById(R.id.notification_option);
        more_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeleteConfirmDialog bs = new DeleteConfirmDialog();
                Bundle args = new Bundle();
                args.putInt("type", DeleteConfirmDialog.TYPE_DELETE_NOTIFICATION);
                args.putString("contentId", data.notificationId);
                args.putInt("position", position);
                bs.setArguments(args);
                bs.SetListener(NotificationViewAdaptor.this);
                bs.show(fragmentManager, "delete bs dialog");
            }
        });*/

        //Also needed to implement imageView for delete or some more options
        SetupProfileView(view, data);
        SetupFollowingButton(view,data,position);

        return view;

    }


    void SetupFollowingButton(View view, SuggestedUserData data,int position)
    {

        Button followingBtn = view.findViewById(R.id.Follow_BTN);
        followingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    SendFollowRequest(data.userID, position);

            }
        });
    }

    void SendFollowRequest(String userId,int position)
    {
        if(!this.followingInProgress) {
            this.lastFollowPosition = position;
            this.followingInProgress = true;
            Map<String, String> request = new HashMap<String, String>();
            request.put("user_id", UserDataCache.GetInstance().id);
            request.put("friend_id", userId);
            requestHandler.MakeRequest(request, "following");
        }
    }

    void SetupProfileView(View view, final SuggestedUserData data) {
        final SuggestedUserListAdaptor self = this;

        View r1 = view.findViewById(R.id.ViewProfile);
        r1.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle args = new Bundle();
                        args.putString("userId",data.userID);
                        Fragment fragment = null;
                        fragment = new OtherProfileFragment();
                        fragment.setArguments(args);
                        FragmentManager fragmentManager = self.fragmentManager;
                        fragmentManager.beginTransaction().add(R.id.Main_Layout, fragment).addToBackStack(null).commit();

                    }
                }
        );
    }


    @Override
    public void OnSuccess(JSONObject response, String apiName) {
            mSuggested.remove(lastFollowPosition);
            notifyDataSetChanged();
            this.followingInProgress = false;
            this.lastFollowPosition = -1;
    }

    @Override
    public void OnFailure(String errorMessage, String apiName) {

    }

    @Override
    public void OnNetworkError() {

    }
}
