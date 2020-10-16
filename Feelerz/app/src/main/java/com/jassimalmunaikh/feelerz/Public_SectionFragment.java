package com.jassimalmunaikh.feelerz;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
public class Public_SectionFragment extends Fragment implements  ServerCallObserver {

    RequestHandler requestHandler;
    PostViewAdapter postViewAdapter;
    ArrayList<PostViewData> dataList;
    RecyclerView listView;
    Boolean isLoading;
    Integer currPageNo;
    SwipeRefreshLayout pullToRefresh;

    public Public_SectionFragment() {
        this.currPageNo = 0;
        this.isLoading = false;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_public__section, container, false);

        this.requestHandler = new RequestHandler(getContext(),this);

        dataList = new ArrayList<PostViewData>();

        FragmentManager manager = getActivity().getSupportFragmentManager();
        postViewAdapter = new PostViewAdapter(getContext(),dataList,manager,R.id.public_posts_view_id,true);
        postViewAdapter.CreateLoader(this,view);
        listView =  (RecyclerView) view.findViewById(R.id.public_posts);
        listView.setAdapter(postViewAdapter);
        listView.setLayoutManager(new LinearLayoutManager(getContext()));

        DividerItemDecoration itemDecorator = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.divider));
        listView.addItemDecoration(itemDecorator);

        this.pullToRefresh = view.findViewById(R.id.pull_to_refresh);
        this.pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currPageNo = 0;
                Populate();
            }
        });

        Populate();

        initScrollListener();

        return view;

    }

    public void Populate()
    {
        Map<String,String> request = new HashMap<String,String>();
        request.put("page_no", currPageNo.toString());
        requestHandler.MakeRequest(request,"get_public_posts");

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
    public void OnSuccess(final JSONObject response, String apiName) {

        if(isLoading)
        {
            try {
                JSONArray posts = response.getJSONArray("data");
                if(posts == null)
                {
                    isLoading = false;
                    dataList.remove(dataList.size() - 1);
                    postViewAdapter.notifyItemRemoved(dataList.size());
                    return;
                }

                dataList.remove(dataList.size() - 1);
                int scrollPosition = dataList.size();
                postViewAdapter.notifyItemRemoved(scrollPosition);
                int currentSize = scrollPosition;

                for (int i = 0; i < posts.length(); i++) {
                    JSONObject postData = posts.getJSONObject(i);
                    PostViewData newPostData = new PostViewData();
                    newPostData.SetData(postData);
                    dataList.add(newPostData);
                }

                postViewAdapter.notifyDataSetChanged();

            } catch (JSONException e) { }
            isLoading = false;
        }
        else {

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        dataList.clear();
                        JSONArray posts = response.getJSONArray("data");

                        for (int i = 0; i < posts.length(); i++) {
                            JSONObject postData = posts.getJSONObject(i);
                            PostViewData newPostData = new PostViewData();
                            newPostData.SetData(postData);
                            dataList.add(newPostData);
                        }

                        postViewAdapter.notifyDataSetChanged();

                    } catch (JSONException e) { }
                    Public_SectionFragment.this.pullToRefresh.setRefreshing(false);
                }

            });

        }
    }

    @Override
    public void OnFailure(String errorMessage, String apiName) {

    }

    @Override
    public void OnNetworkError() {
        noInternetPopUp();
        this.pullToRefresh.setRefreshing(false);

    }


    private void initScrollListener() {
        listView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == dataList.size() - 1) {
                        //bottom of list!
                        loadMore();
                        isLoading = true;
                    }
                }
            }
        });
    }

    private void loadMore() {
        dataList.add(null);
        postViewAdapter.notifyItemInserted(dataList.size() - 1);
        currPageNo++;
        Populate();
    }


}
