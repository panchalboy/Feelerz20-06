package com.jassimalmunaikh.feelerz;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
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

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class OtherProfileFragment extends Fragment implements ServerCallObserver, TopLevelFrag, UnfollowBlockListener{

    Typeface myCustomFontt1,myCustomFontt2,myCustomFontt3,myCustomFontt4;
    TextView followingBtn;
    SwipeRefreshLayout pullToRefresh;
    ProfileVIewCache userData;
    RequestHandler requestHandler;
    PostViewAdapter postViewAdapter;
    ArrayList<PostViewData> dataList;
    RecyclerView listView;

    String mCurrentUserId;

    View mainView;

    CustomLoader loader;

    Button Profile_Menu;

    public OtherProfileFragment() {

        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view = inflater.inflate(R.layout.fragment_other_profile, container, false);
        this.mainView = view;
        removePhoneKeypad();
       GlobalFragmentStack.getInstance().Register(this);

       this.loader = new CustomLoader(getActivity(),(ViewGroup)view);

       this.userData = new ProfileVIewCache();

       followingBtn = view.findViewById(R.id.follow_btn);


        mCurrentUserId = getArguments().getString("userId");

        this.requestHandler = new RequestHandler(getContext(),this);

        /*pullToRefresh = view.findViewById(R.id.pull_to_refresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refesh();
                ShowUserData();
                userData.notifyAll();

            }
        });*/

        ImageButton Back_Btn = view.findViewById(R.id.Back_Btn);
        Back_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnManualClose();
            }});

        ShowUserData();

        return view;

    }


    void removePhoneKeypad() {
        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        View focusedView = getActivity().getCurrentFocus();
        /*
         * If no view is focused, an NPE will be thrown
         *
         * Maxim Dmitriev
         */
        if (focusedView != null) {
            inputManager.hideSoftInputFromWindow(focusedView.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    public void SettextSytle(){

        myCustomFontt1 = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Klavika-Regular.ttf");
        myCustomFontt2 = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Nunito-Bold.ttf");
        myCustomFontt3 = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Nunito-Regular.ttf");
        myCustomFontt4 = Typeface.createFromAsset(getActivity().getAssets(),"fonts/nunito-semibold.ttf");
    }

    void SetUserData(Boolean hideForBlock)
    {
        TextView blockedUserText = mainView.findViewById(R.id.BlockUserText);
        blockedUserText.setVisibility(hideForBlock ? View.VISIBLE : View.INVISIBLE);
        String localName = StringEscapeUtils.unescapeJava(userData.name).trim();
        blockedUserText.setText("User is blocked");
        blockedUserText.setTypeface(myCustomFontt2);
        //downloading profile pics
        String imageId = userData.imageId;
        if(!imageId.isEmpty()) {
            ImageView backImage = mainView.findViewById(R.id.Profile_Back);
            String url = getResources().getString(R.string.ImageURL) + imageId;
            Picasso.get().load(url).into(backImage);

            CircleImageView topProfilePic = mainView.findViewById(R.id.profile_pic);
            Picasso.get().load(url).into(topProfilePic);

            topProfilePic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewProfilePic();
                }
            });

            //back
        }

        SettextSytle();

        //Name and username
        TextView name = mainView.findViewById(R.id.User_name_profile);
        name.setText(Html.fromHtml(StringEscapeUtils.unescapeJava(userData.name)));
        name.setTypeface(myCustomFontt2);
        String userId = userData.userName;
        if(!userId.isEmpty()) {
            TextView userName = mainView.findViewById(R.id.User_id_txt);
            userName.setText("@" + (Html.fromHtml(StringEscapeUtils.unescapeJava(userData.userName))));
            userName.setTypeface(myCustomFontt3);
        }

        //following | followers and feels

        TextView following = mainView.findViewById(R.id.Following);
        following.setTypeface(myCustomFontt3);
        following.setVisibility(hideForBlock ? View.INVISIBLE : View.VISIBLE);
        int total = userData.following.length;
        String s = total + "  Following";
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
        followers.setVisibility(hideForBlock ? View.INVISIBLE : View.VISIBLE);
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

        TextView feels = mainView.findViewById(R.id.Feels);
        feels.setTypeface(myCustomFontt3);
        feels.setVisibility(hideForBlock ? View.INVISIBLE : View.VISIBLE);
        total = userData.totalFeels;
        s = total + "  Feels";
        feels.setText(s);

        //Gender and Age

        TextView gendertxt = mainView.findViewById(R.id.Gender_txt);
        gendertxt.setVisibility(hideForBlock ? View.INVISIBLE : View.VISIBLE);
        gendertxt.setTypeface(myCustomFontt3);

        TextView genderView = mainView.findViewById(R.id.Gender_options_txt);
        genderView.setTypeface(myCustomFontt3);
        genderView.setVisibility(hideForBlock ? View.INVISIBLE : View.VISIBLE);
        if(!hideForBlock) {
            if (userData.gender.isEmpty()) {
                gendertxt.setVisibility(View.INVISIBLE);
                genderView.setVisibility(View.INVISIBLE);
            } else {
                gendertxt.setVisibility(View.VISIBLE);
                genderView.setVisibility(View.VISIBLE);
                gendertxt.setText("Gender");
                genderView.setText(userData.gender);
            }
        }


        TextView agetxt = mainView.findViewById(R.id.Age_txt);
        agetxt.setVisibility(hideForBlock ? View.INVISIBLE : View.VISIBLE);
        agetxt.setTypeface(myCustomFontt3);

        TextView ageView = mainView.findViewById(R.id.Age_options_txt);
        ageView.setVisibility(hideForBlock ? View.INVISIBLE : View.VISIBLE);
        ageView.setTypeface(myCustomFontt3);
        if(!hideForBlock) {
            if (userData.dob.isEmpty()) {
                agetxt.setVisibility(View.INVISIBLE);
                ageView.setVisibility(View.INVISIBLE);
            } else {
                agetxt.setVisibility(View.VISIBLE);
                ageView.setVisibility(View.VISIBLE);
                agetxt.setText("Age");
                ageView.setText(Utilities.getInstance().GetAge(userData.dob));
            }
        }
        //bio

        /*LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView gender;
        if()
        {
            params.gravity = Gravity.CENTER;
            gender.setLayoutParams(params);
        }*/

        TextView bioView = mainView.findViewById(R.id.Bio_txt);
        bioView.setVisibility(hideForBlock ? View.INVISIBLE : View.VISIBLE);
        bioView.setTypeface(myCustomFontt3);
        bioView.setText(Html.fromHtml(StringEscapeUtils.unescapeJava(userData.bio)));

        SetupFollowingButton(hideForBlock);
        SetupMessagingButton(hideForBlock);
    }

    private void viewProfilePic(){
        Bundle bundle = new Bundle();
        bundle.putBoolean("forProfilePicture",true);
        bundle.putString("postImageId",userData.imageId);
        ImagePreview fragment = new ImagePreview();
        fragment.setArguments(bundle);
        getFragmentManager().beginTransaction().add(R.id.Main_Layout, fragment).addToBackStack(null).commit();
    }

    void SetupFollowingButton(Boolean hideForBlock)
    {
        followingBtn.setVisibility(hideForBlock ? View.INVISIBLE : View.VISIBLE);
        followingBtn.setTypeface(myCustomFontt3);
        followingBtn.setText(AmIFollowingThisGuy() ? "Following" : "Follow");
        followingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String status = followingBtn.getText().toString();
                if(status.equals("Follow")) {
                    SendFollowRequest();
                }
                else if(status.equals("Following"))
                {
                    OpenUnfollowBlockBS();
                }
            }
        });
    }

    void OpenUnfollowBlockBS()
    {
        UnfollowBlockDialog bs = new UnfollowBlockDialog();
        Bundle args = new Bundle();
        args.putString("userId",userData.id);
        args.putInt("position",0);
        bs.setArguments(args);
        bs.SetListener(this);
        bs.show(getFragmentManager(),"delete bs dialog");
    }

    void SetupMessagingButton(Boolean hideForBlock)
    {
        TextView Btn = mainView.findViewById(R.id.message_btn);
        Btn.setVisibility(hideForBlock ? View.INVISIBLE : View.VISIBLE);
        Btn.setTypeface(myCustomFontt3);
        Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle data = new Bundle();
                data.putString("userId",userData.id);
                data.putString("name",userData.name);
                data.putString("imageId",userData.imageId);
                Fragment fragment = null;
                fragment = new Chating_UserFragment();
                fragment.setArguments(data);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().add(R.id.Main_Layout, fragment).addToBackStack(null).commit();
            }
        });
    }

    void SendFollowRequest()
    {
        loader.show();
        Map<String,String> request = new HashMap<String,String>();
        request.put("user_id", UserDataCache.GetInstance().id);
        request.put("friend_id",mCurrentUserId);
        requestHandler.MakeRequest(request,"following");
    }

    void SendUnfollowRequest()
    {
        Map<String,String> request = new HashMap<String,String>();
        request.put("user_id", UserDataCache.GetInstance().id);
        request.put("friend_id",mCurrentUserId);
        requestHandler.MakeRequest(request,"unfollowing");
    }



    Boolean HaveIBlockedThisGuy()
    {
        for(int i = 0; i < this.userData.blockedByOthers.length ; i++)
        {
            if(this.userData.blockedByOthers[i].equals(UserDataCache.GetInstance().id))
                return true;
        }
        return false;
    }

    Boolean AmIFollowingThisGuy()
    {
       for(int i = 0; i < this.userData.followers.length ; i++)
       {
           if(this.userData.followers[i].equals(UserDataCache.GetInstance().id))
               return true;
       }
       return false;
    }

    public void ShowUserData()
    {
        loader.show();
        Map<String,String> request = new HashMap<String,String>();
        request.put("user_id", mCurrentUserId);
        request.put("friend_id",mCurrentUserId);
        requestHandler.MakeRequest(request,"get_friend_profile");
    }


    public void ShowUserPost()
    {
        dataList = new ArrayList<PostViewData>();
        FragmentManager manager = getActivity().getSupportFragmentManager();
        postViewAdapter = new PostViewAdapter(getContext(),dataList,getFragmentManager(),R.id.test_post_view,true);
        postViewAdapter.CreateLoader(this,getView());
        listView =  mainView.findViewById(R.id.profile_posts);
        listView.setAdapter(postViewAdapter);
        listView.setLayoutManager(new LinearLayoutManager(getContext()));
        listView.setNestedScrollingEnabled(false);

        DividerItemDecoration itemDecorator = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.divider2));
        listView.addItemDecoration(itemDecorator);


        Map<String,String> request = new HashMap<String,String>();
        request.put("user_id", mCurrentUserId);
        request.put("page_no","0");
        request.put("friend_id",mCurrentUserId);
        requestHandler.MakeRequest(request,"my_post");
    }

    @Override
    public void OnSuccess(JSONObject response, String apiName) {
        try
        {
            if(apiName == "my_post") {
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
            else if(apiName == "get_friend_profile")
            {
                JSONObject data = response.getJSONObject("data");
                userData.SetLoginData(data);
                if(HaveIBlockedThisGuy()) {
                    SetUserData(true);
                }
                else {
                    SetUserData(false);
                    ShowUserPost();
                }
            }
            else if(apiName == "following")
            {
                TextView followBtn = mainView.findViewById(R.id.follow_btn);
                followBtn.setEnabled(true);
                int total = userData.following.length;
                followBtn.setText("Following");
                followBtn.setTypeface(myCustomFontt3);

            }
            else if(apiName == "unfollowing")
            {
                TextView followBtn = mainView.findViewById(R.id.follow_btn);
                followBtn.setEnabled(true);
                int total = userData.following.length;
                userData.UnfollowThisUser();
                followBtn.setText("Follow");
                followBtn.setTypeface(myCustomFontt3);
            }

        }
        catch (JSONException e){}

        loader.hide();
    }

    @Override
    public void OnFailure(String errorMessage, String apiName) {
        loader.SetErrorColor();
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

    @Override
    public void OnBlockDone(int position) {
        loader.hide();
        SetUserData(true);
        dataList.clear();
        postViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void OnUnfollowDone(int position) {
        loader.hide();
        userData.UnfollowThisUser();
        SetupFollowingButton(false);
    }

    @Override
    public void OnProcessing() {
        loader.show();
    }
}
