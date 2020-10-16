package com.jassimalmunaikh.feelerz;

import android.graphics.Typeface;
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
public class Suggested_FriendFragment extends Fragment implements ServerCallObserver,TopLevelFrag {

    Typeface myCustomFontt1,myCustomFontt2,myCustomFontt3,myCustomFontt4;
    TextView SuggestedFriend_Title,Skip_Btn;
    Button Back_btn;

    ListView mSuggestedUserList;
    SuggestedUserListAdaptor mAdapter;
    ArrayList<SuggestedUserData> mList;

    RequestHandler requestHandler;
    CustomLoader loader;

    SwipeRefreshLayout pullToRefresh;
    Boolean isLoading;
    Integer currPageNo;

    public Suggested_FriendFragment() {
        // Required empty public constructor
        this.currPageNo = 0;
        this.isLoading = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_suggested__friend, container, false);

        GlobalFragmentStack.getInstance().Register(this);
        SettextSytle();

        this.loader = new CustomLoader(getActivity(),(ViewGroup)view);
        requestHandler = new RequestHandler(getContext(),this);

        mList = new ArrayList<SuggestedUserData>();
        mAdapter = new SuggestedUserListAdaptor(getContext(),mList,getActivity().getSupportFragmentManager(),this);
        this.mSuggestedUserList = (ListView)view.findViewById(R.id.SuggestedUserList);
        this.mSuggestedUserList.setAdapter(mAdapter);

        SuggestedFriend_Title = view.findViewById(R.id.SuggestedFriend_Title);
        SuggestedFriend_Title.setTypeface(myCustomFontt4);

        Skip_Btn = view.findViewById(R.id.Skip_Btn);
        Skip_Btn.setTypeface(myCustomFontt4);
        Back_btn = (Button)view.findViewById(R.id.Back_Btn);

//        boolean shouldSuggestFriends = (GlobalCache.GetInstance().SuggestFriendEntryType == 0);

        Boolean shouldShowBackButton = GlobalCache.GetInstance().SuggestFriendEntryType == 1;
        Boolean shouldShowSkipButton = GlobalCache.GetInstance().SuggestFriendEntryType == 0;


        Back_btn.setVisibility(shouldShowBackButton ? View.VISIBLE : View.INVISIBLE);
        Skip_Btn.setVisibility(shouldShowSkipButton ? View.VISIBLE : View.INVISIBLE);
        Back_btn.setEnabled(shouldShowBackButton);
        Skip_Btn.setEnabled(shouldShowSkipButton);
        if(shouldShowBackButton)
        {
            Back_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OnManualClose();
                }
            });
        }

        if(shouldShowSkipButton)
        {
            Skip_Btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OnManualClose();
                }
            });
        }


        this.pullToRefresh = view.findViewById(R.id.pull_to_refresh);
        this.pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currPageNo = 0;
                populateSuggestedFriendSection();
            }
        });
        populateSuggestedFriendSection();

        return view;
    }
    public void SettextSytle(){

        myCustomFontt1 = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Klavika-Regular.ttf");
        myCustomFontt2 = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Nunito-Bold.ttf");
        myCustomFontt3 = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Nunito-Regular.ttf");
        myCustomFontt4 = Typeface.createFromAsset(getActivity().getAssets(),"fonts/nunito-semibold.ttf");
    }

    public void populateSuggestedFriendSection() {
        loader.show();
        Map<String,String> request = new HashMap<String,String>();
        request.put("user_id", UserDataCache.GetInstance().id);
        String u = "u";
        String n = "";
        request.put("more",u);
        request.put("search_text",n);
        requestHandler.MakeRequest(request,"search_friend");
    }

    @Override
    public void OnSuccess(JSONObject response, String apiName) {
        if(apiName == "search_friend") {
            mList.clear();
            try {
                JSONArray Suggestions = response.getJSONArray("users");

                for (int i = 0; i < Suggestions.length(); i++) {
                    JSONObject Suggestion = Suggestions.getJSONObject(i);
                    SuggestedUserData newSuggestedUserData = new SuggestedUserData();
                    newSuggestedUserData.SetData(Suggestion,false);
                    mList.add(newSuggestedUserData);
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

    }

    @Override
    public void OnManualClose() {
        GlobalFragmentStack.getInstance().Unregister(this);
        GlobalCache.GetInstance().SuggestFriendEntryType = -1;
        Close();
    }

    @Override
    public void Close() {
        getFragmentManager().beginTransaction().remove(this).commit();
    }
}