package com.jassimalmunaikh.feelerz;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class Block_UnfollowConfirmDialog extends BottomSheetDialogFragment implements ServerCallObserver {

    RequestHandler requestHandler;

    public Block_UnfollowConfirmDialog() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.block__unfollow_confirm_dialog, container, false);

        this.requestHandler = new RequestHandler(getContext(),this);

        TextView blockuser = view.findViewById(R.id.BlockText);
        TextView unfollowUser = view.findViewById(R.id.UnfollowText);
        TextView Cancel_BTN = view.findViewById(R.id.Cancel_BTN);
        String otherUserName = getArguments().getString("name");
        blockuser.setText(otherUserName+" Block");
        unfollowUser.setText(otherUserName+" Unfollow");

        Cancel_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        blockuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Block_User();
            }
        });
        unfollowUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Unfollow_User();
            }
        });

        return view;
    }

    public void Block_User() {
//        loader.show();
        String blockUserID = getArguments().getString("userId");
        Map<String,String> request = new HashMap<String,String>();
        request.put("user_id", UserDataCache.GetInstance().id);
        request.put("block_id", blockUserID);
        requestHandler.MakeRequest(request,"block_users");
    }

    public void Unfollow_User() {
//        loader.show();
        String unfollowUserID = getArguments().getString("userId");
        Map<String,String> request = new HashMap<String,String>();
        request.put("user_id", UserDataCache.GetInstance().id);
        request.put("friend_id", unfollowUserID);
        requestHandler.MakeRequest(request,"unfollowing");
    }

    @Override
    public void OnSuccess(JSONObject response, String apiName) {

    }

    @Override
    public void OnFailure(String errorMessage, String apiName) {

    }

    @Override
    public void OnNetworkError() {

    }
}
