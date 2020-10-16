package com.jassimalmunaikh.feelerz;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class OTP_Forget_Activity extends AppCompatActivity implements ServerCallObserver {

    CustomLoader loader;
    RequestHandler requestHandler;

    private static final String KEY_STATUS = "status";
    private static final String KEY_MESSAGE = "message";

    private static final String KEY_OTP = "otp";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_FORGET_EMAIL = "email";

    private String otp, Fid, FEmail;

    Button Submit_OTP_Btn, Resend_OTP_Btn, Back_Btn;
    TextView Page_Title, TXT_OTP;
    EditText OTP_EDT;

    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarGadiant(this);
        setContentView(R.layout.activity_otp__forget_);

        this.requestHandler = new RequestHandler(getApplicationContext(),this);
        this.loader = new CustomLoader(this,(ViewGroup)getWindow().getDecorView().getRootView());

        Submit_OTP_Btn = findViewById(R.id.Submit_otp);
        Resend_OTP_Btn = findViewById(R.id.Resend_otp);
        Back_Btn = findViewById(R.id.Back_Btn);

        Page_Title = findViewById(R.id.forgetPasswordTitle);
        TXT_OTP = findViewById(R.id.txt_OTP);
        OTP_EDT = findViewById(R.id.et_otp);
        Page_Title.setText("OTP");
///////////////////////////////////////////////////////////////////////////////////////////////////////
        Typeface myCustomFontt = Typeface.createFromAsset(getApplication().getAssets(), "fonts/Nunito-Bold.ttf");
        Page_Title.setTypeface(myCustomFontt);

        Typeface myCustomFontt2 = Typeface.createFromAsset(getAssets(), "fonts/Nunito-Regular.ttf");
        TXT_OTP.setTypeface(myCustomFontt2);

        Typeface myCustomFontt3 = Typeface.createFromAsset(getAssets(), "fonts/Nunito-Regular.ttf");
        OTP_EDT.setTypeface(myCustomFontt3);

        Typeface myCustomFontt4 = Typeface.createFromAsset(getAssets(), "fonts/Nunito-Regular.ttf");
        Submit_OTP_Btn.setTypeface(myCustomFontt4);

        Typeface myCustomFontt5 = Typeface.createFromAsset(getAssets(), "fonts/Nunito-Regular.ttf");
        Resend_OTP_Btn.setTypeface(myCustomFontt5);
///////////////////////////////////////////////////////////////////////////////////////////////////////////

        Back_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OTP_Forget_Activity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        Submit_OTP_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otp = OTP_EDT.getText().toString().trim();
                send_otp();
            }
        });

        Resend_OTP_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resend_otp();
            }
        });
    }

    private void setStatusBarGadiant(OTP_Forget_Activity loginActivity) {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            Window window =loginActivity.getWindow();
            Drawable background = loginActivity.getResources().getDrawable(R.drawable.background_color);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(loginActivity.getResources().getColor(android.R.color.transparent));
            window.setBackgroundDrawable(background);
        }
    }

    private void OTP_Send(){
        final Dialog dialog = new Dialog(context);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.otp_send_popup);
        dialog.setTitle("OTP Send");

        TextView Message = (TextView)dialog.findViewById(R.id.message_txt);
        Message.setText(KEY_MESSAGE);

        TextView OK = (TextView) dialog.findViewById(R.id.OK_BTN);
        OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void send_otp() {

        this.loader.show();
        Map<String,String> request = new HashMap<String,String>();

        Fid = UserDataCache.GetInstance().id;
        request.put(KEY_USER_ID, Fid);
        request.put(KEY_OTP, otp);

        this.loader.show();
        this.requestHandler.MakeRequest(request,"check_otp_for_password");

    }

    private void resend_otp() {

        this.loader.show();
        Map<String,String> request = new HashMap<String,String>();

        FEmail = UserDataCache.GetInstance().email;
        request.put(KEY_FORGET_EMAIL, FEmail);

        this.loader.show();
        this.requestHandler.MakeRequest(request,"forgate");

    }

    private void noInternetPopUp(){
        final Dialog dialog = new Dialog(this);
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

        if (apiName == "check_otp_for_password"){

            Intent intent = new Intent(OTP_Forget_Activity.this, Reset_Password_ForgetActivity.class);
            startActivity(intent);
            try {
                Toast.makeText(getApplicationContext(), response.getString(KEY_MESSAGE), Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else if (apiName == "forgate"){
            OTP_Send();
            try {
                Toast.makeText(getApplicationContext(), response.getString(KEY_MESSAGE), Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    this.loader.hide();
    }

    @Override
    public void OnFailure(String errorMessage, String apiName) {
        this.loader.hide();
        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void OnNetworkError() {
        noInternetPopUp();
        loader.hide();
    }
}