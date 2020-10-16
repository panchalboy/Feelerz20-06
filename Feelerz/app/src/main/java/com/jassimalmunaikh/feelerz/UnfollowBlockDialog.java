package com.jassimalmunaikh.feelerz;

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

interface UnfollowBlockListener
{
    public void OnBlockDone(int position);
    public void OnUnfollowDone(int position);
    public void OnProcessing();
}

public class UnfollowBlockDialog extends BottomSheetDialogFragment implements ServerCallObserver {
    OtherProfileFragment otherProfileFragment;
    UnfollowBlockListener mListener = null;
    String endPoint = "";
    RequestHandler requestHandler;
    TextView BlockText;
    TextView UnfollowText;
    int mPosition;
    String userId;
    Button dismissButton;
    ProfileVIewCache userData;

    public void SetListener(UnfollowBlockListener listener)
    {
        this.mListener = listener;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View parent = (View)getView().getParent();
        parent.setBackgroundColor(Color.TRANSPARENT);
  }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.block__unfollow_confirm_dialog, container, false);

        this.userData = new ProfileVIewCache();
        this.otherProfileFragment = new OtherProfileFragment();
        this.requestHandler = new RequestHandler(getActivity(), this);
        this.mPosition = getArguments().getInt("position",-1);
        userId = getArguments().getString("userId","");

        if(mPosition == -1 || userId.isEmpty()) {
            Toast.makeText(getContext(),"please provide data position to delete",Toast.LENGTH_SHORT).show();
            dismiss();
            return v;
        }

        this.BlockText =  v.findViewById(R.id.BlockText);
        this.UnfollowText =  v.findViewById(R.id.UnfollowText);
        this.dismissButton = v.findViewById(R.id.Cancel_BTN);

        this.dismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        this.BlockText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null) {
                    mListener.OnProcessing();
                }
                dismiss();
                Map<String,String> request = new HashMap<String,String>();
                request.put("user_id",UserDataCache.GetInstance().id);
                request.put("block_id",userId);
                requestHandler.MakeRequest(request,"block_users");
            }
        });

        this.UnfollowText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if(mListener != null) {
                    mListener.OnProcessing();
                }
                Map<String,String> request = new HashMap<String,String>();
                request.put("user_id",UserDataCache.GetInstance().id);
                request.put("friend_id",userId);
                requestHandler.MakeRequest(request,"unfollowing");
            }
        });


        return v;
    }

    /*public void clear()
    {
        for(Fragment fragment : fragmentList)
            fragmentManager.beginTransaction().remove(fragment).commit();
        fragmentList.clear();
        titlelist.clear();
    }*/


    public Window getWindow() {
        return null;
    }

    @Override
    public void OnSuccess(JSONObject response, String apiName) {

        if(apiName == "block_users") {
            if (mListener != null) {
                mListener.OnBlockDone(mPosition);
            }
        }
        else if(apiName == "unfollowing")
        {
            if(mListener != null)
//                otherProfileFragment.ShowUserData();
                mListener.OnUnfollowDone(mPosition);

//            TextView followBtn = mainView.findViewById(R.id.follow_btn);
//            followBtn.setEnabled(true);
//            int total = userData.following.length;
//            userData.UnfollowThisUser();
//            followBtn.setText("Follow");
//            followBtn.setTypeface(myCustomFontt3);
//                otherProfileFragment.pullToRefresh.setRefreshing();
        }
    }

    @Override
    public void OnFailure(String errorMessage, String apiName) {
        Toast.makeText(getContext(),"Failed to delete..",Toast.LENGTH_LONG).show();
        dismiss();
    }

    @Override
    public void OnNetworkError() {

    }

}