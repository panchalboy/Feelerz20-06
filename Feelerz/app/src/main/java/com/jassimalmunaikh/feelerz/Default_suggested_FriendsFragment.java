package com.jassimalmunaikh.feelerz;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 */
public class Default_suggested_FriendsFragment extends Fragment {


    public Default_suggested_FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_default_suggested__friends, container, false);

        Button Skip_btn = (Button)view.findViewById(R.id.Skip_Btn);
        Skip_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.Main_Layout, new HomeFragment()).addToBackStack(null).commit();
            }
        });

        return view;
    }

}
