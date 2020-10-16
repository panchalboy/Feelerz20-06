package com.jassimalmunaikh.feelerz;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.apache.commons.text.StringEscapeUtils;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import de.hdodenhof.circleimageview.CircleImageView;

class MentionViewData {

    private String name;
    private String imageURL;
    public String userId;

    MentionViewData() {
        name = "Default";
        imageURL = "";
    }



    MentionViewData(String name, String imageURL, String id) {
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

public class Mention_UserAdapter extends ArrayAdapter<MentionViewData> {
    FragmentManager fragmentManager2;
    private Context mContext2;
    private ArrayList<MentionViewData> mUsersList2 = null;

    public Mention_UserAdapter(@NonNull Context context, ArrayList<MentionViewData> list1, FragmentManager manager)
    {
        super(context, 0 , list1);
        mContext2 = context;
        mUsersList2 = list1;
        this.fragmentManager2 = manager;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listItem = LayoutInflater.from(mContext2).inflate(R.layout.mention_list_item,parent,false);

        /*MentionViewData currentUser2 = mUsersList2.get(position);
        CircleImageView imageView = (CircleImageView) listItem.findViewById(R.id.Mention_Userprofile);

        String id = currentUser2.getImageURL();
        if(!id.isEmpty()) {
            String imageAddress = mContext2.getResources().getString(R.string.ImageURL) + id;
            Picasso.get().load(imageAddress).into(imageView);
        }

        TextView name = (TextView) listItem.findViewById(R.id.Mention_UserName);
        name.setText(Html.fromHtml(StringEscapeUtils.unescapeJava(currentUser2.getName())));*/

        return listItem;
    }
}
