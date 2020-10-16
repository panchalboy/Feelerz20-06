package com.jassimalmunaikh.feelerz;


import android.graphics.Typeface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
public class FeelingsPosts extends Fragment implements ServerCallObserver , TopLevelFrag {

    Typeface myCustomFontt1,myCustomFontt2,myCustomFontt3,myCustomFontt4;
    RequestHandler requestHandler;
    PostViewAdapter postViewAdapter;
    ArrayList<PostViewData> dataList;
    RecyclerView listView;

    public FeelingsPosts() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_feelings_posts, container, false);

        SettextSytle();

        this.requestHandler = new RequestHandler(getContext(),this);

        GlobalFragmentStack.getInstance().Register(this);

        dataList = new ArrayList<PostViewData>();

        FragmentManager manager = getActivity().getSupportFragmentManager();
        postViewAdapter = new PostViewAdapter(getContext(),dataList,manager,R.id.test_post_view,true);
        postViewAdapter.CreateLoader(this,view);
        listView =  view.findViewById(R.id.feelings_posts);
        listView.setLayoutManager(new LinearLayoutManager(getContext()));
        listView.setAdapter(postViewAdapter);

        DividerItemDecoration itemDecorator = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.divider));
        listView.addItemDecoration(itemDecorator);

        Button feel_btn = view.findViewById(R.id.Feel_layout_btn);
        feel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               OnManualClose();
            }
        });

        Button Back_Btn = view.findViewById(R.id.Back_Btn);
        Back_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnManualClose();
            }
        });

        TextView postTitle = view.findViewById(R.id.Feel_Title);
        postTitle.setTypeface(myCustomFontt4);
        String feelingName = getArguments().getString("feelingName");
        postTitle.setText(feelingName);
        Populate(feelingName);
        return view;

    }

    public void SettextSytle(){

        myCustomFontt1 = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Klavika-Regular.ttf");
        myCustomFontt2 = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Nunito-Bold.ttf");
        myCustomFontt3 = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Nunito-Regular.ttf");
        myCustomFontt4 = Typeface.createFromAsset(getActivity().getAssets(),"fonts/nunito-semibold.ttf");
    }

    public void Populate(String feelingName)
    {
        Map<String,String> request = new HashMap<String,String>();
        request.put("user_id", UserDataCache.GetInstance().id);
        request.put("feeling_name",feelingName);
        requestHandler.MakeRequest(request,"get_feeling_posts");
    }

    @Override
    public void OnSuccess(JSONObject response, String apiName) {
        try
        {
            dataList.clear();
            JSONArray posts = response.getJSONArray("data");

            for(int i =0 ; i < posts.length() ; i++)
            {
                JSONObject postData = posts.getJSONObject(i);
                PostViewData newPostData = new PostViewData();
                newPostData.SetData(postData);
                dataList.add(newPostData);
            }

            postViewAdapter.notifyDataSetChanged();

        }
        catch (JSONException e){}
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