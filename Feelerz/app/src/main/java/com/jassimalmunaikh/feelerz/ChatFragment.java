package com.jassimalmunaikh.feelerz;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment implements ServerCallObserver {
    View view1;
    RecyclerView mChatUserList;
    ChatUserAdapter mAdapter;
    ArrayList<ChatUserData> mList;

    RequestHandler requestHandler;
    CustomLoader loader;

    Typeface myCustomFontt1, myCustomFontt2, myCustomFontt3, myCustomFontt4;
    TextView MessageChat_Title;
    EditText Search_userChat;
    SwipeRefreshLayout pullToRefresh;
    Boolean isLoading;
    Integer currPageNo;

    public ChatFragment() {
        // Required empty public constructor
        this.currPageNo = 0;
        this.isLoading = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        this.view1 = view;

        this.loader = new CustomLoader(getActivity(), (ViewGroup) view);
        requestHandler = new RequestHandler(getContext(), this);

        mList = new ArrayList<ChatUserData>();

        mAdapter = new ChatUserAdapter(getContext(), mList, getActivity().getSupportFragmentManager(), this);
        mChatUserList = (RecyclerView) view.findViewById(R.id.ChatUserList);
        mChatUserList.setAdapter(mAdapter);
        mChatUserList.setLayoutManager(new LinearLayoutManager(getContext()));

        DividerItemDecoration itemDecorator = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.divider));
        mChatUserList.addItemDecoration(itemDecorator);

        SettextSytle();
        MessageChat_Title = view.findViewById(R.id.MessageChat_Title);
        Search_userChat = view.findViewById(R.id.Search_userChat);

        // Add Text Change Listener to EditText
        Search_userChat.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Call back the Adapter with current character to Filter
                mAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        MessageChat_Title.setTypeface(myCustomFontt4);
        Search_userChat.setTypeface(myCustomFontt3);

        this.pullToRefresh = view.findViewById(R.id.pull_to_refresh);
        this.pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currPageNo = 0;
                populateChatFriendSection();
            }
        });

        loader.show();
        populateChatFriendSection();
        initScrollListener();

        return view;
    }

    public void removePhoneKeypad() {
        InputMethodManager inputManager = (InputMethodManager) view1
                .getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        IBinder binder = view1.getWindowToken();
        inputManager.hideSoftInputFromWindow(binder,
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void populateChatFriendSection() {
//          loader.hide();
        Map<String, String> request = new HashMap<String, String>();
        request.put("user_id", UserDataCache.GetInstance().id);
        request.put("page_no", String.valueOf(currPageNo));
        requestHandler.MakeRequest(request, "get_chat_friend_list");
    }


    public void SettextSytle() {

        myCustomFontt1 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Klavika-Regular.ttf");
        myCustomFontt2 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Nunito-Bold.ttf");
        myCustomFontt3 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Nunito-Regular.ttf");
        myCustomFontt4 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/nunito-semibold.ttf");
    }

    private void noInternetPopUp() {
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
        loader.hide();
        if(isLoading)
        {
            try {
                JSONArray Chat = response.getJSONArray("data");
                if(Chat == null)
                {
                    isLoading = false;
                    mList.remove(mList.size() - 1);
                    mAdapter.notifyItemRemoved(mList.size());
                    return;
                }

                mList.remove(mList.size() - 1);
                int scrollPosition = mList.size();
                mAdapter.notifyItemRemoved(scrollPosition);
                int currentSize = scrollPosition;

                for (int i = 0; i < Chat.length(); i++) {
                    JSONObject mChat = Chat.getJSONObject(i);
                    ChatUserData newChatUserData = new ChatUserData();
                    newChatUserData.SetData(mChat);
                    mList.add(newChatUserData);
                }

                mAdapter.notifyDataSetChanged();

            } catch (JSONException e) { }
            isLoading = false;
        }
        else {

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        mList.clear();
                        JSONArray Chat = response.getJSONArray("data");

                        for (int i = 0; i < Chat.length(); i++) {
                            JSONObject mChat = Chat.getJSONObject(i);
                            ChatUserData newChatUserData = new ChatUserData();
                            newChatUserData.SetData(mChat);
                            mList.add(newChatUserData);
                            mAdapter.notifyDataSetChanged();
                        }

                    } catch (JSONException e) { }
                    ChatFragment.this.pullToRefresh.setRefreshing(false);
                }

            });

        }

//        if (apiName == "get_chat_friend_list") {
//            mList.clear();
//            try {
//                JSONArray Chat = response.getJSONArray("data");
//
//                for (int i = 0; i < Chat.length(); i++) {
//                    JSONObject mChat = Chat.getJSONObject(i);
//                    ChatUserData newChatUserData = new ChatUserData();
//                    newChatUserData.SetData(mChat);
//                    mList.add(newChatUserData);
//                    mAdapter.notifyDataSetChanged();
//                }
//            } catch (JSONException e) {
//            }
//
//        }
//        this.pullToRefresh.setRefreshing(false);
//        loader.hide();
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

    private void initScrollListener() {
        this.mChatUserList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == mList.size() - 1) {
                        //bottom of list!
                        loadMore();
                        isLoading = true;
                    }
                }
            }
        });
    }

    private void loadMore() {
        mList.add(null);
        mAdapter.notifyItemInserted(mList.size() - 1);
        currPageNo++;
        populateChatFriendSection();
    }

}
