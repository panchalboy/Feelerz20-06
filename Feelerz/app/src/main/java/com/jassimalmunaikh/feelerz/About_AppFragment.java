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
public class About_AppFragment extends Fragment implements ServerCallObserver,TopLevelFrag {

    CustomLoader loader;
    RequestHandler requestHandler;

    Typeface myCustomFontt1,myCustomFontt2,myCustomFontt3,myCustomFontt4;
    TextView AboutApp_Title,aboutApp_content;
    String id,content1,user_id,date_time;

    public About_AppFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about__app, container, false);

        SettextSytle();
        GlobalFragmentStack.getInstance().Register(this);
        this.requestHandler = new RequestHandler(getContext(),this);
        this.loader = new CustomLoader(getActivity(),(ViewGroup)view);

        AboutApp_Title = view.findViewById(R.id.AboutApp_Title);
        AboutApp_Title.setTypeface(myCustomFontt4);

        getData();

        aboutApp_content = view.findViewById(R.id.aboutApp_content);
        aboutApp_content.setTypeface(myCustomFontt3);

        Button Back_btn = (Button)view.findViewById(R.id.Back_Btn);
        Back_btn.setOnClickListener(new View.OnClickListener() {
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

    private void getData(){

        this.loader.show();

        Map<String,String> request = new HashMap<String,String>();
        String UserID = UserDataCache.GetInstance().id;
        request.put("user_id",UserID);
        this.requestHandler.MakeRequest(request,"get_about_us");

    }

    @Override
    public void OnSuccess(JSONObject response, String apiName) {
        if(apiName =="get_about_us") {
            try {
                content1 = response.getJSONObject("data").getString("about_us_content").toString();
                aboutApp_content.setText(content1);
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