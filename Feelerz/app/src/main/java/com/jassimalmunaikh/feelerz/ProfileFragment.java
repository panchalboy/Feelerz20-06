package com.jassimalmunaikh.feelerz;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.widget.Toast.makeText;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment implements ServerCallObserver {

    RequestHandler requestHandler;
    PostViewAdapter postViewAdapter;
    ProfileVIewCache userData;
    ArrayList<PostViewData> dataList;
    RecyclerView listView;
    SwipeRefreshLayout pullToRefresh;
    Boolean AlreadyOpen = true;

    String mCurrentUserId;

    View mainView;

    CustomLoader loader;

    CircleImageView topProfilePic;
    Typeface myCustomFontt1,myCustomFontt2,myCustomFontt3,myCustomFontt4;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

       View view = inflater.inflate(R.layout.fragment_profile, container, false);

       this.userData = new ProfileVIewCache();
       this.loader = new CustomLoader(getActivity(),(ViewGroup)view);

       Button  Profile_Menu = (Button)view.findViewById(R.id.Profile_Edit);
       Profile_Menu.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               FragmentManager fragmentManager = getFragmentManager();
               fragmentManager.beginTransaction().add(R.id.Main_Layout, new Profile_MenuFragment()).commit();
           }
       });


       this.mainView = view;

        dataList = new ArrayList<PostViewData>();
        FragmentManager manager = getActivity().getSupportFragmentManager();
        postViewAdapter = new PostViewAdapter(getContext(),dataList,getActivity().getSupportFragmentManager(),R.id.test_post_view,true);
        postViewAdapter.CreateLoader(this,view);
        postViewAdapter.EnableRefeelText("profile");
        listView =  mainView.findViewById(R.id.profile_posts);
        listView.setAdapter(postViewAdapter);
        listView.setLayoutManager(new LinearLayoutManager(getContext()));
        listView.setNestedScrollingEnabled(false);

        DividerItemDecoration itemDecorator = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.divider2));
        listView.addItemDecoration(itemDecorator);

        this.requestHandler = new RequestHandler(getContext(),this);

        ShowUserData();

        this.pullToRefresh = view.findViewById(R.id.pull_to_refresh);
        this.pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ShowUserData();
                PopulateUserPosts();
            }
        });

        PopulateUserPosts();

        return view;
    }

    public void SettextSytle(){

        myCustomFontt1 = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Klavika-Regular.ttf");
        myCustomFontt2 = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Nunito-Bold.ttf");
        myCustomFontt3 = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Nunito-Regular.ttf");
        myCustomFontt4 = Typeface.createFromAsset(getActivity().getAssets(),"fonts/nunito-semibold.ttf");
    }


    public void ShowUserData()
    {
        loader.show();
        Map<String,String> request = new HashMap<String,String>();
        request.put("user_id", UserDataCache.GetInstance().id);
        request.put("friend_id",UserDataCache.GetInstance().id);
        requestHandler.MakeRequest(request,"get_friend_profile");
    }
    
    void SetUserData() {
        //downloading profile pics
        String imageId = userData.imageId;
        if(!imageId.isEmpty()) {
            try {
                topProfilePic = mainView.findViewById(R.id.profile_pic);
                String url = getResources().getString(R.string.ImageURL) + imageId;
                ImageView backImageView = mainView.findViewById(R.id.Profile_Back);
                Picasso.get().load(url).into(topProfilePic);
                Picasso.get().load(url).into(backImageView);
                topProfilePic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewProfilePic();
                    }
                });
            }
            catch (Exception e){}
        }

        SettextSytle();

        //Name and username
        TextView name = mainView.findViewById(R.id.User_name_profile);
        name.setText(Html.fromHtml(StringEscapeUtils.unescapeJava(userData.name)));
        String userId = userData.userName;
        name.setTypeface(myCustomFontt2);

        if(!userId.isEmpty()) {
            TextView userName = mainView.findViewById(R.id.User_id_txt);
            userName.setText("@" + (Html.fromHtml(StringEscapeUtils.unescapeJava(userData.userName))));
            userName.setTypeface(myCustomFontt3);
        }

        //following | followers and feels

        TextView feels = mainView.findViewById(R.id.Feels);
        feels.setTypeface(myCustomFontt3);
        int total = userData.totalFeels;
        String s = total + "  Feels";
        feels.setText(s);

        TextView following = mainView.findViewById(R.id.Following);
        following.setTypeface(myCustomFontt3);
        total = userData.following.length;
        s = total + "  Following";
        following.setText(s);
        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle data = new Bundle();
                data.putString("tabOneTitle","Following");
                data.putString("tabTwoTitle","Followers");
                data.putString("targetId",userData.id);
                GenericTabsFragment fragment = new GenericTabsFragment();
                fragment.setArguments(data);
                getFragmentManager().beginTransaction().add(R.id.Main_Layout,fragment,"generic tabs").commit();
            }
        });

        TextView followers = mainView.findViewById(R.id.Followers);
        followers.setTypeface(myCustomFontt3);
        total = userData.followers.length;
        s = total + "  Followers";
        followers.setText(s);
        followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle data = new Bundle();
                data.putString("tabOneTitle","Following");
                data.putString("tabTwoTitle","Followers");
                data.putString("targetId",userData.id);
                GenericTabsFragment fragment = new GenericTabsFragment();
                fragment.setArguments(data);
                getFragmentManager().beginTransaction().add(R.id.Main_Layout,fragment,"generic tabs").commit();
            }
        });

        //Gender and Age
        TextView gendertxt = mainView.findViewById(R.id.Gender_txt);
        gendertxt.setTypeface(myCustomFontt3);

        TextView genderView = mainView.findViewById(R.id.Gender_options_txt);
        genderView.setTypeface(myCustomFontt3);

        if (userData.gender.isEmpty()){
            gendertxt.setVisibility(View.INVISIBLE);
            genderView.setVisibility(View.INVISIBLE);
        }
        else {
            gendertxt.setVisibility(View.VISIBLE);
            genderView.setVisibility(View.VISIBLE);
            genderView.setText(userData.gender);
            gendertxt.setText("Gender");
        }

        TextView agetxt = mainView.findViewById(R.id.Age_txt);
        agetxt.setTypeface(myCustomFontt3);

        TextView ageView = mainView.findViewById(R.id.Age_options_txt);
        ageView.setTypeface(myCustomFontt3);
        if (userData.dob.isEmpty()){
            agetxt.setVisibility(View.INVISIBLE);
            ageView.setVisibility(View.INVISIBLE);
        }
        else {
            agetxt.setVisibility(View.VISIBLE);
            ageView.setVisibility(View.VISIBLE);
            agetxt.setText("Age");
            ageView.setText(Utilities.getInstance().GetAge(userData.dob));
        }


        //bio

        TextView bioView = mainView.findViewById(R.id.Bio_txt);
        bioView.setTypeface(myCustomFontt3);
        bioView.setText(Html.fromHtml(StringEscapeUtils.unescapeJava(userData.bio)));

    }

    private void viewProfilePic(){
        Bundle bundle = new Bundle();
        bundle.putBoolean("forProfilePicture",true);
        bundle.putString("postImageId",userData.imageId);
        ImagePreview fragment = new ImagePreview();
        fragment.setArguments(bundle);
        getFragmentManager().beginTransaction().add(R.id.Main_Layout, fragment).addToBackStack(null).commit();
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

        if(AlreadyOpen){
        dialog.show();
        AlreadyOpen = false;
        }else
        AlreadyOpen = true;
    }

    public void PopulateUserPosts()
    {
        loader.show();
        Map<String,String> request = new HashMap<String,String>();
        request.put("user_id", UserDataCache.GetInstance().id);
        request.put("page_no","0");
        request.put("friend_id",UserDataCache.GetInstance().id);
        requestHandler.MakeRequest(request,"my_post");
    }


    @Override
    public void OnSuccess(JSONObject response, String apiName) {

        try
        {
            if(apiName.equals("my_post")) {
                dataList.clear();
                JSONArray posts = response.getJSONArray("data");

                for (int i = 0; i < posts.length(); i++) {
                    JSONObject postData = posts.getJSONObject(i);
                    PostViewData newPostData = new PostViewData();
                    newPostData.SetData(postData);
                    dataList.add(newPostData);
                }

                postViewAdapter.notifyDataSetChanged();

            }
            else if(apiName.equals("get_friend_profile")) {
                JSONObject data = response.getJSONObject("data");
                userData.SetLoginData(data);
                SetUserData();
            }

        }
        catch (JSONException e){}

        this.pullToRefresh.setRefreshing(false);
        loader.hide();

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