package com.jassimalmunaikh.feelerz;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllUsersFromSearch extends Fragment implements  ServerCallObserver{

    Typeface myCustomFontt1,myCustomFontt2,myCustomFontt3,myCustomFontt4;
    RequestHandler requestHandler;
    ListView usersList;
    TextView topHeaderText;
    UsersViewAdapter customAdapter = null;
    ArrayList<UserViewData> data = null;

    public AllUsersFromSearch() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_all_users_from_search, container, false);

        SettextSytle();

        ImageButton Back_Button = view.findViewById(R.id.Back_Btn);

        Back_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().remove(AllUsersFromSearch.this).commit();
            }
        });

        this.requestHandler = new RequestHandler(getActivity().getApplicationContext(),this);

        usersList = (ListView)view.findViewById(R.id.all_querty_result);
        topHeaderText = (TextView)view.findViewById(R.id.title_search_query);
        topHeaderText.setTypeface(myCustomFontt4);

        data = new ArrayList<UserViewData>();
        customAdapter = new UsersViewAdapter(getContext(),data,getFragmentManager());

        usersList.setAdapter(customAdapter);


        String query = getArguments().getString("query");
        topHeaderText.setText(query);
        populateList(query);

        return view;
    }

    public void SettextSytle(){

        myCustomFontt1 = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Klavika-Regular.ttf");
        myCustomFontt2 = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Nunito-Bold.ttf");
        myCustomFontt3 = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Nunito-Regular.ttf");
        myCustomFontt4 = Typeface.createFromAsset(getActivity().getAssets(),"fonts/nunito-semibold.ttf");
    }

    public void populateList(String query)
    {
        Map<String,String> request = new HashMap<String,String>();
        request.put("user_id", UserDataCache.GetInstance().id);
        request.put("search_text", query);
        request.put("more","u");
        requestHandler.MakeRequest(request,"search_FriendA");

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

        try {
            data.clear();
            JSONArray users = response.getJSONArray("users");

            for (int i = 0; i < users.length(); i++) {
                JSONObject user = users.getJSONObject(i);
                String name = user.getString("name");
                String imageId = user.getString("profileimage");
                String userId = user.getString("user_id");
                data.add(new UserViewData(name, imageId,userId));

            }

            customAdapter.notifyDataSetChanged();
        }
        catch (JSONException e){}
    }

    @Override
    public void OnFailure(String errorMessage, String apiName) {

    }

    @Override
    public void OnNetworkError() {
        noInternetPopUp();
    }

}
