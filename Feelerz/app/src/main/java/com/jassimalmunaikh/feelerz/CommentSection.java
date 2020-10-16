package com.jassimalmunaikh.feelerz;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.otaliastudios.autocomplete.Autocomplete;
import com.otaliastudios.autocomplete.AutocompleteCallback;
import com.otaliastudios.autocomplete.AutocompletePolicy;
import com.otaliastudios.autocomplete.AutocompletePresenter;
import com.otaliastudios.autocomplete.CharPolicy;
import com.wdullaer.swipeactionadapter.SwipeActionAdapter;

import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

//import com.jassimalmunaikh.feelerz.presenter.UserPresenter;
//import com.jassimalmunaikh.feelerz.presenter.Users;


public class CommentSection extends Fragment implements ServerCallObserver, TopLevelFrag,
        DeleteConfirmListener {
    Typeface myCustomFontt1, myCustomFontt2, myCustomFontt3, myCustomFontt4;
    View view1;
    ListView mCommentsList, MentionList;
    ArrayList<CommentViewData> mList;
    RequestHandler requestHandler;
    ImageButton postCommmentBtn;
    EditText commentBox;
    protected SwipeActionAdapter mAdapterSwipe;
    CustomLoader loader;
    int hugCount;
    int commentCount;
    int refeelCount;
    private final int HUG_TEXT_VIEW_ID = 0;
    private final int COMMENT_TEXT_VIEW_ID = 1;
    private final int REFEEL_TEXT_VIEW_ID = 2;
    CommentViewAdapter mAdapter;
    String postCreater;
    String post_privacy;
    boolean SpinnerUserMention;
    String comment;
    FragmentManager fragmentManager;
    //    Mention_UserAdapter customAdapter2 = null;
    public static ArrayList<Users> friendList = null;
    //ArrayList<String> friendListString = null;
    private Autocomplete mentionsAutocomplete;
    String mPostId;


    public CommentSection() {
        // Required empty public constructor
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_comment_section, container, false);
        this.view1 = view;
        SettextSytle();


        this.SpinnerUserMention = false;
        friendList = new ArrayList<Users>();
        // friendListString = new ArrayList<String>();
        //    customAdapter2 = new Mention_UserAdapter(getContext(),friendList,getFragmentManager());


        GlobalFragmentStack.getInstance().Register(this);

        this.loader = new CustomLoader(getActivity(), (ViewGroup) view);
        requestHandler = new RequestHandler(getContext(), this);

        GetMentionList();

        commentBox = view.findViewById(R.id.comment_box_field);
        setupMentionsAutocomplete();

//
//        commentBox.setOnItemClickListener((parent, view2, position, rowId) -> {
//            String selection = (String)parent.getItemAtPosition(position);
//            System.out.println("section===" + selection);
//
//            //TODO Do something with the selected text
//        });


        this.postCommmentBtn = (ImageButton) view.findViewById(R.id.post_comment_btn);

        mList = new ArrayList<CommentViewData>();
        mAdapter = new CommentViewAdapter(getContext(), mList, getActivity().getSupportFragmentManager(), this);
        mAdapter.CreateLoader(this, view);
        mCommentsList = (ListView) view.findViewById(R.id.comments);
        mCommentsList.setAdapter(mAdapter);


        this.mPostId = getArguments().getString("post_id");
        this.postCreater = getArguments().getString("post_creater");
        this.post_privacy = getArguments().getString("post_privacy");

        hugCount = getArguments().getInt("hugs", 0);
        commentCount = getArguments().getInt("comments", 0);
        refeelCount = getArguments().getInt("refeels", 0);


        ImageView Back_Btn = view.findViewById(R.id.Back_Btn);
        Back_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnManualClose();
                removePhoneKeypad();
            }
        });

//        TextView hugsCountTex = view.findViewById(R.id.comment_hugs_count_button);
        LinearLayout postBottomPanel = view.findViewById(R.id.post_bottom_panel);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = 10;
        params.rightMargin = 10;
        float fontSize = 14;
        postBottomPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle data = new Bundle();
                data.putString("tabOneTitle", "Hugs");
                data.putString("tabTwoTitle", "Refeels");
                data.putString("postId", mPostId);
                GenericTabsFragment fragment = new GenericTabsFragment();
                fragment.setArguments(data);
                getFragmentManager().beginTransaction().add(R.id.Main_Layout, fragment).commit();
            }
        });

        postBottomPanel.removeAllViews();

        if (refeelCount > 0) {
            AddTextView(postBottomPanel, refeelCount, " Refeel", REFEEL_TEXT_VIEW_ID);
        }

        if (hugCount > 0) {
            AddTextView(postBottomPanel, hugCount, " Hug", HUG_TEXT_VIEW_ID);
        }

//        if (commentCount > 0) {
//            AddTextView(postBottomPanel, commentCount, " Comment", COMMENT_TEXT_VIEW_ID);
//        }


        this.postCommmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comment = commentBox.getText().toString();
                comment = Html.fromHtml(StringEscapeUtils.escapeJava(comment)).toString();
//                comment = "<html><font color=\"blue\">" +comment + "</font></html>";
                commentBox.setText("");

                postCommmentBtn.setEnabled(false);
                postCommmentBtn.setVisibility(View.INVISIBLE);
                registerCommentOnServer(comment);
            }
        });

        populateCommentSection();


        return view;
    }

    void EnableSpinner() {
        this.SpinnerUserMention = true;
    }

    private void setupMentionsAutocomplete() {
        //EditText edit = findViewById(R.id.multi);
        float elevation = 6f;
        Drawable backgroundDrawable = new ColorDrawable(Color.WHITE);
        AutocompletePolicy policy = new CharPolicy('@'); // Look for @mentions
        AutocompletePresenter<Users> presenter = new UserPresenter(getContext());
        AutocompleteCallback<Users> callback = new AutocompleteCallback<Users>() {
            @Override
            public boolean onPopupItemClicked(@NonNull Editable editable, @NonNull Users item) {
                // Replace query text with the full name.
                int[] range = CharPolicy.getQueryRange(editable);
                if (range == null) return false;
                int start = range[0];
                int end = range[1];
                String replacement = item.getUsername() + " ";
                editable.replace(start, end, replacement);
                // This is better done with regexes and a TextWatcher, due to what happens when
                // the user clears some parts of the text. Up to you.
                editable.setSpan(new StyleSpan(Typeface.BOLD), start, start + replacement.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                return true;
            }

            public void onPopupVisibilityChanged(boolean shown) {
            }
        };

        mentionsAutocomplete = Autocomplete.<Users>on(commentBox)
                .with(elevation)
                .with(backgroundDrawable)
                .with(policy)
                .with(presenter)
                .with(callback)
                .build();
    }

    private void AddTextView(LinearLayout parent, int value, String postfix, int tag) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.rightMargin = 10;
        params.leftMargin = 10;
        float fontSize = 17;

        TextView likeText = new TextView(getContext());
        Typeface Font3 = Typeface.createFromAsset(getContext().getAssets(), "fonts/Nunito-Regular.ttf");
        likeText.setTypeface(Font3);
        likeText.setTag(tag);
        likeText.setLayoutParams(params);
        likeText.setTextSize(fontSize);
        likeText.setTextColor(Color.parseColor("#ff0099cc"));
        likeText.setText(value + postfix);
        parent.addView(likeText);

    }

    public void removePhoneKeypad() {
        InputMethodManager inputManager = (InputMethodManager) view1
                .getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        IBinder binder = view1.getWindowToken();
        inputManager.hideSoftInputFromWindow(binder,
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void SettextSytle() {

        myCustomFontt1 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Klavika-Regular.ttf");
        myCustomFontt2 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Nunito-Bold.ttf");
        myCustomFontt3 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Nunito-Regular.ttf");
        myCustomFontt4 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/nunito-semibold.ttf");
    }

    private void GetMentionList() {
        loader.show();
        Map<String, String> request1 = new HashMap<String, String>();
        request1.put("user_id", UserDataCache.GetInstance().id);
        request1.put("post_id", this.mPostId);
        requestHandler.MakeRequest(request1, "get_TagList");
    }


    private void registerCommentOnServer(String comment) {
        loader.show();
        Map<String, String> request = new HashMap<String, String>();
        request.put("user_id", UserDataCache.GetInstance().id);
        request.put("post_id", this.mPostId);
        request.put("comment", comment);
        requestHandler.MakeRequest(request, "add_comment");
    }

    private String GetFromattedDate() {
        SimpleDateFormat date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
        date.setTimeZone(TimeZone.getTimeZone("UTC"));
        return date.format(new Date());
    }


    void UpdateAdapter(int offset) {
        int index = GlobalCache.GetInstance().postIndex;
        GlobalCache.GetInstance().dataList.get(index).commentCount += offset;
        GlobalCache.GetInstance().adapter.notifyItemChanged(index);
    }

    private void noInternetPopUp() {
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

        if (apiName == "get_comment") {
            mList.clear();
            mAdapter.notifyDataSetChanged();
            mAdapter.notifyDataSetInvalidated();
            try {
                JSONArray comments = response.getJSONArray("data");
                JSONObject postObject = response.getJSONObject("post");
                for (int i = 0; i < comments.length(); i++) {
                    JSONObject comment = comments.getJSONObject(i);
                    CommentViewData newCommentData = new CommentViewData();
                    String userName;
                    if (postObject.getString("privacy").equals("Anonymous")) {
                        userName = "anonymous";
                    } else {

                        userName = comment.getString("userName");
                    }
                    comment.put("userName", userName);
                    newCommentData.SetData(comment, false);
                    mList.add(newCommentData);
                }
            } catch (JSONException e) {
            }

            mAdapter.notifyDataSetChanged();

        } else if (apiName == "add_comment") {
            try {
                JSONObject comment = response.getJSONObject("data");
                CommentViewData newCommment = new CommentViewData();
                newCommment.SetData(comment, true);
                mList.add(newCommment);

                UpdateAdapter(1);

                postCommmentBtn.setEnabled(true);
                postCommmentBtn.setVisibility(View.VISIBLE);

            } catch (JSONException e) {
            }

            mAdapter.notifyDataSetChanged();

        }
        /*else if(apiName == "delete_comment")
        {
            mList.removeIf(new Predicate<CommentViewData>() {
                @Override
                public boolean test(CommentViewData commentViewData) {
                    return commentViewData.commentId == GlobalCache.GetInstance().selectedCommentId;
                }
            });

            UpdateAdapter(-1);

            mAdapter.notifyDataSetChanged();
        }*/
        else {
            try {
                friendList.clear();
                JSONArray users = response.getJSONArray("data");
                JSONObject postObject = response.getJSONObject("post");

                for (int i = 0; i < users.length(); i++) {
                    JSONObject user = users.getJSONObject(i);
                    String name = user.getString("name");
                    String imageId = user.getString("profileimage");
                    String userName;
                    if (postObject.getString("privacy").equals("Anonymous")) {
                        userName = "anonymous";
                    } else {

                        userName = user.getString("userName");
                    }

                    System.out.println("name ====" + Html.fromHtml(StringEscapeUtils.unescapeJava(name)));

                    friendList.add(new Users(name, imageId, userName));

                }


            } catch (JSONException e) {
            }
        }

        loader.hide();
    }

    @Override
    public void OnFailure(String errorMessage, String apiName) {

        postCommmentBtn.setEnabled(true);
        postCommmentBtn.setVisibility(View.VISIBLE);
        loader.SetErrorColor();
    }

    @Override
    public void OnNetworkError() {
        noInternetPopUp();
        loader.hide();
    }

    private void populateCommentSection() {
        loader.show();
        Map<String, String> request = new HashMap<String, String>();
        request.put("user_id", UserDataCache.GetInstance().id);
        request.put("post_id", this.mPostId);
        requestHandler.MakeRequest(request, "get_comment");
    }

    @Override
    public void OnManualClose() {
        GlobalFragmentStack.getInstance().Unregister(CommentSection.this);
        Close();
    }

    @Override
    public void Close() {
        getFragmentManager().beginTransaction().remove(CommentSection.this).commit();
    }


    @Override
    public void OnListItemDeletion(int position) {
        UpdateAdapter(-1);
        mList.remove(position);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void OnDeletionInProgress() {

    }

    @Override
    public void OnDeletionFailed() {

    }

    public void swipeView(int position) {

        CommentViewData data = mList.get(position);
        if (data.ownerId.equals(UserDataCache.GetInstance().id)) {
            DeleteConfirmDialog bs = new DeleteConfirmDialog();
            Bundle args = new Bundle();
            args.putInt("position", position);
            args.putInt("type", DeleteConfirmDialog.TYPE_DELETE_COMMENT);
            args.putString("contentId", data.commentId);
            args.putString("comment", data.comment);
            bs.setArguments(args);
            bs.SetListener(CommentSection.this);
            bs.show(getFragmentManager(), "delete bs dialog");
        } else {
            String ownerName = "";
            if (data.ownerUserName == null || data.ownerUserName.equals("null") || data.ownerUserName.equals("") || data.ownerUserName.isEmpty()) {
//                ownerName = "";
                /*Do Nothing...*/
            } else {
                ownerName = "@" + StringEscapeUtils.unescapeJava(data.ownerUserName);

                ownerName = ownerName.replace(" ", "");
                commentBox.setEnabled(true);
//                                        ownerName = "<html> <font color =\"blue\" >" + ownerName + "</font> </html>";
                commentBox.append(Html.fromHtml(ownerName));
                commentBox.setFocusable(true);
                Utilities.getInstance().ShowKeyboard(getActivity());

            }
        }

    }

}
