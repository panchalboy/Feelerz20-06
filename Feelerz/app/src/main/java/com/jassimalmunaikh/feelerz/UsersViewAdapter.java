package com.jassimalmunaikh.feelerz;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.apache.commons.text.StringEscapeUtils;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

class UserViewData
{
    private String name;
    private String imageURL;
    public String userId;

    UserViewData()
    {
        name = "Default";
        imageURL = "";
    }

    UserViewData(String name,String imageURL,String id){
        this.name = name;
        this.imageURL = imageURL;
        this.userId = id;
    }

    String getName() {
        return name;
    }

    public String getImageURL() {
        return imageURL;
    }
}

class UserProfileData{
    private String name2;
    private String imageURL2;
    public String userId2;
    private String feeling2;
    private String postText2;

    UserProfileData()
    {
        name2 = "Default";
        imageURL2 = "";
        feeling2 = "";
        postText2 = "";
    }

    UserProfileData(String name,String imageURL,String id,String postText,String feeling){
        this.name2 = name;
        this.imageURL2 = imageURL;
        this.userId2 = id;
        this.postText2 = postText;
        this.feeling2 = feeling;
    }

    String getName1() {
        return name2;
    }

    public String getImageURL1() {
        return imageURL2;
    }

    String getFeeling1(){
        return feeling2;
    }

    String getPostText1(){
        return postText2;
    }

}
//public class UsersViewAdapter extends ArrayAdapter<UserViewData>
class UserProfileAdapter extends ArrayAdapter<UserProfileData>{
    FragmentManager fragmentManager2;
    private Context mContext2;
    private ArrayList<UserProfileData> mUsersList2 = null;


    public UserProfileAdapter(@NonNull Context context, ArrayList<UserProfileData> list1,FragmentManager manager)
    {
        super(context, 0 , list1);
        mContext2 = context;
        mUsersList2 = list1;
        this.fragmentManager2 = manager;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listItem = LayoutInflater.from(mContext2).inflate(R.layout.user_search_list_item,parent,false);

        UserProfileData currentUser2 = mUsersList2.get(position);
        CircleImageView imageView = (CircleImageView) listItem.findViewById(R.id.user_profile_pic);

        String id = currentUser2.getImageURL1();
        if(!id.isEmpty()) {
            String imageAddress = mContext2.getResources().getString(R.string.ImageURL) + id;
            Picasso.get().load(imageAddress).into(imageView);
        }

        TextView name = (TextView) listItem.findViewById(R.id.user_name);
        name.setText(Html.fromHtml(StringEscapeUtils.unescapeJava(currentUser2.getName1())));

        TextView feel = (TextView)listItem.findViewById(R.id.FeelingText);
        feel.setText(Html.fromHtml(StringEscapeUtils.unescapeJava(currentUser2.getFeeling1())));

        TextView post = (TextView)listItem.findViewById(R.id.postText);
        post.setText(Html.fromHtml(StringEscapeUtils.unescapeJava(currentUser2.getPostText1())));

//        Typeface myCustomFontt1 = Typeface.createFromAsset(getAssets(),"fonts/Nunito-Regular.ttf");
//        name.setTypeface(myCustomFontt1);

//        Typeface name = Typeface.createFromAsset(getAssets(), "fonts/Nunito-Regular.ttf");

        SetupProfileView(listItem,currentUser2.userId2);
        return listItem;
    }

    void SetupProfileView(View view, final String userId)
    {
        View view1;
        final UserProfileAdapter self = this;

        View r1 = view.findViewById(R.id.user_profile_pic);
        r1.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(UserDataCache.GetInstance().id.equals(userId)) {
                            ((DashboardActivity)mContext2).OpenMyProfile();
                        }
                        else
                        {
                            Bundle data = new Bundle();
                            data.putString("userId",userId);
                            Fragment fragment = null;
                            fragment = new OtherProfileFragment();
                            fragment.setArguments(data);
                            FragmentManager fragmentManager = self.fragmentManager2;
                            fragmentManager.beginTransaction().add(R.id.Main_Layout, fragment).addToBackStack(null).commit();
                        }

                    }
                }
        );
    }


}


public class UsersViewAdapter extends ArrayAdapter<UserViewData>  {

    FragmentManager fragmentManager;
    private Context mContext;
    private ArrayList<UserViewData> mUsersList = null;

    public UsersViewAdapter(@NonNull Context context, ArrayList<UserViewData> list,FragmentManager manager)
    {
        super(context, 0 , list);
        mContext = context;
        mUsersList = list;
        this.fragmentManager = manager;

    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listItem = LayoutInflater.from(mContext).inflate(R.layout.user_search_list_item,parent,false);

        UserViewData currentUser = mUsersList.get(position);
        CircleImageView imageView = (CircleImageView) listItem.findViewById(R.id.user_profile_pic);

        String id = currentUser.getImageURL();
        if(!id.isEmpty()) {
            String imageAddress = mContext.getResources().getString(R.string.ImageURL) + id;
            Picasso.get().load(imageAddress).into(imageView);
        }

        TextView name = (TextView) listItem.findViewById(R.id.user_name);
        name.setText(Html.fromHtml(StringEscapeUtils.unescapeJava(currentUser.getName())));

//        Typeface myCustomFontt1 = Typeface.createFromAsset(getAssets(),"fonts/Nunito-Regular.ttf");
//        name.setTypeface(myCustomFontt1);

//        Typeface name = Typeface.createFromAsset(getAssets(), "fonts/Nunito-Regular.ttf");

        SetupProfileView(listItem,currentUser.userId);
        return listItem;
    }

    void SetupProfileView(View view, final String userId)
    {
        View view1;
        final UsersViewAdapter self = this;

        View r1 = view.findViewById(R.id.user_profile_pic);
        r1.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(UserDataCache.GetInstance().id.equals(userId)) {
                            ((DashboardActivity)mContext).OpenMyProfile();
                        }
                        else
                        {
                            Bundle data = new Bundle();
                            data.putString("userId",userId);
                            Fragment fragment = null;
                            fragment = new OtherProfileFragment();
                            fragment.setArguments(data);
                            FragmentManager fragmentManager = self.fragmentManager;
                            fragmentManager.beginTransaction().add(R.id.Main_Layout, fragment).addToBackStack(null).commit();
                        }

                    }
                }
        );
    }
    /*public void removePhoneKeypad() {
        InputMethodManager inputManager = (InputMethodManager) myfragment
                .getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        IBinder binder = myfragment.getWindowToken();
        inputManager.hideSoftInputFromWindow(binder,
                InputMethodManager.HIDE_NOT_ALWAYS);
    }*/


    public AssetManager getAssets() {
        return getAssets();
    }
}


