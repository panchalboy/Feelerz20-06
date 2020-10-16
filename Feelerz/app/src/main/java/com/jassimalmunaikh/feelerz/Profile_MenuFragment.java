package com.jassimalmunaikh.feelerz;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.IBinder;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jassimalmunaikh.feelerz.social.SocialLogin;
import com.squareup.picasso.Picasso;

import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.widget.Toast.makeText;

/**
 * A simple {@link Fragment} subclass.
 */
public class Profile_MenuFragment extends Fragment implements TopLevelFrag,ServerCallObserver {

    private static final String KEY_MESSAGE = "message";

    TextView profileMenu_Title,Txt_User_Name,UP_txt,SF_txt,CP_txt,PP_txt,AA_txt,S_txt,BUL_txt,L_txt;
    Typeface myCustomFontt1,myCustomFontt2,myCustomFontt3,myCustomFontt4;
    Button Back_Btn;
    RelativeLayout updateProfile,suggestedFriend,changePassword,privacyPolicy,aboutApp,Support,blockedUSerList,logout;
    int EntryType;
    View view1;
    CustomLoader loader;
    RequestHandler requestHandler;

    public SessionHandler session;

    public Profile_MenuFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile__menu, container, false);
        this.view1 = view;
        removePhoneKeypad();
        this.requestHandler = new RequestHandler(getContext(),this);
        this.loader = new CustomLoader(getActivity(),(ViewGroup)view);

        EntryType = GlobalCache.GetInstance().SuggestFriendEntryType = 1;

        GlobalFragmentStack.getInstance().Register(this);

        CircleImageView iv = view.findViewById(R.id.profile_image);
        String url = getResources().getString(R.string.ImageURL) + UserDataCache.GetInstance().imageId;
        Picasso.get().load(url).into(iv);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewProfilePic();
            }
        });

        profileMenu_Title = view.findViewById(R.id.profileMenu_Title);
        Txt_User_Name = view.findViewById(R.id.Txt_User_Name);
        UP_txt = view.findViewById(R.id.UP_txt);
        SF_txt = view.findViewById(R.id.SF_txt);
        CP_txt = view.findViewById(R.id.CP_txt);
        PP_txt = view.findViewById(R.id.PP_txt);
        AA_txt = view.findViewById(R.id.AA_txt);
        S_txt = view.findViewById(R.id.S_txt);
        BUL_txt = view.findViewById(R.id.BUL_txt);
        L_txt = view.findViewById(R.id.L_txt);

        SettextSytle();

        profileMenu_Title.setTypeface(myCustomFontt4);
        Txt_User_Name.setTypeface(myCustomFontt2);
        Txt_User_Name.setText(Html.fromHtml(StringEscapeUtils.unescapeJava(UserDataCache.GetInstance().name)));
//        Txt_User_Name.setText(UserDataCache.GetInstance().name);
        UP_txt.setTypeface(myCustomFontt3);
        SF_txt.setTypeface(myCustomFontt3);
        CP_txt.setTypeface(myCustomFontt3);
        PP_txt.setTypeface(myCustomFontt3);
        AA_txt.setTypeface(myCustomFontt3);
        S_txt.setTypeface(myCustomFontt3);
        BUL_txt.setTypeface(myCustomFontt3);
        L_txt.setTypeface(myCustomFontt3);

        session.GetInstance().InitForCurrentContext(getActivity());

        Back_Btn = view.findViewById(R.id.Back_Btn);
        Back_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnManualClose();
            }
        });
        updateProfile = view.findViewById(R.id.UP_btn);
        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().add(R.id.Main_Layout, new UpdateProfileFragment()).commit();
            }
        });
        suggestedFriend = view.findViewById(R.id.SF_btn);
        suggestedFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().add(R.id.Main_Layout, new Suggested_FriendFragment()).commit();
            }
        });
        changePassword = view.findViewById(R.id.CP_btn);
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().add(R.id.Main_Layout, new Change_PasswordFragment()).commit();
            }
        });
        privacyPolicy = view.findViewById(R.id.PP_btn);
        privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().add(R.id.Main_Layout, new Privacy_PolicyFragment()).commit();
            }
        });
        aboutApp = view.findViewById(R.id.AA_btn);
        aboutApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().add(R.id.Main_Layout, new About_AppFragment()).commit();
            }
        });
        Support = view.findViewById(R.id.S_btn);
        Support.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().add(R.id.Main_Layout, new SupportFragment()).commit();
            }
        });
        blockedUSerList = view.findViewById(R.id.BUL_btn);
        blockedUSerList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().add(R.id.Main_Layout, new Blocked_User_ListFragment()).commit();
            }
        });
        logout = view.findViewById(R.id.L_btn);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    LogoutPopUp();
                    return;
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

    private void viewProfilePic(){
        Bundle bundle = new Bundle();
        bundle.putBoolean("forProfilePicture",true);
        bundle.putString("postImageId",UserDataCache.GetInstance().imageId);
        ImagePreview fragment = new ImagePreview();
        fragment.setArguments(bundle);
        getFragmentManager().beginTransaction().add(R.id.Main_Layout, fragment).addToBackStack(null).commit();
    }

    private void LogoutPopUp(){

        SettextSytle();
        // custom dialog
        final Dialog dialog = new Dialog(getContext());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.logout_user_confirm_popup);
        dialog.setTitle("Logout Confirmation");

        TextView dialog_LogoutTitle = dialog.findViewById(R.id.MsgDlt_Heading_txt);
        TextView dialog_LogoutConfirmation = dialog.findViewById(R.id.MsgDlt_Confirm_txt);
        TextView dialog_Cancel_btn = (TextView) dialog.findViewById(R.id.cancel_BTN);
        TextView dialog_OK_btn = (TextView) dialog.findViewById(R.id.Delete_BTN);

        dialog_LogoutTitle.setTypeface(myCustomFontt4);
        dialog_LogoutConfirmation.setTypeface(myCustomFontt2);
        dialog_Cancel_btn.setTypeface(myCustomFontt3);
        dialog_OK_btn.setTypeface(myCustomFontt3);

        dialog_OK_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogoutUser();
                dialog.dismiss();
            }
        });

        dialog_Cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void SettextSytle(){

        myCustomFontt1 = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Klavika-Regular.ttf");
        myCustomFontt2 = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Nunito-Bold.ttf");
        myCustomFontt3 = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Nunito-Regular.ttf");
        myCustomFontt4 = Typeface.createFromAsset(getActivity().getAssets(),"fonts/nunito-semibold.ttf");
    }

    @Override
    public void OnManualClose() {
        GlobalFragmentStack.getInstance().Unregister(this);
        Close();
    }
    private void LogoutUser(){

        this.loader.show();
        Map<String,String> request = new HashMap<>();
        request.put("user_id",UserDataCache.GetInstance().id);
        this.requestHandler.MakeRequest(request,"logout");

    }

    @Override
    public void Close() {
        getFragmentManager().beginTransaction().remove(this).commit();
    }

    @Override
    public void OnSuccess(JSONObject response, String apiName) {
        if(apiName =="logout")
        {
            SocialLogin currentLogin = GlobalCache.GetInstance().currentSocialLogin;
            if(currentLogin != null)
                currentLogin.SignOut();
            session.GetInstance().logoutUser();
            GlobalFragmentStack.getInstance().clear();
            Intent intent1 = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent1);
        }
        this.loader.hide();
    }

    @Override
    public void OnFailure(String errorMessage, String apiName) {
        this.loader.hide();
    }

    @Override
    public void OnNetworkError() {

    }
}