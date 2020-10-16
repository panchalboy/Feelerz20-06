package com.jassimalmunaikh.feelerz;

import com.jassimalmunaikh.feelerz.BuildConfig;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.NonNull;

import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.internal.metrics.Tag;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class SplashActivity extends AppCompatActivity implements ServerCallObserver{

    int versionCode = BuildConfig.VERSION_CODE;
    String versionName = BuildConfig.VERSION_NAME;

    Typeface myCustomFontt1,myCustomFontt2,myCustomFontt3,myCustomFontt4;
    private static int SPLASH_TIMEOUT = 1000;
    RequestHandler requestHandler;
    int Version;


    @Override
    protected void onResume() {
        super.onResume();

        GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestHandler = new RequestHandler(getApplicationContext(),this);


        GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(this);

        SettextSytle();

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(SplashActivity.this, "FirebaseInstance.getInstanceId failed", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        // Get new Instance ID token
                        String tokenRcvd = task.getResult().getToken();
                        String token = Utilities.getInstance().updateFCMToken(tokenRcvd);

//                        Toast.makeText(SplashActivity.this, token, Toast.LENGTH_SHORT).show();
                    }
                });
        printHashKey(SplashActivity.this);
        setStatusBarGadiant(this);
        setContentView(R.layout.activity_splash);

        TextView CopyRight = (TextView)findViewById(R.id.Copyright);
        CopyRight.setTypeface(myCustomFontt2);

        Typeface myCustomFontt = Typeface.createFromAsset(getAssets(),"fonts/Nunito-Regular.ttf");
        CopyRight.setTypeface(myCustomFontt);

//        checkVersion();
        startSplash();
    }

    public void startSplash(){
        new Handler().postDelayed(new Runnable(){

            @Override
            public void run(){
//                FragmentManager fragmentManager = getSupportFragmentManager();
//                fragmentManager.beginTransaction().replace(R.id.Main_Layout,new HomeFragment()).addToBackStack(null).commit();
                Intent intent = new Intent(SplashActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        },SPLASH_TIMEOUT);
    }

    public void checkVersion() {
//        loader.show();
        int pagevalue = 0;
        Map<String,String> request = new HashMap<String,String>();
        requestHandler.MakeRequest(request,"android_version_control");
    }

    public static void printHashKey(Context pContext){
        try {
            PackageInfo info = pContext.getPackageManager().getPackageInfo(pContext.getPackageName(), PackageManager.GET_SIGNATURES);
                for(Signature signature: info.signatures){
                    MessageDigest md = MessageDigest.getInstance("SHA");
                    md.update(signature.toByteArray());
                    String hashKey = new String(Base64.encode(md.digest(),0));
                    Log.i("SplashActivity","printHashKey() Hash Key:"+hashKey);
                }
        }catch (NoSuchAlgorithmException e){
            Log.e("SplashActivity","PrintHashKey()",e);
        }catch (Exception e){
            Log.e("SplashActivity","printHashKey()",e);
        }
    }

    public void SettextSytle(){

        myCustomFontt1 = Typeface.createFromAsset(getAssets(),"fonts/Klavika-Regular.ttf");
        myCustomFontt2 = Typeface.createFromAsset(getAssets(),"fonts/Nunito-Bold.ttf");
        myCustomFontt3 = Typeface.createFromAsset(getAssets(),"fonts/Nunito-Regular.ttf");
        myCustomFontt4 = Typeface.createFromAsset(getAssets(),"fonts/nunito-semibold.ttf");
    }

    private void setStatusBarGadiant(SplashActivity splashActivity) {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            Window window =splashActivity.getWindow();
            Drawable background = splashActivity.getResources().getDrawable(R.drawable.background_color);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(splashActivity.getResources().getColor(android.R.color.transparent));
            window.setBackgroundDrawable(background);
        }
    }

    private void upgradeAppPopUp(){
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.new_version_upgrade_popup);
        dialog.setTitle("Update Available");

        TextView dialog_update_btn = (TextView) dialog.findViewById(R.id.Update_BTN);
        dialog_update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectPlaystore();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void redirectPlaystore(){
        try {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=com.jassimalmunaikh.feelerz&hl=en" + getPackageName())));
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=com.jassimalmunaikh.feelerz&hl=en" + getPackageName())));
        }
    }

    @Override
    public void OnSuccess(JSONObject response, String apiName) {
        if(apiName =="android_version_control") {
            try {
                 Version = response.getJSONObject("data").getInt("version_code");
                if(Version>versionCode){
//                    startSplash();
                    upgradeAppPopUp();
//                    Toast.makeText(SplashActivity.this, Version+"  "+versionCode+"  "+versionName, Toast.LENGTH_SHORT).show();
                }else {
                    startSplash();
                }
            }
            catch (JSONException e){}
        }
//        this.loader.hide();
    }

    @Override
    public void OnFailure(String errorMessage, String apiName) {

    }

    @Override
    public void OnNetworkError() {

    }
}
