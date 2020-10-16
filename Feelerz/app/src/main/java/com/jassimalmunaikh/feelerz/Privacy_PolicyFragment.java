package com.jassimalmunaikh.feelerz;

import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.widget.Toast.makeText;

/**
 * A simple {@link Fragment} subclass.
 */
public class Privacy_PolicyFragment extends Fragment implements ServerCallObserver, TopLevelFrag{

    Typeface myCustomFontt1,myCustomFontt2,myCustomFontt3,myCustomFontt4;
    TextView PrivacyPolicy_Title,Content;

    private static final String KEY_MESSAGE = "message";
    private static final String KEY_DATA = "data";
    private static final String KEY_STATUS = "status";
    CustomLoader loader;
    RequestHandler requestHandler;

    String id,content1,user_id,date_time;

    public Privacy_PolicyFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_privacy__policy, container, false);

        SettextSytle();
        GlobalFragmentStack.getInstance().Register(this);
        this.requestHandler = new RequestHandler(getContext(),this);
        this.loader = new CustomLoader(getActivity(),(ViewGroup)view);

        PrivacyPolicy_Title = view.findViewById(R.id.PrivacyPolicy_Title);
        PrivacyPolicy_Title.setTypeface(myCustomFontt4);

        Button Back_btn = (Button)view.findViewById(R.id.Back_Btn);
        Back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnManualClose();
            }
        });

        getData();

        Content = (TextView) view.findViewById(R.id.privacy_policy_content);
        Content.setTypeface(myCustomFontt3);

        return view;
    }

    public void SettextSytle(){

        myCustomFontt1 = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Klavika-Regular.ttf");
        myCustomFontt2 = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Nunito-Bold.ttf");
        myCustomFontt3 = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Nunito-Regular.ttf");
        myCustomFontt4 = Typeface.createFromAsset(getActivity().getAssets(),"fonts/nunito-semibold.ttf");
    }

    private void getData(){

        this.loader.show();

        Map<String,String> request = new HashMap<String,String>();

        request.put("user_id",UserDataCache.GetInstance().id);

        this.requestHandler.MakeRequest(request,"get_privacy_policy");

    }

    @Override
    public void OnSuccess(JSONObject response, String apiName) {
        if(apiName =="get_privacy_policy") {
            try {
                content1 = response.getJSONArray("data").getJSONObject(0).getString("privacy_policy_content");
                Content.setText(content1);
            }
            catch (JSONException e){}
        }
        this.loader.hide();
    }

    @Override
    public void OnFailure(String errorMessage, String apiName) {
        makeText(getActivity(), "Server Error !!", Toast.LENGTH_SHORT).show();
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