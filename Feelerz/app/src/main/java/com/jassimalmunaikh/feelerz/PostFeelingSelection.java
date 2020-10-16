package com.jassimalmunaikh.feelerz;


import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.IBinder;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class PostFeelingSelection extends Fragment implements ServerCallObserver {

    Typeface myCustomFontt1,myCustomFontt2,myCustomFontt3,myCustomFontt4;
    TextView createPostSearchbg,createPostText;
    View view1;
    RequestHandler requestHandler;
    PostViewAdapter postViewAdapter;
    ArrayList<PostViewData> dataList;
    RecyclerView listView;
    SelectedFeelingTrendPage selectedFeelingTrendPage = new SelectedFeelingTrendPage();

    ArrayList<View> mFeelings = new ArrayList<View>();

    public PostFeelingSelection() {
        // Required empty public constructor
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(hidden)
        {
            getFragmentManager().beginTransaction().remove(selectedFeelingTrendPage).commit();
        }
        else
        {
            AnimateFeelings();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_post_feeling_selection, container, false);
        this.view1 = v;
        SettextSytle();
        createPostSearchbg = v.findViewById(R.id.createPostSearchbg);
        createPostText = v.findViewById(R.id.createPostText);

        createPostSearchbg.setTypeface(myCustomFontt3);
        createPostText.setTypeface(myCustomFontt2);

        this.requestHandler = new RequestHandler(getContext(),this);

        this.dataList = new ArrayList<PostViewData>();
        FragmentManager manager = getActivity().getSupportFragmentManager();
        postViewAdapter = new PostViewAdapter(getContext(),dataList,manager,R.id.public_posts_view_id,false);
        postViewAdapter.CreateLoader(this,v);
        listView =  (RecyclerView) v.findViewById(R.id.friends_post_samples);
        listView.setAdapter(postViewAdapter);
        listView.setClickable(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext()){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };

        listView.setLayoutManager(layoutManager);

        DividerItemDecoration itemDecorator = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.divider2));
        listView.addItemDecoration(itemDecorator);

        View sad = v.findViewById(R.id.feeling1);
        sad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnSelection("Sad");
            }
        });
        mFeelings.add(sad);


        View mad = v.findViewById(R.id.feeling2);
        mad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnSelection("Mad");
            }
        });
        mFeelings.add(mad);


        View happy = v.findViewById(R.id.feeling3);
        happy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnSelection("Happy");
            }
        });
        mFeelings.add(happy);


        View excited = v.findViewById(R.id.feeling4);
        excited.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnSelection("Excited");
            }
        });
        mFeelings.add(excited);


        View bored = v.findViewById(R.id.feeling5);
        bored.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnSelection("Bored");
            }
        });
        mFeelings.add(bored);

        AnimateFeelings();
        FillView();

        return v;

    }

    public void removePhoneKeypad() {
        InputMethodManager inputManager = (InputMethodManager) view1
                .getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        IBinder binder = view1.getWindowToken();
        inputManager.hideSoftInputFromWindow(binder,
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void SettextSytle(){

        myCustomFontt1 = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Klavika-Regular.ttf");
        myCustomFontt2 = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Nunito-Bold.ttf");
        myCustomFontt3 = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Nunito-Regular.ttf");
        myCustomFontt4 = Typeface.createFromAsset(getActivity().getAssets(),"fonts/nunito-semibold.ttf");
    }

    @Override
    public void onResume() {
        super.onResume();

    }


    public int toDPI(int value)
    {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,value,getContext().getResources().getDisplayMetrics());
    }

    void AnimateFeelings()
    {
        Display d = getActivity().getWindowManager().getDefaultDisplay();
        Point p = new Point();
        d.getSize(p);

        for(int i = 0 ; i < mFeelings.size() ; i++)
        {
            View temp = mFeelings.get(i);
            PropertyValuesHolder posX = PropertyValuesHolder.ofFloat("translationX",temp.getTranslationX());
            PropertyValuesHolder posY = PropertyValuesHolder.ofFloat("translationY",temp.getTranslationY());
            PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX",temp.getScaleX());
            PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY",temp.getScaleY());
            ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(temp,posX,posY,scaleX,scaleY);
            Interpolator interpolator = new DecelerateInterpolator(1.0f);
            animator.setInterpolator(interpolator);
            animator.setDuration(350 + (i * 300));
            temp.setTranslationX(p.x/2);
            float y = p.y - 200;
            temp.setTranslationY(y);
            temp.setScaleX(.0f);
            temp.setScaleY(.0f);
            animator.start();
        }
    }

    void FillView()
    {
        Map<String,String> request = new HashMap<String,String>();
        request.put("page_no", "0");
        request.put("user_id",UserDataCache.GetInstance().id);
        requestHandler.MakeRequest(request,"get_friend_posts");
    }

    void OnSelection(String feelingName)
    {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        Bundle data = new Bundle();
        data.putString("feelingName",feelingName);

        selectedFeelingTrendPage.setArguments(data);
        fragmentManager.beginTransaction().add(R.id.Main_Layout, selectedFeelingTrendPage).addToBackStack(null).commit();
    }

    @Override
    public void OnSuccess(JSONObject response, String apiName) {
        try {
            dataList.clear();
            JSONArray posts = response.getJSONArray("data");

            for (int i = 0; i < 4; i++) {
                JSONObject postData = posts.getJSONObject(i);
                PostViewData newPostData = new PostViewData();
                newPostData.SetData(postData);
                dataList.add(newPostData);
            }

            postViewAdapter.notifyDataSetChanged();

        } catch (JSONException e) { }
    }

    @Override
    public void OnFailure(String errorMessage, String apiName) {

    }

    @Override
    public void OnNetworkError() {

    }
}
