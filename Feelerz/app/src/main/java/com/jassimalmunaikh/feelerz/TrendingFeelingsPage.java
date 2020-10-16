package com.jassimalmunaikh.feelerz;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.TimeInterpolator;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class TrendingFeelingsPage extends Fragment implements ServerCallObserver, TopLevelFrag{

    Typeface myCustomFontt1,myCustomFontt2,myCustomFontt3,myCustomFontt4;
    TextView TrendFeel_Title;

    RequestHandler requestHandler;
    ArrayList<FeelingData> mFeelings;
    ViewGroup parent;
    View heading;

    public TrendingFeelingsPage() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.requestHandler = new RequestHandler(getContext(),this);
        Map<String,String> request = new HashMap<String,String>();
        request.put("user_id",UserDataCache.GetInstance().id);
        requestHandler.MakeRequest(request,"main_five_feeling");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_trending_feelings_page, container, false);

        SettextSytle();
        TrendFeel_Title = view.findViewById(R.id.TrendFeel_Title);
        TrendFeel_Title.setTypeface(myCustomFontt4);

        GlobalFragmentStack.getInstance().Register(this);

        this.heading = view.findViewById(R.id.heading);
        this.heading.setVisibility(View.INVISIBLE);
        this.mFeelings = new ArrayList<FeelingData>();
        this.parent = (ViewGroup) view.findViewById(R.id.feelings_scroller);


        Button Back_Btn = view.findViewById(R.id.Back_Btn);
        Back_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               OnManualClose();
            }
        });

        return view;
    }

    public void SettextSytle(){

        myCustomFontt1 = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Klavika-Regular.ttf");
        myCustomFontt2 = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Nunito-Bold.ttf");
        myCustomFontt3 = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Nunito-Regular.ttf");
        myCustomFontt4 = Typeface.createFromAsset(getActivity().getAssets(),"fonts/nunito-semibold.ttf");
    }

    @Override
    public void OnSuccess(JSONObject response, String apiName) {

                try {
                    JSONArray others = response.getJSONObject("data").getJSONArray("Other");
                    for(int i = 0; i < others.length() ; i++)
                    {
                        JSONObject feeling = others.getJSONObject(i);
                        FeelingData data = new FeelingData();
                        data.ParseData(feeling);
                        mFeelings.add(data);
                    }

                }
                catch(JSONException e){}

                Arrange();

    }


    public void Arrange()
    {
        Display d = getActivity().getWindowManager().getDefaultDisplay();
        Point p = new Point();
        d.getSize(p);

        int len = mFeelings.size();
        int lastXMargin = 40;
        int currX = 40;
        int currY = 40;
        int lastLargeDiameter = 0;

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);


        for(int i = 0; i < len ; i++)
        {
            View layout = inflater.inflate(R.layout.feeling_circle,parent,false);
            SetupDataForView(layout,i); //will update the circle size
            FeelingCircle circle = layout.findViewById(R.id.circle_view);

            int diameter = circle.getDiameter();

            if(currX + diameter > p.x)
            {
                currX = lastXMargin = (int)(Math.random() * 80) + 30;
                currY += lastLargeDiameter + 40;
                lastLargeDiameter = 0;
            }

            if(diameter > lastLargeDiameter)
            {
                lastLargeDiameter = diameter;
            }

            FrameLayout.LayoutParams param = (FrameLayout.LayoutParams) layout.getLayoutParams();
            param.topMargin = (int)currY;
            param.leftMargin = (int)currX;
            layout.setLayoutParams(param);

            SetColorForCircle(circle,mFeelings.get(i).parentFeeling);
            AnimateBubble(layout);

            currX += diameter + 50;


            parent.addView(layout);
        }

        this.heading.setVisibility(View.VISIBLE);

    }

    public void AnimateBubble(View view)
    {
        float diff = (float)( Math.random() * 10) + 200;
        int random = (int)Math.floor(Math.random() * 10) + 1;
        String axisName = "translationX";
        float originTranslation = view.getTranslationX();
        if(Math.random() < .5f)
            view.setTranslationX(originTranslation + diff);
        else
            view.setTranslationX(originTranslation - diff);
        view.setAlpha(.1f);

        if(Math.random() < .5f)
        {
            view.setTranslationX(originTranslation);
            originTranslation = view.getTranslationY();

            if(Math.random() < .5f)
                view.setTranslationY(originTranslation + diff);
            else
                view.setTranslationY(originTranslation - diff);
            axisName = "translationY";
        }


        PropertyValuesHolder pos = PropertyValuesHolder.ofFloat(axisName,originTranslation);
        PropertyValuesHolder opacity = PropertyValuesHolder.ofFloat("alpha",1.0f);
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(view,pos,opacity);
        TimeInterpolator interpolator = new DecelerateInterpolator(1.0f);
        animator.setInterpolator(interpolator);
        animator.setDuration(1000);
        animator.start();
    }

    void SetupDataForView(View view,int position)
    {
        FeelingData data = mFeelings.get(position);

        FeelingCircle c = view.findViewById(R.id.circle_view);

        String feelingName = Html.fromHtml(StringEscapeUtils.unescapeJava(data.name)).toString();

        //make sure to call setName after setFragmentManager
        c.SetFragmentManager(getFragmentManager());
        c.setName(feelingName,BubbleSelectActionType.SHOW_ALL_POSTS);
        c.setRadius(data.size);

    }

    public void SetColorForCircle(FeelingCircle c,String parent)
    {
        int endColor,startColor;

        if(parent.equals("Happy"))
        {
            startColor = FeelingsColor.happyStart.toArgb();
            endColor = FeelingsColor.happyEnd.toArgb();
        }
        else if(parent.equals("Sad"))
        {
            startColor = FeelingsColor.sadStart.toArgb();
            endColor = FeelingsColor.sadEnd.toArgb();
        }
        else if(parent.equals("Excited"))
        {
            startColor = FeelingsColor.excitedStart.toArgb();
            endColor = FeelingsColor.excitedEnd.toArgb();
        }
        else if(parent.equals("Bored"))
        {
            startColor = FeelingsColor.boredStart.toArgb();
            endColor = FeelingsColor.boredEnd.toArgb();
        }
        else
        {
            startColor = FeelingsColor.madStart.toArgb();
            endColor = FeelingsColor.madEnd.toArgb();
        }

        c.setGradientColors(startColor,endColor);
    }
    @Override
    public void OnFailure(String errorMessage, String apiName) {
        Log.e(apiName,errorMessage);
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
}
