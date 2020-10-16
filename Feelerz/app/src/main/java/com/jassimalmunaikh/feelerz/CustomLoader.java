package com.jassimalmunaikh.feelerz;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class CustomLoader {

    View progressBar;


    public CustomLoader(Activity activity, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater)activity.getSystemService(LAYOUT_INFLATER_SERVICE);
        progressBar = inflater.from(activity.getApplicationContext()).inflate(R.layout.popup_progress_bar,null);
        progressBar.setVisibility(View.INVISIBLE);
        parent.addView(progressBar);
    }


    public void show()
    {
        if(progressBar != null)
        {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    public void SetErrorColor()
    {
        if(progressBar != null)
        {
            Toast.makeText(progressBar.getContext(),"Error",Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    public void hide()
    {
        if(progressBar != null)
        {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

}
