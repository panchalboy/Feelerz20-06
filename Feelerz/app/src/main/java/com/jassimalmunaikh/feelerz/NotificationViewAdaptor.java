package com.jassimalmunaikh.feelerz;

import android.content.Context;
import android.graphics.Typeface;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationViewAdaptor extends ArrayAdapter<NotificationData> implements ServerCallObserver, DeleteConfirmListener {

    CustomLoader loader;
    Context mContext;
    private ArrayList<NotificationData> mNotification = null;
    FragmentManager fragmentManager;
    NotificationFragment owner;
    RequestHandler requestHandler;
    PostViewAdapter postViewAdapter;

    public NotificationViewAdaptor(@NonNull Context context, ArrayList<NotificationData> list, FragmentManager manager, NotificationFragment owner) {
        super(context, 0, list);
        mContext = context;
        mNotification = list;
        this.fragmentManager = manager;
        this.owner = owner;

    }
    public void CreateLoader(Fragment owner, View view)
    {
        this.loader = new CustomLoader(owner.getActivity(),(ViewGroup)view);
    }


    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.notification_layout, parent, false);

        final NotificationData data = mNotification.get(position);

        if (!data.profilePicId.isEmpty()) {
            CircleImageView imageView = (CircleImageView) view.findViewById(R.id.profile_pic);
            String url = mContext.getResources().getString(R.string.ImageURL) + data.profilePicId;
            Picasso.get().load(url).into(imageView);
        }
// for notification post

        requestHandler = new RequestHandler(getContext(), this);

        TextView message = (TextView) view.findViewById(R.id.notification_text);
        String messagetxt = ""+(Html.fromHtml(StringEscapeUtils.unescapeJava(data.ownerName))) +" "+ Html.fromHtml(StringEscapeUtils.unescapeJava(data.message));
        message.setText(messagetxt);
        Typeface Font = Typeface.createFromAsset(mContext.getAssets(), "fonts/Nunito-Bold.ttf");
        message.setTypeface(Font);

        TextView timeStamp = (TextView) view.findViewById(R.id.notification_time);
        timeStamp.setText(data.timeStamp);
        timeStamp.setTypeface(Font);

        ImageView more_option = (ImageView) view.findViewById(R.id.notification_option);
        more_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeleteConfirmDialog bs = new DeleteConfirmDialog();
                Bundle args = new Bundle();
                args.putInt("type", DeleteConfirmDialog.TYPE_DELETE_NOTIFICATION);
                args.putString("contentId", data.notificationId);
                args.putInt("position", position);
                bs.setArguments(args);
                bs.SetListener(NotificationViewAdaptor.this);
                bs.show(fragmentManager, "delete bs dialog");
            }
        });

        //Also needed to implement imageView for delete or some more options
        SetupNotificationView(view, data);

        SetupPostNotification(view,data.postId);

        return view;

    }

    void SetupPostNotification(View view, final String postId)
    {
        final NotificationViewAdaptor self = this;

        View r1 = view.findViewById(R.id.notification_part);
        r1.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!postId.equals("")) {
                            Bundle data = new Bundle();
                            data.putString("post_id",postId);
                            Fragment fragment = null;
                            fragment = new NotificationPostFragment();
                            fragment.setArguments(data);
                            FragmentManager fragmentManager = self.fragmentManager;
                            fragmentManager.beginTransaction().add(R.id.Main_Layout, fragment).addToBackStack(null).commit();
                        }
                    }
                }
        );
    }


    void SetupNotificationView(View view, final NotificationData data) {
        final NotificationViewAdaptor self = this;

        View r1 = view.findViewById(R.id.notification_part);
        r1.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Get Notification from notification list
                        if (data.Type.equals("2")) {
                            // fragmentManager.beginTransaction().add(R.id.postLayout, new NotificationPostFragment(data.postId)).addToBackStack(null).commit();
                        }
                    }
                }
        );
    }


    @Override
    public void OnSuccess(JSONObject response, String apiName) {

    }

    @Override
    public void OnFailure(String errorMessage, String apiName) {

    }

    @Override
    public void OnNetworkError() {

    }

    @Override
    public void OnListItemDeletion(int position)
    {
        mNotification.remove(position);
        notifyDataSetChanged();
        this.loader.hide();
    }

    @Override
    public void OnDeletionInProgress() {
        this.loader.show();
    }

    @Override
    public void OnDeletionFailed() {
        this.loader.hide();
    }
}
