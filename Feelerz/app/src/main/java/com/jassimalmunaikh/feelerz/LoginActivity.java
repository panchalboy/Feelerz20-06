package com.jassimalmunaikh.feelerz;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jassimalmunaikh.feelerz.social.FacebookLogin;
import com.jassimalmunaikh.feelerz.social.GoogleLogin;
import com.jassimalmunaikh.feelerz.social.SnapChatLogin;
import com.jassimalmunaikh.feelerz.social.SocialLogin;
import com.jassimalmunaikh.feelerz.social.SocialLoginObserver;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static android.widget.Toast.makeText;


public class LoginActivity extends AppCompatActivity implements ServerCallObserver, SocialLoginObserver {

    CustomLoader loader;
    RequestHandler requestHandler;

    private static final String KEY_STATUS = "status";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_DATA = "data";
    private static final String KEY_EMPTY = "";

    private static final String KEY_DEVICE_TOKEN = "deviceToken";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";

    private static final String KEY_FORGET_EMAIL = "email";

    private EditText Login_Email;
    private EditText Login_Password;

    private String email;
    private String password;
    private String FEmail;

    Boolean passwordVisible;

    private ProgressDialog pDialog;
    private SessionHandler session;

    View revealPassword;
    final Context context = this;
    EditText popup_email;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SocialLogin.GOOGLE)
        {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {

                GoogleSignInAccount account = task.getResult(ApiException.class);
                Map<String,String> request = new HashMap<String,String>();
                request.put("fname",account.getDisplayName());
                request.put("lname","");
                request.put("email",account.getEmail());
                request.put("mobile","");
                request.put("country","");
                request.put("password","");
                request.put("social_id",account.getId());
                request.put("types","Google");
                request.put("deviceToken",UserDataCache.GetInstance().currentFCMToken);

                socialLogin(request);
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
        if(GlobalCache.GetInstance().currentSocialLogin.GetType() == SocialLogin.FACEBOOK)
        {
            ((FacebookLogin)GlobalCache.GetInstance().currentSocialLogin).GetCallbackManager().onActivityResult(requestCode,resultCode,data);
        }
    }

    void SetSocialLogins()
    {
        Button googleSignInBtn = findViewById(R.id.google_btn);
        googleSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalCache.GetInstance().currentSocialLogin = new GoogleLogin();
                GlobalCache.GetInstance().currentSocialLogin.Initialize(LoginActivity.this,null);
                GlobalCache.GetInstance().currentSocialLogin.SignIn();
            }
        });

        Button FBSignInBtn = findViewById(R.id.facebook_btn);
        FBSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalCache.GetInstance().currentSocialLogin = new FacebookLogin(LoginActivity.this);
                GlobalCache.GetInstance().currentSocialLogin.Initialize(LoginActivity.this,null);
                GlobalCache.GetInstance().currentSocialLogin.SignIn();
            }
        });

        View snapChat = findViewById(R.id.snapchat_btn);
        snapChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalCache.GetInstance().currentSocialLogin = new SnapChatLogin(LoginActivity.this);
                GlobalCache.GetInstance().currentSocialLogin.Initialize(LoginActivity.this,null);
                GlobalCache.GetInstance().currentSocialLogin.SignIn();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarGadiant(this);
        this.passwordVisible = true;
        this.session = SessionHandler.GetInstance();
        this.session.InitForCurrentContext(getApplicationContext());

        if(this.session.isLoggedIn())
        {
            int socialLoginType = session.GetSocialLoginType();
            if(socialLoginType == SocialLogin.FACEBOOK)
                GlobalCache.GetInstance().currentSocialLogin = new FacebookLogin(this);
            else if(socialLoginType == SocialLogin.GOOGLE)
                GlobalCache.GetInstance().currentSocialLogin = new GoogleLogin();
            else if(socialLoginType == SocialLogin.SNAPCHAT)
                GlobalCache.GetInstance().currentSocialLogin = new SnapChatLogin(this);
            else if(socialLoginType == SocialLogin.TWITTER)
                GlobalCache.GetInstance().currentSocialLogin = null;

            if(GlobalCache.GetInstance().currentSocialLogin != null)
                GlobalCache.GetInstance().currentSocialLogin.Initialize(this,null);
            UserDataCache.GetInstance().SetLoginData(this.session.getData());
            loadDashboard();
            return;
        }
        setStatusBarGadiant(this);
        setContentView(R.layout.activity_login);

        this.requestHandler = new RequestHandler(getApplicationContext(),this);
        this.loader = new CustomLoader(this,(ViewGroup)getWindow().getDecorView().getRootView());

        this.revealPassword = findViewById(R.id.reveal_password);
        Login_Email = findViewById(R.id.etLoginUsername);
        Login_Password = findViewById(R.id.etLoginPassword);

        TextView Forget_Password = findViewById(R.id.Login_forget_pswd);

        final Button Login_btn = findViewById(R.id.btnLogin);
        Button Registration_btn = findViewById(R.id.btnRegister);

        SetSocialLogins();


        this.revealPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

              passwordVisible = !passwordVisible;

              TransformationMethod mode = passwordVisible ?
                      PasswordTransformationMethod.getInstance() :
                      HideReturnsTransformationMethod.getInstance();

              Login_Password.setTransformationMethod(mode);
              visibilityPassword(passwordVisible);
              return false;
            }
        });

//////////////////////////   FONTS ////////////////////////////////////////////////////////////
        Typeface myCustomFontt1 = Typeface.createFromAsset(getAssets(),"fonts/Nunito-Regular.ttf");
        Login_Email.setTypeface(myCustomFontt1);

        Typeface myCustomFontt2 = Typeface.createFromAsset(getAssets(),"fonts/Nunito-Regular.ttf");
        Login_Password.setTypeface(myCustomFontt2);

        Typeface myCustomFontt3 = Typeface.createFromAsset(getAssets(),"fonts/Nunito-Regular.ttf");
        Forget_Password.setTypeface(myCustomFontt3);

        Typeface myCustomFontt4 = Typeface.createFromAsset(getAssets(),"fonts/Nunito-Regular.ttf");
        Login_btn.setTypeface(myCustomFontt4);

        Typeface myCustomFontt5 = Typeface.createFromAsset(getAssets(),"fonts/Nunito-Regular.ttf");
        Registration_btn.setTypeface(myCustomFontt5);
//////////////////////////////////////////////////////////////////////////////////////////////////////////

        Login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (validationEmailAddress(email)){
                    email = Login_Email.getText().toString().toLowerCase().trim();
                }
                password = Login_Password.getText().toString().trim();
                if (validateInputs()) {
                    login();
                }
            }
        });
        Registration_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,RegistrationActivity.class);
                startActivity(intent);
            }
        });

//      Forget Password popup start.
        Forget_Password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // custom dialog
                final Dialog dialog = new Dialog(context);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setContentView(R.layout.forget_password_popup);
                dialog.setTitle("Forget Password...");

                TextView text = (TextView) dialog.findViewById(R.id.TXT_LOGIN_ID_popup);
                popup_email = (EditText) dialog.findViewById(R.id.ET_EMAIL_ID_popup);
                Button dialog_Submit_btn = (Button) dialog.findViewById(R.id.SUBMIT_LOGIN_ID_popup);

////////////////////////////////////  FONTS  ////////////////////////////////////////////////////////////////
                Typeface myCustomFontt1 = Typeface.createFromAsset(getAssets(), "fonts/nunito-semibold.ttf");
                text.setTypeface(myCustomFontt1);

                Typeface myCustomFontt2 = Typeface.createFromAsset(getAssets(), "fonts/Nunito-Regular.ttf");
                popup_email.setTypeface(myCustomFontt2);

                Typeface myCustomFontt3 = Typeface.createFromAsset(getAssets(), "fonts/nunito-semibold.ttf");
                dialog_Submit_btn.setTypeface(myCustomFontt3);
///////////////////////////////////////////////////////////////////////////////////////////////////

                dialog_Submit_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (validationEmailAddress2(FEmail)){
                            FEmail = popup_email.getText().toString().toLowerCase().trim();
                        }
                         if (validateInputs()) {
                            forget();
                        }
                    }
                });

                Button dialog_cancel_Button = (Button) dialog.findViewById(R.id.Close_popup_btn);

                dialog_cancel_Button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
//         Forget Password popup end.
    }
//      Code for disable back button when user logout.
    @Override
    public void onBackPressed() {

    }

    private void visibilityPassword(Boolean visibility){

        if(visibility){
            revealPassword.setBackgroundResource(R.drawable.login_password_eye_icon_on_shape);
        }
        else {
            revealPassword.setBackgroundResource(R.drawable.login_password_eye_icon_off_shape);
        }
    }

    private void loadDashboard() {
        Intent i = new Intent(getApplicationContext(), DashboardActivity.class);
        startActivity(i);
        finish();
    }

    private void displayLoader() {
        pDialog = new ProgressDialog(LoginActivity.this);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("Please wait...");
        pDialog.setIndeterminate(true);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    private boolean validationEmailAddress(String email) {
        String emailInput = Login_Email.getText().toString();

        if(!emailInput.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()){
//            Toast.makeText(this,"Email Validated Successfully",Toast.LENGTH_LONG).show();
            return true;
        }else{
            Toast.makeText(this,"Invalid Email Address !",Toast.LENGTH_LONG).show();
            return false;
        }
    }

    private boolean validationEmailAddress2(String FEmail) {
        String emailInput = popup_email.getText().toString();

        if(!emailInput.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()){
//            Toast.makeText(this,"Email Validated Successfully",Toast.LENGTH_LONG).show();
            return true;
        }else{
            Toast.makeText(this,"Invalid Email Address !",Toast.LENGTH_LONG).show();
            return false;
        }
    }


    private void socialLogin(Map<String,String> request)
    {
        this.loader.show();
        this.requestHandler.MakeRequest(request,"social_login");

    }

    private void login() {

        this.loader.show();

        Map<String,String> request = new HashMap<String,String>();

        request.put(KEY_DEVICE_TOKEN,UserDataCache.GetInstance().currentFCMToken);
        request.put(KEY_EMAIL, email);
        request.put(KEY_PASSWORD, password);

        this.requestHandler.MakeRequest(request,"user_login");

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

    //forget code....
    private  void forget(){

        this.loader.show();

        Map<String,String> request = new HashMap<String,String>();

        request.put(KEY_FORGET_EMAIL,FEmail);

        this.requestHandler.MakeRequest(request,"forgate");

    }

    private void setStatusBarGadiant(DashboardActivity dashboardActivity) {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            Window window =dashboardActivity.getWindow();
            Drawable background = dashboardActivity.getResources().getDrawable(R.drawable.background_color);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(dashboardActivity.getResources().getColor(android.R.color.transparent));
            window.setBackgroundDrawable(background);
        }
    }

    /**
     * Validates inputs and shows error if any
     * @return
     */

    private boolean validateInputs() {

        if(KEY_EMPTY.equals(FEmail)){
            Toast.makeText(LoginActivity.this,"Please enter id",Toast.LENGTH_LONG).show();
            return false;
        }

        if(KEY_EMPTY.equals(email)){
            Toast.makeText(LoginActivity.this,"Please enter id",Toast.LENGTH_LONG).show();
            return false;
        }
        if(KEY_EMPTY.equals(password)){
            Toast.makeText(LoginActivity.this,"Please enter Password",Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void setStatusBarGadiant(LoginActivity loginActivity) {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            Window window =loginActivity.getWindow();
            Drawable background = loginActivity.getResources().getDrawable(R.drawable.background_color);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(loginActivity.getResources().getColor(android.R.color.transparent));
            window.setBackgroundDrawable(background);
        }
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
        if(apiName =="user_login" || apiName == "social_login")
        {
            try {
                JSONObject userData = response.getJSONObject(KEY_DATA);
                LoginActivity.this.session.onSuccessfullLogin(userData);
                UserDataCache.GetInstance().SetLoginData(response.getJSONObject(KEY_DATA));
                loadDashboard();

//              Toast.makeText(getApplicationContext(), response.getString(KEY_MESSAGE), Toast.LENGTH_SHORT).show();
            }
            catch (JSONException e){}
        }
        else if (apiName == "forgate"){

            UserDataCache.GetInstance().email = FEmail;

            try {
                UserDataCache.GetInstance().setforgetdata(response.getJSONObject(KEY_DATA));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent(LoginActivity.this,OTP_Forget_Activity.class);
            startActivity(intent);
            OTP_Send();

//            Toast.makeText(getApplicationContext(), response.getString(KEY_MESSAGE), Toast.LENGTH_SHORT).show();

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

    @Override
    public void OnLoginSuccess(Map<String, String> request) {
     request.put("deviceToken",UserDataCache.GetInstance().currentFCMToken);
     socialLogin(request);
    }

    @Override
    public void OnLoginFailure() {

    }
}