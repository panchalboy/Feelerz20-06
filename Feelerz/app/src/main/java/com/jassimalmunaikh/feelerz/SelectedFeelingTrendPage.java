package com.jassimalmunaikh.feelerz;


import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.TimeInterpolator;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
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
public class SelectedFeelingTrendPage extends Fragment implements  ServerCallObserver{

    String feelingName;
    RequestHandler requestHandler;
    ArrayList<FeelingData> mFeelings;
    ViewGroup parent;

    View addCustomFeeling;

    public SelectedFeelingTrendPage() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_selected_feeling_trend_page, container, false);
        view.setClickable(true);

        View closeArea = view.findViewById(R.id.close_area);

        closeArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().remove(SelectedFeelingTrendPage.this).commit();
            }
        });

        view.bringToFront();

        this.mFeelings = new ArrayList<FeelingData>();
        this.parent = (ViewGroup) view.findViewById(R.id.select_feeling_options);

        LayoutInflater _inflater = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
        this.addCustomFeeling = _inflater.inflate(R.layout.add_custom_feeling_layout,parent,false);

        this.feelingName = getArguments().getString("feelingName");

        this.requestHandler = new RequestHandler(getContext(),this);
        Map<String,String> request = new HashMap<String,String>();
        request.put("user_id",UserDataCache.GetInstance().id);
        requestHandler.MakeRequest(request,"main_five_feeling");

        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    private void noInternetPopUp(){
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

        ArrayList<String> FeelingName = new ArrayList<String>();

        try {
            JSONArray others = response.getJSONObject("data").getJSONArray(feelingName);
            for(int i = 0; i < others.length() ; i++)
            {
                JSONObject feeling = others.getJSONObject(i);
                FeelingData data = new FeelingData();
                data.ParseData(feeling);
                mFeelings.add(data);
                FeelingName.add(Html.fromHtml(StringEscapeUtils.unescapeJava(data.name)).toString());
                GlobalCache.GetInstance().FeelingString = FeelingName;
            }

        }
        catch(JSONException e){}
        Arrange();
    }


    void setupCustomAdd(int x, int y)
    {
        addCustomFeeling.setTranslationY(y);
        addCustomFeeling.setTranslationX(x);

        FeelingCircle circle = addCustomFeeling.findViewById(R.id.circle);
        circle.setRadius(4);
        addCustomFeeling.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle data = new Bundle();
                        data.putString("parentFeeling",feelingName);
                        data.putString("colorCode",mFeelings.get(0).colorCode);
                        data.putString("emojie",mFeelings.get(0).emojie);
                        CreateCustomFeelingPost fragment = new CreateCustomFeelingPost();
                        fragment.setArguments(data);
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        transaction.add(R.id.Main_Layout,fragment).commit();
                        getFragmentManager().beginTransaction().remove(SelectedFeelingTrendPage.this).commit();
                    }
                }
        );

        SetColorForCircle(circle,feelingName);
        parent.addView(addCustomFeeling);
    }

    void AnimateCustomAdd()
    {
        String axisName = "translationY";
        addCustomFeeling.setAlpha(.1f);
        float diff = 200;
        float originTranslation = addCustomFeeling.getTranslationY();
        addCustomFeeling.setTranslationY(originTranslation - diff);

        PropertyValuesHolder pos = PropertyValuesHolder.ofFloat(axisName,originTranslation);
        PropertyValuesHolder opacity = PropertyValuesHolder.ofFloat("alpha",1.0f);
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(addCustomFeeling,pos,opacity);
        TimeInterpolator interpolator = new DecelerateInterpolator(1.0f);
        animator.setInterpolator(interpolator);
        animator.setDuration(1500);
        animator.start();
    }

    int getCustomAddDiameter()
    {
        FeelingCircle circle = addCustomFeeling.findViewById(R.id.circle);
        return circle.getDiameter();
    }

    public int toDPI(int value)
    {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,value,getContext().getResources().getDisplayMetrics());
    }

    public void Arrange()
    {
        Display d = getActivity().getWindowManager().getDefaultDisplay();
        Point p = new Point();
        d.getSize(p);

        int currY = 40;

        this.setupCustomAdd(40,currY);

        this.AnimateCustomAdd();

        int len = mFeelings.size();
        int currX = this.getCustomAddDiameter() + 80;

        int lastLargeDiameter = 0;

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);

        for(int i = 0; i < len  ; i++)
        {

            View layout = inflater.inflate(R.layout.feeling_circle,parent,false);
            SetupDataForView(layout,i); //will update the circle size
            FeelingCircle circle = layout.findViewById(R.id.circle_view);
            circle.SetParentFragment(this);

            int diameter = circle.getDiameter();

            if(currX + diameter > p.x)
            {
                currX = (int)(Math.random() * 80) + 30;
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

            currX += diameter + 40;


            parent.addView(layout);
        }

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
        animator.setDuration(1500);
        animator.start();
    }

    void SetupDataForView(View view,int position)
    {
        FeelingData data = mFeelings.get(position);

        FeelingCircle c = view.findViewById(R.id.circle_view);

        String feelingName = Html.fromHtml(StringEscapeUtils.unescapeJava(data.name)).toString();

        //make sure to call setName after setFragmentManager
        c.SetFragmentManager(getFragmentManager());
        c.setName(feelingName,BubbleSelectActionType.POST_WITH_FEELING);
        c.setRadius(data.size);
        c.setFeelingId(data.id);
        c.setParent(data.parentFeeling);



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
        noInternetPopUp();
    }


    public void Close() {
        getFragmentManager().beginTransaction().remove(SelectedFeelingTrendPage.this).commit();
    }
}
