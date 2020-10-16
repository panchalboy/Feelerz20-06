package com.jassimalmunaikh.feelerz;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
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
public class SearchForUser extends Fragment implements  ServerCallObserver{

    LinearLayout linearLayout;

    SearchView searchView;
    ListView listView,listView2;
    LinearLayout searchViewLayout,searchViewLayout2;
    UsersViewAdapter customAdapter = null;
    UserProfileAdapter customAdapter2 = null;
    ArrayList<UserViewData> data = null;
    ArrayList<UserProfileData> data2 = null;
    public int lastItemPosition = 3;
    Button moreButton = null,moreButton2 = null;
    String currentQuery = "";
    Button Back_Button;
    View view1;

    RequestHandler requestHandler = null;

    public static SearchForUser instance = null;

    public SearchForUser() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_for_user, container, false);

        this.requestHandler = new RequestHandler(getContext(),this);

        instance = this;
        this.view1 = view;
        data = new ArrayList<UserViewData>();
        data2 = new ArrayList<UserProfileData>();
        customAdapter = new UsersViewAdapter(getContext(),data,getFragmentManager());
        customAdapter2 = new UserProfileAdapter(getContext(),data2,getFragmentManager());

        linearLayout = (LinearLayout)view.findViewById(R.id.search_list_view);

        moreButton = (Button)view.findViewById(R.id.more_button);
        moreButton2 = (Button) view.findViewById(R.id.more_button2);
        searchViewLayout = (LinearLayout)view.findViewById(R.id.search_list_view);
        searchViewLayout2 = (LinearLayout)view.findViewById(R.id.Feeling_list_view);
        searchViewLayout.setVisibility(View.INVISIBLE);
        searchViewLayout2.setVisibility(View.INVISIBLE);
        searchView = view.findViewById(R.id.user_search_view);
        listView = (ListView) view.findViewById(R.id.search_result_list_view);
        listView2 = (ListView)view.findViewById(R.id.Feeling_result_list_view);
        listView.setAdapter(customAdapter);
        listView2.setAdapter(customAdapter2);
        Back_Button = view.findViewById(R.id.Back_Btn);

        Back_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removePhoneKeypad();
              getFragmentManager().beginTransaction().remove(SearchForUser.this).commit();
            }
        });

        moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                AllUsersFromSearch fragment = new AllUsersFromSearch();
                Bundle data = new Bundle();
                data.putString("query",currentQuery);
                fragment.setArguments(data);
                transaction.add(R.id.Main_Layout,fragment).commit();
            }
        });
        moreButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                AllPostFromSearch fragment = new AllPostFromSearch();
                Bundle data = new Bundle();
                data.putString("query",currentQuery);
                fragment.setArguments(data);
                transaction.add(R.id.Main_Layout,fragment).commit();
            }
        });

        this.moreButton.setVisibility(View.INVISIBLE);
        this.moreButton2.setVisibility(View.INVISIBLE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
//                searchView.onActionViewExpanded();

//                if(!searchView.isActivated()) {
//                    linearLayout.setVisibility(View.VISIBLE);
//                }
//                else
//                {
//                    linearLayout.setVisibility(View.INVISIBLE);
//                }
                if(!searchViewLayout.isShown() && !searchViewLayout2.isShown())
                    searchViewLayout.setVisibility(View.VISIBLE);
                    searchViewLayout2.setVisibility((View.VISIBLE));
                SearchForUser.instance.populateList(query);
                return true;
            }


            @Override
            public boolean onQueryTextChange(String newText) {
                if(!searchViewLayout.isShown() && !searchViewLayout2.isShown())
                    searchViewLayout.setVisibility(View.VISIBLE);
                    searchViewLayout2.setVisibility(View.VISIBLE);
                SearchForUser.instance.populateList(newText);
                return true;
            }
        });

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

//    public void keyboardhide(){
//        InputMethodManager inputManager = (InputMethodManager)
//                getSystemService(Context.INPUT_METHOD_SERVICE);
//
//        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
//                InputMethodManager.HIDE_NOT_ALWAYS);
//    }

    public void populateList(String query)
    {
        currentQuery = query;

        Map<String,String> request = new HashMap<String,String>();
            //Populate the request parameters
        request.put("user_id", UserDataCache.GetInstance().id);
        request.put("search_text", query);
        this.requestHandler.MakeRequest(request,"search_FriendA");
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
        try
        {
                data.clear();

                JSONArray users = response.getJSONArray("users");

                this.moreButton.setVisibility(users.length() >= 3 ? View.VISIBLE : View.INVISIBLE);

                for (int i = 0; i < users.length(); i++) {
                    JSONObject user = users.getJSONObject(i);
                    String name = user.getString("name");
                    String imageId = user.getString("profileimage");
                    String userId = user.getString("user_id");
                    data.add(new UserViewData(name, imageId, userId));
                }

                JSONArray post = response.getJSONArray("post");

                this.moreButton2.setVisibility(post.length() >= 3 ? View.VISIBLE : View.INVISIBLE);

                for (int j = 0; j < post.length(); j++){
                    JSONObject profile = post.getJSONObject(j);
                    String name22 = profile.getString("name");
                    String imageId22 = profile.getString("profileimage");
                    String userId22 = profile.getString("user_id");
                    String postText22 = profile.getString("post_text");
                    String feeling22 = profile.getString("feeling");
                    data2.add(new UserProfileData(name22, imageId22 , userId22 , postText22 , feeling22));
                }



                customAdapter.notifyDataSetChanged();
                customAdapter2.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void OnFailure(String errorMessage, String apiName) {

    }

    @Override
    public void OnNetworkError() {
        noInternetPopUp();
    }
}