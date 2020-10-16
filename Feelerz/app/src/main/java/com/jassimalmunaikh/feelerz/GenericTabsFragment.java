package com.jassimalmunaikh.feelerz;

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import Adapter.Section_Pager_Adapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class GenericTabsFragment extends Fragment implements TopLevelFrag{

    Section_Pager_Adapter mAdapter;
    ViewPager viewPager;

    public GenericTabsFragment() {
        // Required empty public constructor
    }


    @Override
    public void
    onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mAdapter = new Section_Pager_Adapter(getFragmentManager());
        viewPager = view.findViewById(R.id.pager);

        String tabOneTitle = getArguments().getString("tabOneTitle");
        String tabTwoTitle = getArguments().getString("tabTwoTitle");
        String postId = getArguments().getString("postId","");
        String targetId = getArguments().getString("targetId","");

        Bundle data1 = new Bundle();
        data1.putString("state",tabOneTitle);
        data1.putString("targetId",targetId);
        data1.putString("postId",postId);
        TabFragment tab1 = new TabFragment();
        tab1.setArguments(data1);

        Bundle data2 = new Bundle();
        data2.putString("postId",postId);
        data2.putString("targetId",targetId);
        data2.putString("state",tabTwoTitle);
        TabFragment tab2 = new TabFragment();
        tab2.setArguments(data2);

        mAdapter.addfragment(tab1,tabOneTitle);
        mAdapter.addfragment(tab2,tabTwoTitle);

        viewPager.setAdapter(mAdapter);

        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        tabLayout.setTabTextColors(Color.parseColor("#9E9E9E"), Color.parseColor("#FFFFFF"));
        tabLayout.setupWithViewPager(viewPager);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_generic_tabs, container, false);

        GlobalFragmentStack.getInstance().Register(this);

        Button back_btn = view.findViewById(R.id.Back_Btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnManualClose();
            }
        });

        return view;
    }

    @Override
    public void OnManualClose() {
        GlobalFragmentStack.getInstance().Unregister(this);
        Close();
    }

    @Override
    public void Close() {
        mAdapter.clear();
        getFragmentManager().beginTransaction().remove(GenericTabsFragment.this).commit();
    }
}


// Instances of this class are fragments representing a single
// object in our collection.
