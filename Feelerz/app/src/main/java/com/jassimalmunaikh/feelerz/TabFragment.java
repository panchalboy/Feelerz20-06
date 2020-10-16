package com.jassimalmunaikh.feelerz;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class TabFragment extends Fragment implements ServerCallObserver {
    public static final String ARG_OBJECT = "object";
    String endPoint = "";
    Map<String,String> request = new HashMap<String,String>();

    CustomLoader loader = null;
    RequestHandler requestHandler;
    ListView listView;
    GenericListAdapter customAdapter = null;
    ArrayList<GenericListData> dataList = null;


    public TabFragment()
    {
        super();
    }

    public void SetState(Bundle args)
    {
        String state = args.getString("state");
        request.put("user_id",UserDataCache.GetInstance().id);

        if(state.equalsIgnoreCase("Following")) {
            endPoint = "following_list";
            request.put("friend_id",args.getString("targetId"));
        }
        else if(state.equalsIgnoreCase("Followers"))
        {
            endPoint = "follower_list";
            request.put("friend_id",args.getString("targetId"));
        }
        else if(state.equalsIgnoreCase("Hugs"))
        {
            endPoint = "total_hug_user_list";
            request.put("post_id",args.getString("postId"));
            request.put("page_no","0");
        }
        else if(state.equalsIgnoreCase("Refeels"))
        {
            endPoint = "total_refeel_user_list";
            request.put("post_id",args.getString("postId"));
            request.put("page_no","0");
        }
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.loader = new CustomLoader(getActivity(),(ViewGroup)getView().getRootView());

        dataList = new ArrayList<GenericListData>();
        listView = getView().findViewById(R.id.generic_list);
        customAdapter = new GenericListAdapter(getContext(),dataList,getFragmentManager(),this.loader);
        listView.setAdapter(customAdapter);

    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.generic_list_layout, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        SetState(args);
        requestHandler = new RequestHandler(getContext(),this);
        requestHandler.MakeRequest(request,endPoint);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void OnSuccess(JSONObject response, String apiName) {
        try
        {
            dataList.clear();
            JSONArray data = response.getJSONArray("data");
            for(int i = 0; i < data.length() ; i++)
            {
                JSONObject itemData = data.getJSONObject(i);
                GenericListData newData = new GenericListData();
                newData.id = itemData.getString("id");
                newData.imageId = itemData.getString("profileimage");
                newData.name = itemData.getString("name");
                int type = itemData.getInt("type");
                newData.isBeingFollowed = type == 1;
                dataList.add(newData);
            }
            customAdapter.notifyDataSetChanged();
        }
        catch (JSONException e)
        {

        }
    }

    public void showLoader()
    {

    }

    public void hideLoader()
    {

    }


    @Override
    public void OnFailure(String errorMessage, String apiName) {

    }

    @Override
    public void OnNetworkError() {

    }
}
