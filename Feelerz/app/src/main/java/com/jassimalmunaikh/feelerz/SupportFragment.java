package com.jassimalmunaikh.feelerz;


import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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
public class SupportFragment extends Fragment implements ServerCallObserver,TopLevelFrag {

    Typeface myCustomFontt1,myCustomFontt2,myCustomFontt3,myCustomFontt4;
    TextView Support_Title,supportSubmit_btn;

    private static final String KEY_SUPPORT_MESSAGE = "support_msg";
    private static final String KEY_SUPPORT_DESCRIPTION = "description";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_EMPTY = "";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_STATUS = "status";

    View view1;
    Button Back_btn;
    EditText Message,Description;
    String message,description;

    CustomLoader loader;
    RequestHandler requestHandler;

    public SupportFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_support, container, false);
        this.view1 = view;
        GlobalFragmentStack.getInstance().Register(this);

        SettextSytle();

        Back_btn = (Button)view.findViewById(R.id.Back_Btn);

        Support_Title = view.findViewById(R.id.Support_Title);
        Message = view.findViewById(R.id.Message);
        Description = view.findViewById(R.id.Description);
        supportSubmit_btn = view.findViewById(R.id.supportSubmit_btn);

        Support_Title.setTypeface(myCustomFontt4);
        Message.setTypeface(myCustomFontt3);
        Description.setTypeface(myCustomFontt3);
        supportSubmit_btn.setTypeface(myCustomFontt4);

        this.requestHandler = new RequestHandler(getContext(),this);
        this.loader = new CustomLoader(getActivity(),(ViewGroup)view);

        Back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnManualClose();
                removePhoneKeypad();
            }
        });

        supportSubmit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                message = Message.getText().toString().trim();
                description = Description.getText().toString().trim();
                removePhoneKeypad();

                if (validateInputs()) {
                    Send_Support();
                }
            }
        });

        return view;
    }

    public void SettextSytle() {

        myCustomFontt1 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Klavika-Regular.ttf");
        myCustomFontt2 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Nunito-Bold.ttf");
        myCustomFontt3 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Nunito-Regular.ttf");
        myCustomFontt4 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/nunito-semibold.ttf");

    }

        private void Send_Support() {

        this.loader.show();

        Map<String,String> request = new HashMap<>();

        request.put(KEY_USER_ID,UserDataCache.GetInstance().id);
        request.put(KEY_SUPPORT_MESSAGE, message);
        request.put(KEY_SUPPORT_DESCRIPTION, description);

        this.requestHandler.MakeRequest(request,"support");

    }

    private boolean validateInputs(){

        if(KEY_EMPTY.equals(message)){
            makeText(getActivity(), "Message Can't be Empty.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(KEY_EMPTY.equals(description)){
            makeText(getActivity(), "Description Can't be Empty.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public void removePhoneKeypad() {
        InputMethodManager inputManager = (InputMethodManager) view1
                .getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        IBinder binder = view1.getWindowToken();
        inputManager.hideSoftInputFromWindow(binder,
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public void OnSuccess(JSONObject response, String apiName) {
        if(apiName == "support")
        {
            try {

                makeText(getContext(), response.getString(KEY_MESSAGE), Toast.LENGTH_SHORT).show();

            }
            catch (JSONException e){}
        }
        this.loader.hide();

    }

    @Override
    public void OnFailure(String errorMessage, String apiName) {
        this.loader.SetErrorColor();
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