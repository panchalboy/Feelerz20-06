package com.jassimalmunaikh.feelerz;
import android.content.Context;
import android.graphics.Typeface;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class BlockedUserListAdaptor extends ArrayAdapter<BlockedUserData> implements ServerCallObserver {

    Context mContext;
    private ArrayList<BlockedUserData> mBlocked = null;
    FragmentManager fragmentManager;
    Blocked_User_ListFragment owner;
    RequestHandler requestHandler;
    CustomLoader loader;
    Blocked_User_ListFragment closeFragment;
    int currentDeletedItemIndex = -1;

    public BlockedUserListAdaptor(Context context, ArrayList<BlockedUserData> mList, FragmentManager supportFragmentManager, Blocked_User_ListFragment blocked_user_listFragment) {
        super(context,0,mList);
        mContext = context;
        mBlocked = mList;
        this.fragmentManager = supportFragmentManager;
        this.owner = owner;
        this.closeFragment = blocked_user_listFragment;
    }

    @androidx.annotation.NonNull
    public View getView(int position, @Nullable View convertView, @androidx.annotation.NonNull ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.blocked_user_list, parent, false);

        BlockedUserData data = mBlocked.get(position);

        if (!data.profilePicId.isEmpty()) {
            CircleImageView imageView = (CircleImageView) view.findViewById(R.id.blockedUserPic);
            String url = mContext.getResources().getString(R.string.ImageURL) + data.profilePicId;
            Picasso.get().load(url).into(imageView);
        }
// for notification post
        requestHandler = new RequestHandler(getContext(), this);
        this.loader = new CustomLoader(closeFragment.getActivity(),(ViewGroup)closeFragment.getView());

        TextView UserName = (TextView) view.findViewById(R.id.blockedUserName);
        UserName.setText(data.userName);
        Typeface Font = Typeface.createFromAsset(mContext.getAssets(), "fonts/Nunito-Bold.ttf");
        UserName.setTypeface(Font);

        TextView unblock_btn = view.findViewById(R.id.unblock_btn);
        Typeface Font1 = Typeface.createFromAsset(mContext.getAssets(), "fonts/Nunito-Bold.ttf");
        unblock_btn.setTypeface(Font1);

        unblock_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    loader.show();
                    currentDeletedItemIndex = position;
                    Map<String,String> request = new HashMap<String,String>();
                    request.put("user_id", UserDataCache.GetInstance().id);
                    request.put("block_id",data.userID);
                    requestHandler.MakeRequest(request,"unblock_user");

            }
        });
//        SetupBlockedView(view, data);

        return view;

    }

    @Override
    public void OnSuccess(JSONObject response, String apiName) {

         if(apiName == "unblock_user")
            {
                if(currentDeletedItemIndex != -1) {
                    this.mBlocked.remove(currentDeletedItemIndex);
                    this.notifyDataSetChanged();
                    currentDeletedItemIndex = -1;
                }
            }
         loader.hide();
    }

    @Override
    public void OnFailure(String errorMessage, String apiName) {
        loader.SetErrorColor();
    }

    @Override
    public void OnNetworkError() {

    }

}
