package com.jassimalmunaikh.feelerz;


import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class CreateCustomFeelingPost extends Fragment implements  ServerCallObserver, TopLevelFrag{

    RequestHandler requestHandler;
    CustomLoader loader;
    String parentFeeling;
    String colorCode;
    String emojie;

    public CreateCustomFeelingPost() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_custom_feeling_post, container, false);

        GlobalFragmentStack.getInstance().Register(this);

        parentFeeling = getArguments().getString("parentFeeling","");
        colorCode = getArguments().getString("colorCode","");
        emojie = getArguments().getString("emojie","");

        this.requestHandler = new RequestHandler(getContext(),this);
        this.loader = new CustomLoader(getActivity(),(ViewGroup)view);
        Button close = view.findViewById(R.id.close_button);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnManualClose();
            }
        });

        final EditText editText = view.findViewById(R.id.edit_text_custom_feeling);

        Button submit = view.findViewById(R.id.Submit_New_Feeling);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = editText.getText().toString();
                if(!data.isEmpty())
                {
                    loader.show();
                    Map<String,String> request = new HashMap<String,String>();
                    request.put("user_id",UserDataCache.GetInstance().id);
                    request.put("name", StringEscapeUtils.escapeJava(data));
                    request.put("color_code",colorCode);
                    request.put("emojie",emojie);
                    request.put("parrent_cat_name",parentFeeling);
                    requestHandler.MakeRequest(request,"add_feelings");
                }
                else
                {
                    Toast.makeText(getActivity().getApplicationContext(),"Please enter feeling...",Toast.LENGTH_SHORT).show();
                }
            }
        });


        return view;
    }

    @Override
    public void OnSuccess(JSONObject response, String apiName) {
        if(apiName.equals("add_feelings")) {
            try {
                JSONObject data = response.getJSONObject("data");

                   /* this.id = feelingData.getString("id");
                    this.parentFeeling = feelingData.getString("parrent");
                    this.name = feelingData.getString("name");
                    this.size = feelingData.getInt("size");
                    this.colorCode = feelingData.getString("color_code");
                    this.emojie = feelingData.getString("emojie");*/
                //   ArrayList<String> FeelingName = new ArrayList<String>();


                ArrayList<String> FeelingString = GlobalCache.GetInstance().getFeelingString();
                FeelingString.add(Html.fromHtml(StringEscapeUtils.unescapeJava(data.getString("name"))).toString());
                GlobalCache.GetInstance().FeelingString = FeelingString;


                Bundle bundle = new Bundle();
                bundle.putInt("feelingColor", Color.parseColor(data.getString("color_code")));
                bundle.putString("feelingName", data.getString("name"));
                bundle.putString("feelingParent", data.getString("parrent"));
                bundle.putString("feelingId", data.getString("id"));
                CreatePostFragment fragment = new CreatePostFragment();
                fragment.setArguments(bundle);
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction t = fragmentManager.beginTransaction();
                t.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up);
                // fragment.setEnterTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
                t.add(R.id.Main_Layout, fragment).commit();
                OnManualClose();
            } catch (Exception e) {

            }
        }
    }

    @Override
    public void OnFailure(String errorMessage, String apiName) {
        loader.SetErrorColor();
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
