package com.jassimalmunaikh.feelerz;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.jassimalmunaikh.feelerz.ETab.Home;
import static com.jassimalmunaikh.feelerz.ETab.Profile;

enum ETab
{
    None,
    Home,
    Messenger,
    Post,
    Notification,
    Profile
}

public class DashboardActivity extends AppCompatActivity  {

    ImageButton Home_img,Message_img,Feeling_img,Notification_img,Profile_img;
    ProfileFragment profileFragment = null;
    NotificationFragment notificationFragment = null;
    ChatFragment chatFragment = null;
    HomeFragment homeFragment = null;
    Map<ETab,Fragment> mFragmentsMap;

    ETab currentTab = ETab.None;


    void OpenMyProfile()
    {
        OpenTabFragment(Profile);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarGadiant(this);
        setContentView(R.layout.activity_dashboard);

       Boolean open_notification = getIntent().getBooleanExtra("open_notification",false);

       SessionHandler.GetInstance().InitForCurrentContext(this);

        this.mFragmentsMap = new HashMap<ETab,Fragment>();

        Home_img = findViewById(R.id.Home);
        Message_img = findViewById(R.id.Message);
        Feeling_img = findViewById(R.id.Feeling);
        Notification_img = findViewById(R.id.Notification);
        Profile_img = findViewById(R.id.Profile);

        if(open_notification) {
            if(SessionHandler.GetInstance().isLoggedIn()) {
                JSONObject data = SessionHandler.GetInstance().getData();
                UserDataCache.GetInstance().SetLoginData(data);
                OpenTabFragment(ETab.Notification);
            }
            else
            {
                Intent intent = new Intent(DashboardActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        }
        else
            OpenTabFragment(Home);

        boolean shouldSuggestFriends = (GlobalCache.GetInstance().SuggestFriendEntryType == 0);

          if(shouldSuggestFriends)
            getSupportFragmentManager().beginTransaction().add(R.id.Main_Layout,new Suggested_FriendFragment(),"tag").commit();



        Home_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(currentTab == Home)
                    return;
                OpenTabFragment(Home);
                setNavigationBarIcon();
                keyboardhide();
            }
        });

        Message_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(currentTab == ETab.Messenger)
                    return;

                OpenTabFragment(ETab.Messenger);

                keyboardhide();

            }
        });

        Feeling_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentTab == ETab.Post)
                    return;
                OpenTabFragment(ETab.Post);

                    keyboardhide();
            }
        });

        Notification_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(currentTab == ETab.Notification)
                    return;

                OpenTabFragment(ETab.Notification);

                keyboardhide();
            }
        });

        Profile_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(currentTab == ETab.Profile)
                    return;

                OpenTabFragment(ETab.Profile);

                keyboardhide();
            }
        });
    }

    public void keyboardhide(){

        Utilities.getInstance().HideKeyboard(this);
    }


    private void setNavigationBarIcon()
    {
        Home_img.setImageResource(currentTab == Home ? R.drawable.home_nav_btn_on_shape : R.drawable.home_nav_btn_off_shape);
        Message_img.setImageResource(currentTab == ETab.Messenger ? R.drawable.message_nav_btn_on_shape : R.drawable.message_nav_btn_off_shape);
        Feeling_img.setImageResource(currentTab == ETab.Post ? R.drawable.feel_nav_btn_on_shape : R.drawable.feel_nav_btn_off_shape);
        Notification_img.setImageResource(currentTab == ETab.Notification ? R.drawable.notification_nav_btn_on_shape : R.drawable.notification_nav_btn_off_shape);
        Profile_img.setImageResource(currentTab == ETab.Profile ? R.drawable.profile_nav_btn_on_shape : R.drawable.profile_nav_btn_off_shape);
    }

    void SetPostButtonImage(Boolean open)
    {
        Feeling_img.setImageResource(open? R.drawable.feel_nav_btn_on_shape : R.drawable.feel_nav_btn_off_shape);

    }

    private void setStatusBarGadiant(DashboardActivity dashboardActivity) {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            Window window =dashboardActivity.getWindow();
            Drawable background = dashboardActivity.getResources().getDrawable(R.drawable.background_color);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(dashboardActivity.getResources().getColor(android.R.color.transparent));
            window.setBackgroundDrawable(background);
        }
    }


    public void OpenTabFragment(ETab tab)
    {
        GlobalFragmentStack.getInstance().Flush();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment fragment = null;

        fragment = mFragmentsMap.get(tab);

        HideOthers(tab,ft);

        if(fragment == null)
        {
            switch (tab) {
                case Home:
                    fragment = new HomeFragment();
                    break;
                case Notification:
                    fragment = new NotificationFragment();
                    break;
                case Post:
                    fragment = new PostFeelingSelection();
                    break;
                case Messenger:
                    fragment = new ChatFragment();
                    break;
                case Profile:
                    fragment = new ProfileFragment();
                    break;
            }

            mFragmentsMap.put(tab,fragment);
        }


        if(fragment.isAdded())
            ft.show(fragment);
        else {
            ft.add(R.id.Main_Layout, fragment,tab.toString());
        }

        ft.commit();

        currentTab = tab;
        setNavigationBarIcon();
    }

    void HideOthers(ETab current,FragmentTransaction ft)
    {
        Set<Map.Entry<ETab,Fragment>> set = mFragmentsMap.entrySet();
        for(Map.Entry<ETab,Fragment> pair : set)
        {
            if(pair.getKey() == current)
                continue;
            if(pair.getValue().isAdded()) {
                ft.hide(pair.getValue());
            }
        }
    }

    @Override
    public void onBackPressed() {
        GlobalFragmentStack.getInstance().onBackButton();
    }
}