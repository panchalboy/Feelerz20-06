package com.jassimalmunaikh.feelerz;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

class BubbleSelectActionType
{
    public final static int POST_WITH_FEELING = 0;
    public final static int SHOW_ALL_POSTS = 1;
}

public class FeelingCircle extends View {

    public String parent;
    public String feelingId;
    public String feelingName;
    public float radius;
    public int startColor;
    public int endColor;
    public float scaleFactor;

    Context mContext;
    FragmentManager manager;
    Fragment currentFragment;

    Shader gradient;
    Paint brush;
    Paint textBrush;

    int textSize;

    public void SetParentFragment(Fragment parent){this.currentFragment = parent;}

    public void setParent(String parent)
    {
        this.parent = parent;
    }

    public void setFeelingId(String id)
    {
        this.feelingId = id;
    }

    public FeelingCircle(Context context, AttributeSet attrs) {
        super(context,attrs);

        this.manager = null;

        this.textSize = 70;
        this.mContext = context;
        this.brush = new Paint();
        this.textBrush = new Paint();
        this.feelingName = "";

        //get the attributes specified in attrs.xml using the name we included
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.FeelingCircle, 0, 0);

        try {
            //get the text and colors specified using the names in attrs.xml
            this.radius = a.getFloat(R.styleable.FeelingCircle_circleRadius, 0);
            this.startColor = a.getColor(R.styleable.FeelingCircle_startColor,0);
            this.endColor = a.getColor(R.styleable.FeelingCircle_endColor,0);
            this.scaleFactor = 30.0f;
        } finally {
            a.recycle();
        }

        float actualRadius = this.radius * this.scaleFactor;
        gradient = new LinearGradient(0,0,0,actualRadius * 2.0f,startColor,endColor,Shader.TileMode.MIRROR);

        setClickable(false);
    }

    public int toDPI(int value)
    {
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,value,mContext.getResources().getDisplayMetrics());
    }

    public void SetFragmentManager(FragmentManager manager)
    {
        this.manager = manager;
    }


    private void postWithFeeling()
    {
        setClickable(true);


        this.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SelectedFeelingTrendPage frag = (SelectedFeelingTrendPage)currentFragment;
                if(frag != null)
                    frag.Close();

                Bundle bundle = new Bundle();
                bundle.putInt("feelingColor",startColor);
                bundle.putString("feelingName",feelingName);
                bundle.putString("feelingParent",parent);
                bundle.putString("feelingId",feelingId);
                CreatePostFragment fragment = new CreatePostFragment();
                fragment.setArguments(bundle);
                FragmentManager fragmentManager = manager;
                FragmentTransaction t = fragmentManager.beginTransaction();
                t.setCustomAnimations(R.anim.slide_in_up,R.anim.slide_out_up);
                // fragment.setEnterTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
                t.add(R.id.Main_Layout, fragment).commit();
            }
        });
    }

    private void showAllPosts()
    {
        setClickable(true);

        this.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("feelingName",feelingName);
                FeelingsPosts fragment = new FeelingsPosts();
                fragment.setArguments(bundle);
                FragmentManager fragmentManager = manager;
               // fragment.setEnterTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
                fragmentManager.beginTransaction().add(R.id.Main_Layout, fragment).commit();
            }
        });
    }

    public void setScaleFactor(float sf)
    {
        this.scaleFactor = sf;
    }

    public void setRadius(float radius)
    {
        this.radius = radius;
        float actualRadius = this.radius * this.scaleFactor;
        gradient = new LinearGradient(0,0,0,actualRadius * 2.0f,startColor,endColor,Shader.TileMode.MIRROR);
    }

    public void setGradientColors(int startColor,int endColor)
    {
        this.startColor = startColor;
        this.endColor = endColor;
        float actualRadius = (this.radius * this.scaleFactor);
        gradient = new LinearGradient(0,0,0,actualRadius * 2,startColor,endColor,Shader.TileMode.MIRROR);
    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension((int)(this.radius * this.scaleFactor) * 2,(int)(this.radius*this.scaleFactor) * 2);
    }


    private void calculateTextSize()
    {
        int diameter = (int)(this.radius * this.scaleFactor * 2);
        int len = this.feelingName.length() * this.textSize;

        if(len >= diameter)
        {
            float scale = diameter/(float)len;
            this.textSize = (int)(this.textSize * scale);
        }
    }

    public void setName(String feelingName,int SelectActionType)
    {
        this.feelingName = feelingName;
        calculateTextSize();
        if(manager != null) {
            if(SelectActionType == BubbleSelectActionType.SHOW_ALL_POSTS)
                showAllPosts();
            else if(SelectActionType == BubbleSelectActionType.POST_WITH_FEELING)
                postWithFeeling();
        }
    }
    @Override
    protected void onDraw(Canvas canvas) {
     //   super.onDraw(canvas);

        float actualRadius = this.radius * this.scaleFactor;
        gradient = new LinearGradient(0,0,0,actualRadius * 2.0f,startColor,endColor,Shader.TileMode.MIRROR);
        this.brush.setShader(gradient);

        float x = getTranslationX() + (this.radius * this.scaleFactor );
        float y = getTranslationY() + (this.radius * this.scaleFactor );
        canvas.drawCircle(x,y,this.radius * this.scaleFactor,this.brush);

        this.textBrush.setTextAlign(Paint.Align.CENTER);
        this.textBrush.setColor(Color.WHITE);
        this.textBrush.setTextSize(this.textSize);


        canvas.drawText(this.feelingName, x, y, this.textBrush);
    }

    public int getDiameter() {
        return (int)(this.radius * this.scaleFactor * 2);
    }
}
