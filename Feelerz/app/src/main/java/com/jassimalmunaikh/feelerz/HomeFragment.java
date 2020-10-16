package com.jassimalmunaikh.feelerz;


import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import Adapter.Section_Pager_Adapter;


import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements ServerCallObserver {

    View myfragment;

    RequestHandler requestHandler;

    ViewPager viewPager;
    TabLayout tabLayout;
    Button Feel_btn;

    TextView searchButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myfragment = inflater.inflate(R.layout.fragment_home, container, false);
        removePhoneKeypad();
        this.requestHandler = new RequestHandler(getContext(),this);

        viewPager = myfragment.findViewById(R.id.viewpager);
        tabLayout = myfragment.findViewById(R.id.tablayout);
        Feel_btn = myfragment.findViewById(R.id.Feel_layout_btn);
        searchButton = myfragment.findViewById(R.id.Search_textview);


        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Nunito-Regular.ttf");
        searchButton.setTypeface(font);

//        Typeface fon2 = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Nunito-Regular.ttf");
//        tabLayout.setTypeface(font);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removePhoneKeypad();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().add(R.id.Main_Layout, new SearchForUser()).addToBackStack(null).commit();
            }
        });

        Feel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().add(R.id.Main_Layout, new TrendingFeelingsPage()).addToBackStack(null).commit();
            }
        });
        removePhoneKeypad();

        return myfragment;
    }

    public void removePhoneKeypad() {
        InputMethodManager inputManager = (InputMethodManager) myfragment
                .getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        IBinder binder = myfragment.getWindowToken();
        inputManager.hideSoftInputFromWindow(binder,
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setUpviewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#FFFFFF"));
        tabLayout.setSelectedTabIndicatorHeight((int) (2 * getResources().getDisplayMetrics().density));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    });
}

    private void setUpviewPager(ViewPager viewPager) {

//        changeTabsFont();

        Section_Pager_Adapter adapter = new Section_Pager_Adapter(getChildFragmentManager());

        adapter.addfragment(new Public_SectionFragment(),"Public");
        adapter.addfragment(new Friend_SectionFragment(),"Friend");

        viewPager.setAdapter(adapter);
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

}
