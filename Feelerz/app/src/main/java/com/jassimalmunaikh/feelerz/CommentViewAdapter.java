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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.apache.commons.text.StringEscapeUtils;

import java.util.ArrayList;


import de.hdodenhof.circleimageview.CircleImageView;

public class CommentViewAdapter extends ArrayAdapter<CommentViewData> implements DeleteConfirmListener {

    CustomLoader loader;
    Context mContext;
    CommentSection owner;
    private ArrayList<CommentViewData> mComments = null;
    FragmentManager fragmentManager;

    LayoutInflater mLayoutInflator;

    public CommentViewAdapter(@NonNull Context context, ArrayList<CommentViewData> list,FragmentManager manager,CommentSection owner) {
        super(context, 0, list);
        mContext = context;
        mComments = list;
        this.fragmentManager = manager;
        this.owner = owner;
        mLayoutInflator = LayoutInflater.from(mContext);


    }

    public void CreateLoader(Fragment owner, View view)
    {
        this.loader = new CustomLoader(owner.getActivity(),(ViewGroup)view);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = mLayoutInflator.inflate(R.layout.comment_layout, parent,false);
        CommentViewData data = mComments.get(position);

        if(!data.profilePicId.isEmpty())
        {
            if (data.ownerId.equals(owner.postCreater) && owner.post_privacy.equals("Anonymous")) {
                CircleImageView imageView = (CircleImageView) view.findViewById(R.id.user_profile_pic);
                imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.default_profile));
                /*String url = mContext.getResources().getString(R.string.ImageURL) + data.profilePicId;
                Picasso.get().load(url).into(imageView);*/
            }else
            {
                CircleImageView imageView = (CircleImageView) view.findViewById(R.id.user_profile_pic);
                String url = mContext.getResources().getString(R.string.ImageURL) + data.profilePicId;
                Picasso.get().load(url).into(imageView);
            }
        }

        TextView nameView = (TextView)view.findViewById(R.id.owner_name);
        if(data.ownerId.equals(owner.postCreater) && owner.post_privacy.equals("Anonymous")){
            nameView.setText("Anonymous");
            Typeface Font = Typeface.createFromAsset(mContext.getAssets(), "fonts/Nunito-Bold.ttf");
            nameView.setTypeface(Font);
        }else {
            nameView.setText(Html.fromHtml(StringEscapeUtils.unescapeJava(data.ownerName)));
            Typeface Font = Typeface.createFromAsset(mContext.getAssets(), "fonts/Nunito-Bold.ttf");
            nameView.setTypeface(Font);
        }
        TextView comment = (TextView)view.findViewById(R.id.comment);
        comment.setText(Html.fromHtml(StringEscapeUtils.unescapeJava(data.comment)));
        Typeface Font2 = Typeface.createFromAsset(mContext.getAssets(), "fonts/nunito-semibold.ttf");
        nameView.setTypeface(Font2);

        TextView timeStamp = (TextView)view.findViewById(R.id.comment_timestamp);
        timeStamp.setText(data.timeStamp);
//        Typeface Font3 = Typeface.createFromAsset(mContext.getAssets(), "fonts/Nunito-Bold.ttf");
//        nameView.setTypeface(Font3);
        final RelativeLayout rlLayout = (RelativeLayout) view.findViewById(R.id.rl_layout);
        rlLayout.setOnTouchListener(new SwipeDismissTouchListener(
                rlLayout,
                null,
                new SwipeDismissTouchListener.OnDismissCallback() {
                    @Override
                    public void onDismiss(View view, Object token) {

                        owner.swipeView(position);
                        // rlLayout.removeView(timeStamp);
                    }
                }));

        SetupProfileView(view,data.ownerId);

        if(data.ownerId.equals(UserDataCache.GetInstance().id))
            SetupDeleteComment(view,data,position);

        return view;

    }


    void SetupDeleteComment(View view, CommentViewData data,int position)
    {
        RelativeLayout commentPart = view.findViewById(R.id.comment_part);
        commentPart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteConfirmDialog bs = new DeleteConfirmDialog();
                Bundle args = new Bundle();
                args.putInt("type",DeleteConfirmDialog.TYPE_DELETE_COMMENT);
                args.putString("contentId",data.commentId);
                args.putInt("position",position);
                args.putString("comment",data.comment);
                bs.setArguments(args);
                bs.SetListener(CommentViewAdapter.this);
                bs.show(fragmentManager,"delete bs dialog");
            }
        });
    }

    void SetupProfileView(View view, final String userId)
    {
        final CommentViewAdapter self = this;

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

    @Override
    public void OnListItemDeletion(int position) {
        mComments.remove(position);
        notifyDataSetChanged();
        UpdateAdapter(-1);
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

    void UpdateAdapter(int offset)
    {
        int index = GlobalCache.GetInstance().postIndex;
        GlobalCache.GetInstance().dataList.get(index).commentCount += offset;
        GlobalCache.GetInstance().adapter.notifyItemChanged(index);
    }
}
