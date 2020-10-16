package com.jassimalmunaikh.feelerz;


import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
public class NotificationFragment extends Fragment implements ServerCallObserver {

    Typeface myCustomFontt1,myCustomFontt2,myCustomFontt3,myCustomFontt4;
    TextView Notification_Title;

    ListView mNotificationList;
    NotificationViewAdaptor mAdapter;
    ArrayList<NotificationData> mList;
    RequestHandler requestHandler;

    CustomLoader loader;

    SwipeRefreshLayout pullToRefresh;
    FragmentManager fragmentManager;
    Boolean isLoading;
    Integer currPageNo;

    public NotificationFragment() {
        // Required empty public constructor
        this.currPageNo = 0;
        this.isLoading = false;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_notification, container, false);

        SettextSytle();

        Notification_Title = view.findViewById(R.id.Notification_Title);
        Notification_Title.setTypeface(myCustomFontt4);

        this.loader = new CustomLoader(getActivity(),(ViewGroup)view);

        requestHandler = new RequestHandler(getContext(),this);

        mList = new ArrayList<NotificationData>();
        mAdapter = new NotificationViewAdaptor(getContext(),mList,getActivity().getSupportFragmentManager(),this);
        mAdapter.CreateLoader(this,view);
        this.mNotificationList = (ListView)view.findViewById(R.id.notifications);
        this.mNotificationList.setAdapter(mAdapter);

        this.pullToRefresh = view.findViewById(R.id.pull_to_refresh);
        this.pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currPageNo = 0;
                populateNotificationSection();
            }
        });

        populateNotificationSection();
        return view;
    }

    public void SettextSytle(){

        myCustomFontt1 = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Klavika-Regular.ttf");
        myCustomFontt2 = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Nunito-Bold.ttf");
        myCustomFontt3 = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Nunito-Regular.ttf");
        myCustomFontt4 = Typeface.createFromAsset(getActivity().getAssets(),"fonts/nunito-semibold.ttf");
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

        if(apiName == "get_notification") {
            mList.clear();
            try {
                JSONArray notifications = response.getJSONArray("data");

                for (int i = 0; i < notifications.length(); i++) {
                    JSONObject notification = notifications.getJSONObject(i);
                    NotificationData newNotificationtData = new NotificationData();
                    newNotificationtData.SetData(notification,false);
                    mList.add(newNotificationtData);
                    mAdapter.notifyDataSetChanged();
                }
            } catch (JSONException e) {
            }

        }
        this.pullToRefresh.setRefreshing(false);
        loader.hide();
    }

    public void populateNotificationSection()
    {
        loader.show();
        Map<String,String> request = new HashMap<String,String>();
        request.put("user_id", UserDataCache.GetInstance().id);
        requestHandler.MakeRequest(request,"get_notification");
    }


    @Override
    public void OnFailure(String errorMessage, String apiName) {
        loader.SetErrorColor();
    }

    @Override
    public void OnNetworkError() {
        noInternetPopUp();
        this.pullToRefresh.setRefreshing(false);
        loader.hide();
    }

}