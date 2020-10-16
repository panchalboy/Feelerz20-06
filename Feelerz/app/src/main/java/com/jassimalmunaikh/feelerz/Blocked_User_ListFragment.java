package com.jassimalmunaikh.feelerz;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class Blocked_User_ListFragment extends Fragment implements ServerCallObserver,TopLevelFrag{

    Typeface myCustomFontt1,myCustomFontt2,myCustomFontt3,myCustomFontt4;
    TextView blockUserList_Title;

    ListView mBlockedUserList;
    BlockedUserListAdaptor mAdapter;
    ArrayList<BlockedUserData> mList;

    RequestHandler requestHandler;
    CustomLoader loader;

    SwipeRefreshLayout pullToRefresh;
    Boolean isLoading;
    Integer currPageNo;

    public Blocked_User_ListFragment() {
        // Required empty public constructor
        this.currPageNo = 0;
        this.isLoading = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_blocked__user__list, container, false);

        SettextSytle();

        GlobalFragmentStack.getInstance().Register(this);
        this.loader = new CustomLoader(getActivity(),(ViewGroup)view);
        requestHandler = new RequestHandler(getContext(),this);

        blockUserList_Title = view.findViewById(R.id.blockUserList_Title);
        blockUserList_Title.setTypeface(myCustomFontt4);

        mList = new ArrayList<BlockedUserData>();
        mAdapter = new BlockedUserListAdaptor(getContext(),mList,getActivity().getSupportFragmentManager(),this);
        this.mBlockedUserList = (ListView)view.findViewById(R.id.blockedUserList);
        this.mBlockedUserList.setAdapter(mAdapter);

        Button Back_btn = (Button)view.findViewById(R.id.Back_Btn);
        Back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnManualClose();
            }
        });

        this.pullToRefresh = view.findViewById(R.id.pull_to_refresh);
        this.pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currPageNo = 0;
                populateBlockedSection();
            }
        });
        populateBlockedSection();

        return view;
    }

    public void populateBlockedSection()
    {
        loader.show();
        Map<String,String> request = new HashMap<String,String>();
        request.put("user_id", UserDataCache.GetInstance().id);
        requestHandler.MakeRequest(request,"block_user_list");
    }

    public void SettextSytle(){

        myCustomFontt1 = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Klavika-Regular.ttf");
        myCustomFontt2 = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Nunito-Bold.ttf");
        myCustomFontt3 = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Nunito-Regular.ttf");
        myCustomFontt4 = Typeface.createFromAsset(getActivity().getAssets(),"fonts/nunito-semibold.ttf");
    }

    public void CloseFragment(){
        OnManualClose();
    }

    private void noInternetPopUp(){
        final Dialog dialog = new Dialog(getContext());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.popup_no_internet);
        dialog.setTitle("No internet Connection");

        TextView dialog_OK_btn = (TextView) dialog.findViewById(R.id.OK_BTN);
        dialog_OK_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
    @Override
    public void OnSuccess(JSONObject response, String apiName) {
        if(apiName == "block_user_list") {
            mList.clear();
            try {
                JSONArray Blocks = response.getJSONArray("data");

                for (int i = 0; i < Blocks.length(); i++) {
                    JSONObject Block = Blocks.getJSONObject(i);
                    BlockedUserData newBlockedUserData = new BlockedUserData();
                    newBlockedUserData.SetData(Block,false);
                    mList.add(newBlockedUserData);
                    mAdapter.notifyDataSetChanged();
                }
            } catch (JSONException e) {
            }

        }
        this.pullToRefresh.setRefreshing(false);
        loader.hide();
    }

    @Override
    public void OnFailure(String errorMessage, String apiName) {
        loader.SetErrorColor();
        loader.hide();
    }

    @Override
    public void OnNetworkError() {
        noInternetPopUp();
        this.pullToRefresh.setRefreshing(false);
        loader.hide();
    }

    @Override
    public void OnManualClose() {
        GlobalFragmentStack.getInstance().Unregister(this);
        Close();
    }

    @Override
    public void Close() {
        getFragmentManager().beginTransaction().remove(this).commit();
    }
}
