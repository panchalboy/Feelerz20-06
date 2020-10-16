package com.jassimalmunaikh.feelerz;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
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
public class Change_PasswordFragment extends Fragment implements ServerCallObserver,TopLevelFrag{

    Typeface myCustomFontt1,myCustomFontt2,myCustomFontt3,myCustomFontt4;
    View view1;
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_OLD_PASSWORD = "old_password";
    private static final String KEY_NEW_PASSWORD = "new_password";
    private static final String KEY_EMPTY = "";
    private static final String KEY_MESSAGE = "message";

    String oldpassword,newpassword,confirmnewpassword;
    Button Back_btn;
    EditText oldPassword,newPassword,confirmNewPassword;
    TextView ChangePassword_Title,UpdatePassword_btn11;

    CustomLoader loader;
    RequestHandler requestHandler;


    public Change_PasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_change__password, container, false);
        this.view1 = view;
        SettextSytle();
        GlobalFragmentStack.getInstance().Register(this);
        SessionHandler.GetInstance().InitForCurrentContext(getContext());

        this.requestHandler = new RequestHandler(getContext(),this);
        this.loader = new CustomLoader(getActivity(),(ViewGroup)view);

        Back_btn = (Button)view.findViewById(R.id.Back_Btn);
        Back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnManualClose();
            }
        });

        ChangePassword_Title = view.findViewById(R.id.ChangePassword_Title);
        oldPassword = view.findViewById(R.id.old_password);
        newPassword = view.findViewById(R.id.new_password);
        confirmNewPassword = view.findViewById(R.id.confirm_new_password);
        UpdatePassword_btn11 = view.findViewById(R.id.UpdatePassword_btn11);

        ChangePassword_Title.setTypeface(myCustomFontt4);
        oldPassword.setTypeface(myCustomFontt3);
        newPassword.setTypeface(myCustomFontt3);
        confirmNewPassword.setTypeface(myCustomFontt4);

        UpdatePassword_btn11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                oldpassword = oldPassword.getText().toString().trim();
                newpassword = newPassword.getText().toString().trim();
                confirmnewpassword = confirmNewPassword.getText().toString().trim();
                removePhoneKeypad();

                if (validateInputs()) {
                    setPassword();
                }
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

    public void removePhoneKeypad() {
        InputMethodManager inputManager = (InputMethodManager) view1
                .getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        IBinder binder = view1.getWindowToken();
        inputManager.hideSoftInputFromWindow(binder,
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void setPassword(){

        this.loader.show();

        Map<String,String> request = new HashMap<>();

        request.put(KEY_USER_ID,UserDataCache.GetInstance().id);
        request.put(KEY_OLD_PASSWORD, oldpassword);
        request.put(KEY_NEW_PASSWORD, confirmnewpassword);

        this.requestHandler.MakeRequest(request,"change_password");

    }

    private boolean validateInputs() {

        if(KEY_EMPTY.equals(oldpassword)){
            makeText(getActivity(), "Please enter old password", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(KEY_EMPTY.equals(newpassword)){
            makeText(getActivity(), "Please enter new password", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(KEY_EMPTY.equals(confirmnewpassword)){
            makeText(getActivity(), "Please enter confirm password", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!newpassword.equals(confirmnewpassword)) {
            makeText(getActivity(), "New password and confirm password should be same", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
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

        if(apiName =="change_password")
        {
            try {

                makeText(getContext(), response.getString(KEY_MESSAGE), Toast.LENGTH_SHORT).show();

                OnManualClose();

            }
            catch (JSONException e){}


        }
        this.loader.hide();
    }

    @Override
    public void OnFailure(String errorMessage, String apiName) {
//        this.loader.SetErrorColor();
        this.loader.hide();
        makeText(getActivity(), "Old password is not match.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void OnNetworkError() {
        noInternetPopUp();
        loader.hide();
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