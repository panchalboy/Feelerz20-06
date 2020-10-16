package com.jassimalmunaikh.feelerz;

import android.content.Context;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import de.hdodenhof.circleimageview.CircleImageView;


class GenericListData
{
    public String id = "";
    public String name = "";
    public String imageId = "";
    Boolean isBeingFollowed = false;
}

public class GenericListAdapter extends ArrayAdapter<GenericListData> implements ServerCallObserver,UnfollowBlockListener{

    RequestHandler requestHandler;
    CustomLoader loader;
    FragmentManager fragmentManager;
    Context mContext;
    ArrayList<GenericListData> mItemList;
    int currentRequestPosition = -1;
    Boolean requestInProgress = false;

    public GenericListAdapter(@NonNull Context context, ArrayList<GenericListData> list, FragmentManager manager,CustomLoader pLoader)
    {
        super(context, 0 , list);
        mContext = context;
        mItemList = list;
        this.fragmentManager = manager;
        this.requestHandler = new RequestHandler(mContext,this);
        this.loader = pLoader;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.generic_profile_item_layout, parent, false);

        GenericListData data = mItemList.get(position);
        TextView name = view.findViewById(R.id.name);
        name.setText(Html.fromHtml(StringEscapeUtils.unescapeJava(data.name)));

        Typeface Font = Typeface.createFromAsset(mContext.getAssets(), "fonts/Nunito-Regular.ttf");
        name.setTypeface(Font);


        if (!data.imageId.isEmpty())
        {
            CircleImageView imageView = view.findViewById(R.id.profile_pic);
            String url = mContext.getResources().getString(R.string.ImageURL) + data.imageId;
            Picasso.get().load(url).into(imageView);
        }

        TextView follow_Block_btn = view.findViewById(R.id.follow_Block_btn);
        follow_Block_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otherOption(view,data.id,data.name);
            }
        });

        SetupUnfollowingButton(view,data,position);
        SetupProfileView(view,data.id);
        return view;
    }

    /*private void block_Unfollow(View view,final GenericListAdapter data)
    {
        final GenericListAdapter self = this;

                Bundle bundle = new Bundle();
                bundle.putString("blockId",data.id);
                Block_UnfollowConfirmDialog fragment = new Block_UnfollowConfirmDialog();
                fragment.setArguments(bundle);
                FragmentManager fragmentManager = self.fragmentManager;
                fragmentManager.beginTransaction().add(R.id.Main_Layout, fragment).addToBackStack(null).commit();

    }*/

    void otherOption(View view, String name, final String blockId)
    {
        final GenericListAdapter self = this;

                  Bundle data = new Bundle();
                  data.putString("userId",blockId);
                  data.putString("name",name);
                  Fragment fragment = null;
                  fragment = new Block_UnfollowConfirmDialog();
                  fragment.setArguments(data);
                  FragmentManager fragmentManager = self.fragmentManager;
                  fragmentManager.beginTransaction().add(R.id.Main_Layout, fragment).addToBackStack(null).commit();
    }


    void SetupProfileView(View view, final String userId)
    {
        final GenericListAdapter self = this;

        View r1 = view.findViewById(R.id.r1);
        r1.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(UserDataCache.GetInstance().id.equals(userId)) {
                            ((DashboardActivity)mContext).OpenMyProfile();
                        }
                        else
                        {
                            Bundle data = new Bundle();
                            data.putString("userId",userId);
                            Fragment fragment = null;
                            fragment = new OtherProfileFragment();
                            fragment.setArguments(data);
                            FragmentManager fragmentManager = self.fragmentManager;
                            fragmentManager.beginTransaction().add(R.id.Main_Layout, fragment).addToBackStack(null).commit();
                        }
                    }
                }
        );
    }

    void SetupUnfollowingButton(View view,GenericListData data,int position)
    {
        TextView followingBtn = view.findViewById(R.id.follow_Block_btn);
        followingBtn.setText(data.isBeingFollowed ? "Following" : "Follow");
        followingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String status = followingBtn.getText().toString();
                if(!data.isBeingFollowed) {
                    loader.show();
                    SendFollowRequest(data.id,position);
                }
                else
                {
                    OpenUnfollowBlockBS(data.id,position);
                }
            }
        });
    }

    void SendFollowRequest(String friendId,int position)
    {
        if(!requestInProgress ) {
            this.currentRequestPosition = position;
            this.requestInProgress = true;
            Map<String, String> request = new HashMap<String, String>();
            request.put("user_id", UserDataCache.GetInstance().id);
            request.put("friend_id", friendId);
            requestHandler.MakeRequest(request, "following");
        }
    }

    void OpenUnfollowBlockBS(String userId,int position)
    {
        UnfollowBlockDialog bs = new UnfollowBlockDialog();
        Bundle args = new Bundle();
        args.putString("userId",userId);
        args.putInt("position",position);
        bs.setArguments(args);
        bs.SetListener(this);
        bs.show(fragmentManager,"delete bs dialog");
    }


    @Override
    public void OnBlockDone(int position) {
        mItemList.remove(position);
        notifyDataSetChanged();
        loader.hide();
    }

    @Override
    public void OnUnfollowDone(int position) {
        mItemList.get(position).isBeingFollowed = false;
        this.notifyDataSetChanged();
        loader.hide();
    }

    @Override
    public void OnProcessing() {
        loader.show();
    }

    @Override
    public void OnSuccess(JSONObject response, String apiName) {
        if(apiName == "following")
        {
            mItemList.get(this.currentRequestPosition).isBeingFollowed = true;
            this.notifyDataSetChanged();
            this.currentRequestPosition = -1;
            this.requestInProgress = false;
        }
        loader.hide();
    }

    @Override
    public void OnFailure(String errorMessage, String apiName) {

    }

    @Override
    public void OnNetworkError() {

    }
}
