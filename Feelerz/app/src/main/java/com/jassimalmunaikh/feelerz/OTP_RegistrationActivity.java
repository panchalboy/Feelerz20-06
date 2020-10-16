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

public class OTP_RegistrationActivity extends AppCompatActivity implements ServerCallObserver{

    RequestHandler requestHandler;
    private static final String KEY_STATUS = "status";
    private static final String KEY_MESSAGE = "message";

    private static final String KEY_EMAIL = "email";
    private static final String KEY_OTP = "otp";

    CustomLoader loader;

    Button Submit_OTP,Resend_OTP_Btn,Back_Btn;
    TextView Page_Title,TXT_OTP;
    EditText OTP_EDT;
    String otp;
    final Context context = this;

    int EntryType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarGadiant(this);
        setContentView(R.layout.activity_otp__registration);

        EntryType = GlobalCache.GetInstance().SuggestFriendEntryType = 0;

        this.requestHandler = new RequestHandler(getApplicationContext(),this);
        this.loader = new CustomLoader(this,(ViewGroup)getWindow().getDecorView().getRootView());
        Submit_OTP = findViewById(R.id.Submit_otpR);
        Resend_OTP_Btn = findViewById(R.id.Resend_otpR);
        Back_Btn = findViewById(R.id.Back_Btn);

        Page_Title = findViewById(R.id.OTP_Registration_Title);
        TXT_OTP = findViewById(R.id.txt_OTP);
        OTP_EDT = findViewById(R.id.enterOTP);


///////////////////////////////////////////////////////////////////////////////////////////////////////
        Typeface myCustomFontt1 = Typeface.createFromAsset(getAssets(), "fonts/Nunito-Bold.ttf");
        Page_Title.setTypeface(myCustomFontt1);

        Typeface myCustomFontt2 = Typeface.createFromAsset(getAssets(), "fonts/Nunito-Regular.ttf");
        TXT_OTP.setTypeface(myCustomFontt2);

        Typeface myCustomFontt3 = Typeface.createFromAsset(getAssets(), "fonts/Nunito-Regular.ttf");
        OTP_EDT.setTypeface(myCustomFontt3);

        Typeface myCustomFontt4 = Typeface.createFromAsset(getAssets(), "fonts/Nunito-Regular.ttf");
        Submit_OTP.setTypeface(myCustomFontt4);

        Typeface myCustomFontt5 = Typeface.createFromAsset(getAssets(), "fonts/Nunito-Regular.ttf");
        Resend_OTP_Btn.setTypeface(myCustomFontt5);
///////////////////////////////////////////////////////////////////////////////////////////////////////////

        Back_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OTP_RegistrationActivity.this,RegistrationActivity.class);
                startActivity(intent);
            }
        });

        Submit_OTP.setOnClickListener(new View.OnClickListener() {
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

    private void setStatusBarGadiant(OTP_RegistrationActivity registrationActivity) {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            Window window =registrationActivity.getWindow();
            Drawable background = registrationActivity.getResources().getDrawable(R.drawable.background_color);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(registrationActivity.getResources().getColor(android.R.color.transparent));
            window.setBackgroundDrawable(background);
        }
    }

    private void send_otp() {

        Map<String,String> request = new HashMap<String,String>();

        request.put(KEY_EMAIL, UserDataCache.GetInstance().email);
        request.put(KEY_OTP, otp);

        this.loader.show();
        this.requestHandler.MakeRequest(request,"check_otp");
    }

    private void resend_otp() {

        Map<String,String> request = new HashMap<String,String>();

        request.put(KEY_EMAIL, UserDataCache.GetInstance().email);

        this.loader.show();
        this.requestHandler.MakeRequest(request,"send_otp");
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

        if(apiName =="check_otp")
        {
            try {

                JSONObject data = response.getJSONObject("data");
                UserDataCache.GetInstance().SetLoginData(data);
                SessionHandler.GetInstance().onSuccessfullLogin(data);

                Toast.makeText(getApplicationContext(), response.getString(KEY_MESSAGE), Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(OTP_RegistrationActivity.this, DashboardActivity.class);
                    startActivity(intent);

            }
            catch (JSONException e){}

        }

        else if(apiName == "send_otp")
        {
            OTP_Send();
//            try {
//                Toast.makeText(getApplicationContext(), response.getString(KEY_MESSAGE), Toast.LENGTH_SHORT).show();
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
        }

        this.loader.hide();
    }

    @Override
    public void OnFailure(String errorMessage, String apiName) {
        this.loader.hide();
        Toast.makeText(getApplicationContext(),errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void OnNetworkError() {
        noInternetPopUp();
        loader.hide();
    }
}