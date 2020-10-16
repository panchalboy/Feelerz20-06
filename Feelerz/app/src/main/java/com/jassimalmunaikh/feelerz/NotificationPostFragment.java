package com.jassimalmunaikh.feelerz;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NotificationPostFragment extends Fragment implements ServerCallObserver,TopLevelFrag{


    TextView UserName;
    TextView text;

    PostViewAdapter mAdapter;
    RequestHandler requestHandler;
    RecyclerView listView;
    ArrayList<PostViewData> dataList;
    String postId ;


    public NotificationPostFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_notification_post, container, false);

        GlobalFragmentStack.getInstance().Register(this);

        requestHandler = new RequestHandler(getContext(),this);

        FragmentManager manager = getActivity().getSupportFragmentManager();
        this.postId = getArguments().getString("post_id");

        dataList = new ArrayList<PostViewData>();
        mAdapter = new PostViewAdapter(getContext(), dataList, manager,R.id.notification_posts_id, true);
        mAdapter.CreateLoader(this,view);
        listView =  (RecyclerView) view.findViewById(R.id.single_post);
        listView.setAdapter(mAdapter);
        listView.setLayoutManager(new LinearLayoutManager(getContext()));


        populatePostNotificationSection(postId);

        Button back = view.findViewById(R.id.Back_Btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnManualClose();
            }
        });

        return view;
    }


    public void populatePostNotificationSection(String postId){
        Map<String,String> request = new HashMap<String,String>();
        request.put("post_id", postId);
        requestHandler.MakeRequest(request,"get_one_post_by_id");
    }

    @Override
    public void OnSuccess(JSONObject response, String apiName) {
        if (apiName == "get_one_post_by_id") {
            dataList.clear();
            try {
                JSONObject jsonObject = response.getJSONObject("data");
                PostViewData newData = new PostViewData();
                newData.SetData(jsonObject);
                dataList.add(newData);

            } catch (JSONException e) {
            }
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void OnFailure(String errorMessage, String apiName) {
    }

    @Override
    public void OnNetworkError() {

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
