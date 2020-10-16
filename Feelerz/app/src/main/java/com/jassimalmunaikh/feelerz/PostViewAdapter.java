package com.jassimalmunaikh.feelerz;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.text.Spanned;
import android.text.util.Linkify;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.squareup.picasso.Picasso;

import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;


import de.hdodenhof.circleimageview.CircleImageView;

class PostViewData
{
    public String postId;
    public String userImageId;
    public String postImageId;
    public String ownerName;
    public String privacy;
    public int hugCount;
    public int commentCount;
    public int refeelCount;
    public String postText;
    public String colorCode;
    public String feelingText;
    public long timeStampInMS;
    public String timeStamp;
    public String ownerId;
    public String repostUserString;

    String[] refeelers;
    String[] huggers;
    Map<String,String> IdToComments;


    PostViewData()
    {
        postId = "";
        userImageId = "";
        postImageId = "";
        ownerName = "default";
        privacy = "";
        hugCount = 0;
        commentCount = 0;
        refeelCount = 0;
        postText = "";
        repostUserString = "";

    }


    public void SetData(JSONObject postData)
    {
        try {
            String huggersWithCommas = postData.getString("hug");
            this.huggers = huggersWithCommas.split(",");

            String refeelersWithCommas = postData.getString("repost_user");
            this.refeelers = refeelersWithCommas.split(",");

            this.userImageId = postData.getString("profileimage");
            this.postImageId = postData.getString("image");
            this.ownerName = postData.getString("name");
            this.privacy = postData.getString("privacy");
            this.ownerId = postData.getString("user_id");
            this.hugCount = postData.getInt("total_hug");
            this.refeelCount = postData.getInt("total_repost");
            this.commentCount = postData.getInt("comments_count");
            this.postText = postData.getString("post_text");
            this.colorCode = postData.getString("feeling_color");
            this.postId = postData.getString("id");
            this.repostUserString = postData.getString("repost_user_string");
            Spanned temp = Html.fromHtml(StringEscapeUtils.unescapeJava(postData.getString("feeling")));
            this.feelingText = "    " + temp.toString() + "    ";

            String dateAndTime = postData.getString("date_time");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date mDate = new Date();//2019-11-18 17:02:35
            try {
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                 mDate = sdf.parse(dateAndTime);
                this.timeStampInMS = mDate.getTime();
            } catch (ParseException e) {
                Log.e("ERROR_TIMESTAMP",e.getMessage());
            }

            PrettyTime p  = new PrettyTime();
            this.timeStamp = p.format(mDate);

        }
        catch (JSONException e)
        { }
    }

    public void updateHugs(JSONObject response)
    {

        try {
            this.hugCount = response.getInt("total_hug");
            String hugIdsWithCommas = response.getString("hug");
            this.huggers = hugIdsWithCommas.split(",");
        }
        catch (JSONException e){}

    }

    public void updateRefeels(JSONObject response)
    {
        try {
            this.refeelCount = response.getInt("total_repost");
            String withCommas = response.getString("repost_user");
            this.refeelers = withCommas.split(",");
        }
        catch (JSONException e){}

    }



    public Boolean isHuggedById(String userId)
    {
        Boolean found = false;
        for(int index = 0 ; index < this.huggers.length ; ++index)
        {
                if (this.huggers[index].equals(userId)) {
                    found = true;
                    break;
                }

        }
        return found;
    }

    public Boolean isRefeeledById(String userId)
    {
        Boolean found = false;
        for(int index = 0 ; index < this.refeelers.length ; ++index)
        {
            if (this.refeelers[index].equals(userId)) {
                found = true;
                break;
            }

        }
        return found;
    }

}


public class PostViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements DeleteConfirmListener {

    //need this ids to modify hugs,comment & refeel counts
    private final int HUG_TEXT_VIEW_ID = 0;
    private final int COMMENT_TEXT_VIEW_ID = 1;
    private final int REFEEL_TEXT_VIEW_ID = 2;
    private final int VIEW_TYPE_ITEM = 3;
    private final int VIEW_TYPE_LOADING = 4;
    private final int TAG_YOU_REFEELED_TEXT = 5;
    PostViewData currentPost;
    private Context mContext;
    CustomLoader loader;
    RequestHandler requestHandler;
//    Friend_SectionFragment friend_sectionFragment;
    String anPri = "Anonymous";

    private ArrayList<PostViewData> mPostList = null;
    FragmentManager fragmentManager;
    int layoutId = -1;

    Boolean linkProfiles;
    Boolean showMyRefeelsText;
    private String profileType="";

    Typeface myCustomFontt1,myCustomFontt2,myCustomFontt3,myCustomFontt4;

    public PostViewAdapter(@NonNull Context context, ArrayList<PostViewData> list, FragmentManager manager, int layoutId, Boolean linkProfiles) {
        mContext = context;
        mPostList = list;
//        this.friend_sectionFragment.posts11.toString();
        this.fragmentManager = manager;
        this.layoutId = layoutId;
        this.linkProfiles = linkProfiles;
        this.showMyRefeelsText = false;

    }

    public void CreateLoader(Fragment owner,View view)
    {
        this.loader = new CustomLoader(owner.getActivity(),(ViewGroup)view);
    }



    void EnableRefeelText(String type)
    {
        this.showMyRefeelsText = true;
        this.profileType=type;
    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_layout, parent, false);
            return new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_layout_progress_bar, parent, false);
            return new LoadingViewHolder(view);
        }
    }



    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        if(holder instanceof  ViewHolder) {
            ViewHolder postViewHolder = (ViewHolder)holder;
            LinearLayout.LayoutParams pm = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            View postView = postViewHolder.postView;
            postView.setLayoutParams(pm);
            currentPost = mPostList.get(position);


            // adds "you refeeled" on top of post
            if(showMyRefeelsText)
                CheckAndAddRefeelText((ViewGroup)postView,currentPost,profileType);

            CircleImageView profilePic = (CircleImageView) postView.findViewById(R.id.profile_image_post_view);
            String anPri = "Anonymous";
            //top panel
            if (!currentPost.userImageId.isEmpty()) {

                if(currentPost.privacy.equals(anPri)){
                    profilePic.setImageDrawable(mContext.getResources().getDrawable(R.drawable.default_profile));
                }
                else{
                    String url = mContext.getResources().getString(R.string.ImageURL) + currentPost.userImageId;
                    Picasso.get().load(url).into(profilePic);
                }
            } else {
                profilePic.setImageDrawable(mContext.getResources().getDrawable(R.drawable.default_profile));
            }

            TextView nameView = postView.findViewById(R.id.name_post_view);


            if (currentPost.privacy.equals(anPri)) {
            nameView.setText(anPri);
            }else {
                nameView.setText(Html.fromHtml(StringEscapeUtils.unescapeJava(currentPost.ownerName)));
            }
            Typeface Font = Typeface.createFromAsset(mContext.getAssets(), "fonts/Nunito-Bold.ttf");
            nameView.setTypeface(Font);

            TextView timeStampView = (TextView) postView.findViewById(R.id.time_stamp);
            timeStampView.setText(currentPost.timeStamp);

            Typeface Font1 = Typeface.createFromAsset(mContext.getAssets(), "fonts/Nunito-Regular.ttf");
            timeStampView.setTypeface(Font1);

            TextView feelingText = (TextView) postView.findViewById(R.id.feeling_text);
            feelingText.setText("    " + Html.fromHtml(StringEscapeUtils.unescapeJava(currentPost.feelingText)) + "    ");

            Typeface Font2 = Typeface.createFromAsset(mContext.getAssets(), "fonts/Nunito-Regular.ttf");
            feelingText.setTypeface(Font2);

            View feelingShape = (View) postView.findViewById(R.id.feeling_shape);
            feelingShape.getBackground().setTint(Color.parseColor(currentPost.colorCode));

            //posts

            String postText = currentPost.postText;

            TextView postTextView = postView.findViewById(R.id.post_text);
            postTextView.setText(Html.fromHtml(StringEscapeUtils.unescapeJava(postText)));

         //   postTextView.setText(StringEscapeUtils.unescapeJava(postText));
            Linkify.addLinks(postTextView,Linkify.ALL);

            Typeface Fontt = Typeface.createFromAsset(mContext.getAssets(), "fonts/Nunito-Regular.ttf");
            postTextView.setTypeface(Fontt);

            Typeface Font3 = Typeface.createFromAsset(mContext.getAssets(), "fonts/Nunito-Regular.ttf");
            feelingText.setTypeface(Font3);

            ImageView postImageView = (ImageView) postView.findViewById(R.id.post_image);

            CardView.LayoutParams imagePm = new CardView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
            postImageView.setLayoutParams(imagePm);

            if (!currentPost.postImageId.isEmpty()) {
                int heightInDP = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 350, mContext.getResources().getDisplayMetrics());
                postImageView.getLayoutParams().height = heightInDP;
                String url = mContext.getResources().getString(R.string.PostURL) + currentPost.postImageId;
                Picasso.get().load(url).into(postImageView);
            } else {

                postImageView.setImageBitmap(null);
                postImageView.invalidate();
            }

            //bottom panel

            LinearLayout postBottomPanel = postView.findViewById(R.id.post_bottom_panel);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.leftMargin = 10;
            params.rightMargin = 10;
            float fontSize = 14;

            postBottomPanel.removeAllViews();

            if (currentPost.hugCount > 0) {
                AddTextView(postBottomPanel, currentPost.hugCount, " Hug", HUG_TEXT_VIEW_ID);
            }

            if (currentPost.commentCount > 0) {
                AddTextView(postBottomPanel, currentPost.commentCount, " Comment", COMMENT_TEXT_VIEW_ID);
            }

            if (currentPost.refeelCount > 0) {
                AddTextView(postBottomPanel, currentPost.refeelCount, " Refeel", REFEEL_TEXT_VIEW_ID);
            }


            //SETTING UP LIKE,COMMENT AND REFEEL

            SetupLike(postView, currentPost, postBottomPanel);
            SetupRefeel(postView, currentPost, postBottomPanel);
            SetupComment(postView, currentPost,position);
            if (linkProfiles)
                if (currentPost.privacy.equals(anPri)){

                }else{
                    SetupProfileView(postView, currentPost.ownerId);
                }

            if (!currentPost.postImageId.isEmpty())
                SetupImagePreview(postView, currentPost);

            SetupPostAction(postView,currentPost,position);
        }

    }

    @Override
    public int getItemCount()
    {
        return mPostList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mPostList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }


    class ViewHolder extends RecyclerView.ViewHolder
    {
        View postView;
        public ViewHolder(View itemView) {
            super(itemView);
            this.postView = itemView;
        }
    }


    class LoadingViewHolder extends RecyclerView.ViewHolder
    {
        public LoadingViewHolder(View itemView) {
            super(itemView);
        }
    }

    @SuppressLint("RtlHardcoded")
    void CheckAndAddRefeelText(ViewGroup postView, PostViewData currentPost, String profileType)
    {
        TextView view = postView.findViewById(TAG_YOU_REFEELED_TEXT);

        if(currentPost.isRefeeledById(UserDataCache.GetInstance().id) ) {
            if(view == null) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                TextView youRefeeled = new TextView(mContext);
                Typeface Font3 = Typeface.createFromAsset(mContext.getAssets(), "fonts/Nunito-Regular.ttf");
                youRefeeled.setTypeface(Font3);
                if(profileType.equals("profile"))
                {
                    youRefeeled.setText("@You Refeeled");
                }else if(profileType.equals("friend")&& !currentPost.repostUserString.equals(""))
                {
                    youRefeeled.setText("@" +Html.fromHtml(StringEscapeUtils.unescapeJava(currentPost.repostUserString)) +   " Refeeled");
                }else
                {
                    postView.removeView(view);
                }

                youRefeeled.setId(TAG_YOU_REFEELED_TEXT);
                youRefeeled.setTextSize(12);
                params.gravity = Gravity.LEFT;
                params.topMargin = 20;
                params.leftMargin = 220;
                youRefeeled.setLayoutParams(params);
                postView.addView(youRefeeled, 0);

            }else
            {

            }

        }
        else
        {

            if (view != null) {
                postView.removeView(view);
            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            TextView youRefeeled = new TextView(mContext);
//            youRefeeled.setGravity(0);
            Typeface Font3 = Typeface.createFromAsset(mContext.getAssets(), "fonts/Nunito-Regular.ttf");
            youRefeeled.setTypeface(Font3);
            if (profileType.equals("friend") && !currentPost.repostUserString.equals("")) {
                youRefeeled.setText("@" +Html.fromHtml(StringEscapeUtils.unescapeJava(currentPost.repostUserString))+ " Refeeled");
            }
            youRefeeled.setId(TAG_YOU_REFEELED_TEXT);
            youRefeeled.setTextSize(12);

            params.gravity = Gravity.START;
            params.topMargin = 20;
            params.leftMargin = 220;
            youRefeeled.setLayoutParams(params);
            postView.addView(youRefeeled, 0);

        }
    }

    void SetupProfileView(View view, final String userId)
    {
        final PostViewAdapter self = this;

        View r1 = view.findViewById(R.id.Profile_link);
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

    private void AddTextView(LinearLayout parent,int value,String postfix,int tag)
    {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.rightMargin = 10;
        params.leftMargin = 10;
        float fontSize = 14;

        TextView likeText = new TextView(mContext);
        Typeface Font3 = Typeface.createFromAsset(mContext.getAssets(), "fonts/Nunito-Regular.ttf");
        likeText.setTypeface(Font3);
        likeText.setTag(tag);
        likeText.setLayoutParams(params);
        likeText.setTextSize(fontSize);
        likeText.setText(value + postfix);
        parent.addView(likeText);

    }

    private void RemoveTextView(LinearLayout parent,int tag)
    {
        View view = parent.findViewWithTag(tag);
        if(view != null)
            parent.removeView(view);
    }



    private void ToggleImage(ImageView view,int off,int on,Boolean condition)
    {
        view.setImageResource(condition ? on : off);
    }


    private void ToggleImageWithAnimation(final ImageView view,int off,int on,Boolean condition)
    {
        view.setImageResource(condition ? on : off);
        if(condition)
        {
            view.setScaleX(0.0f);
            view.setScaleY(0.0f);
            PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX",1.0f);
            PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY",1.0f);
            ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(view,scaleX,scaleY);
            BounceInterpolator interpolator = new BounceInterpolator();
            animator.setInterpolator(interpolator);
            animator.setDuration(550);
            animator.start();

            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {

                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });



        }
    }


    private void SetupLike(final View view, final PostViewData viewData,final LinearLayout bottomPanel)
    {

        final RelativeLayout button = (RelativeLayout) view.findViewById(R.id.hug_button);
        final ImageView ib = (ImageView) view.findViewById(R.id.hug_image);
        Boolean hasHugged = viewData.isHuggedById(UserDataCache.GetInstance().id);
        ToggleImage(ib,R.drawable.hearts454,R.drawable.heart2,hasHugged);

        TextView hugtext = view.findViewById(R.id.Hug_txt);
        TextView Commenttxt = view.findViewById(R.id.comment_txt);
        TextView Refeeltxt = view.findViewById(R.id.refeel_txt);

        Typeface Font4 = Typeface.createFromAsset(mContext.getAssets(), "fonts/Nunito-Regular.ttf");
        Commenttxt.setTypeface(Font4);

        Typeface Font5 = Typeface.createFromAsset(mContext.getAssets(), "fonts/Nunito-Regular.ttf");
        hugtext.setTypeface(Font5);

        Typeface Font6 = Typeface.createFromAsset(mContext.getAssets(), "fonts/Nunito-Regular.ttf");
        Refeeltxt.setTypeface(Font6);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setEnabled(false);
                ///////////////////////////

                Boolean hasHugged = viewData.isHuggedById(UserDataCache.GetInstance().id);
                ToggleImageWithAnimation(ib,R.drawable.hearts454,R.drawable.heart2,!hasHugged);

                String forget_url = mContext.getString(R.string.Link)+"hugs";
                JSONObject request = new JSONObject();
                try
                {
                    String userId = UserDataCache.GetInstance().id;
                    request.put("post_id",viewData.postId);
                    request.put("user_id",userId);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
                JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.POST, forget_url, request, new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getInt("status") == 1) {

                                button.setEnabled(true);
                                viewData.updateHugs(response);

                                if(viewData.hugCount == 0)
                                    RemoveTextView(bottomPanel,HUG_TEXT_VIEW_ID);
                                if(viewData.hugCount >= 1)
                                {
                                    TextView view = bottomPanel.findViewWithTag(HUG_TEXT_VIEW_ID);
                                    if(view != null )
                                        view.setText(viewData.hugCount + " hug");
                                    else
                                        AddTextView(bottomPanel,viewData.hugCount," hug",HUG_TEXT_VIEW_ID);
                                }

                                String userId = UserDataCache.GetInstance().id;
                                Boolean hasHugged = viewData.isHuggedById(UserDataCache.GetInstance().id);
                                ToggleImage(ib,R.drawable.hearts454,R.drawable.heart2,hasHugged);

                            }
                            else {

                            }
                        }catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Display error message whenever an error occurs

                    }
                });

                jsArrayRequest.setRetryPolicy(
                        new DefaultRetryPolicy(
                                0,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                        )
                );
                MySingleton.getInstance(mContext).addToRequestQueue(jsArrayRequest);

                //////////////////////////
            }
        });
    }


    private void SetupRefeel(final View view, final PostViewData viewData,final LinearLayout bottomPanel)
    {
        final RelativeLayout button = (RelativeLayout) view.findViewById(R.id.refeel_button);
        final ImageView ib = (ImageView)view.findViewById(R.id.refeel_image);
        Boolean hasRefeeled = viewData.isRefeeledById(UserDataCache.GetInstance().id);
        ToggleImage(ib,R.drawable.refeel_icon2,R.drawable.refeelblues452,hasRefeeled);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setEnabled(false);

                Boolean hasDone = viewData.isRefeeledById(UserDataCache.GetInstance().id);
                ToggleImageWithAnimation(ib,R.drawable.refeel_icon2,R.drawable.refeelblues452,!hasDone);

                ///////////////////////////

                String forget_url = mContext.getString(R.string.Link)+"repost_comment";
                JSONObject request = new JSONObject();
                try
                {
                    String userId = UserDataCache.GetInstance().id;
                    request.put("post_id",viewData.postId);
                    request.put("user_id",userId);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
                JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.POST, forget_url, request, new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getInt("status") == 1) {

                                button.setEnabled(true);
                                viewData.updateRefeels(response);

                                if(viewData.refeelCount == 0)
                                    RemoveTextView(bottomPanel,REFEEL_TEXT_VIEW_ID);
                                notifyDataSetChanged();
                                if(viewData.refeelCount >= 1)
                                {
                                    TextView view = bottomPanel.findViewWithTag(REFEEL_TEXT_VIEW_ID);
                                    if(view != null )
                                        view.setText(viewData.refeelCount + " Refeel");
                                    else
                                        AddTextView(bottomPanel,viewData.refeelCount," Refeel",REFEEL_TEXT_VIEW_ID);
                                }

                                String userId = UserDataCache.GetInstance().id;
                                Boolean hasRefeeled = viewData.isRefeeledById(UserDataCache.GetInstance().id);
                                ToggleImage(ib,R.drawable.refeel_icon2,R.drawable.refeelblues452,hasRefeeled);
                                notifyDataSetChanged();

                            }else {

                            }
                        }catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Display error message whenever an error occurs

                    }
                });

                jsArrayRequest.setRetryPolicy(
                        new DefaultRetryPolicy(
                                0,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                        )
                );
                MySingleton.getInstance(mContext).addToRequestQueue(jsArrayRequest);

                //////////////////////////
            }
        });
    }

    private void SetupComment(final View postViewData, final PostViewData post, final int position)
    {
        final PostViewAdapter self = this;

        final RelativeLayout button = (RelativeLayout) postViewData.findViewById(R.id.comment_button);
        button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        GlobalCache.GetInstance().postIndex = position;
                        GlobalCache.GetInstance().adapter = self;
                        GlobalCache.GetInstance().dataList = mPostList;
                        Bundle data = new Bundle();
                        data.putString("post_id",post.postId);
                        data.putInt("hugs",post.hugCount);
                        data.putInt("comments",post.commentCount);
                        data.putInt("refeels",post.refeelCount);
                        data.putString("post_creater",post.ownerId);
                        data.putString("post_privacy",post.privacy);
                        CommentSection fragment = new CommentSection();
                        fragment.setArguments(data);
                        FragmentManager fragmentManager = self.fragmentManager;
                        fragmentManager.beginTransaction().add(R.id.Main_Layout, fragment).addToBackStack(null).commit();
                    }
                }
        );

    }

    private void SetupImagePreview(View view,final PostViewData data)
    {
        final PostViewAdapter self = this;
        ImageView image = (ImageView) view.findViewById(R.id.post_image);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("postImageId",data.postImageId);
                ImagePreview fragment = new ImagePreview();
                fragment.setArguments(bundle);
                FragmentManager fragmentManager = self.fragmentManager;
                fragmentManager.beginTransaction().add(R.id.Main_Layout, fragment).addToBackStack(null).commit();
            }
        });
    }

    private void SetupPostAction(View view ,final PostViewData data,final int position)
    {
        final PostViewAdapter self = this;
        String userId = UserDataCache.GetInstance().id;

        final Button dots = (Button)view.findViewById(R.id.more_post);
        dots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(!userId.equals(data.ownerId))
                {
                    Bundle bundle = new Bundle();
                    bundle.putString("postid",data.postId);

                    ReportFragment fragment = new ReportFragment();
                    fragment.setArguments(bundle);
                    FragmentManager fragmentManager = self.fragmentManager;
                    fragmentManager.beginTransaction().add(R.id.Main_Layout, fragment).addToBackStack(null).commit();

                }   else

                    {
                    DeleteConfirmDialog bs = new DeleteConfirmDialog();
                    Bundle args = new Bundle();
                    args.putInt("type",DeleteConfirmDialog.TYPE_DELETE_POST);
                    args.putString("contentId",data.postId);
                    args.putInt("position",position);
                    bs.setArguments(args);
                    bs.SetListener(PostViewAdapter.this);
                    bs.show(fragmentManager,"delete bs dialog");
                }
                    return;

            }
        });


    }

    @Override
    public void OnListItemDeletion(int position) {
        mPostList.remove(position);
        notifyItemRemoved(position);
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
