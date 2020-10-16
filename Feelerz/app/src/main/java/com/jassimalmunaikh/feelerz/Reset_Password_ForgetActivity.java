package com.jassimalmunaikh.feelerz;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class Reset_Password_ForgetActivity extends AppCompatActivity {

    private static final String KEY_STATUS = "status";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_FGT_PASSWORD ="new_password";
    private static final String KEY_USER_ID ="user_id";
    private static final String KEY_EMPTY = "";

    private String password,c_password,Fid;

    Button Set_Password_Btn,Back_Btn;
    TextView Page_Title;
    EditText TXT_New_Password,TXT_New_Confirm_Password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarGadiant(this);
        setContentView(R.layout.activity_reset__password__forget);

        Page_Title = findViewById(R.id.Title_page);

        Set_Password_Btn = findViewById(R.id.Update_Password_btn);
        Back_Btn = findViewById(R.id.Back_Btn);

        TXT_New_Password = findViewById(R.id.et_password);
        TXT_New_Confirm_Password = findViewById(R.id.et_con_password);

///////////////////////////////////////////////////////////////////////////////////////////////////////
        Typeface myCustomFontta = Typeface.createFromAsset(getAssets(), "fonts/Nunito-Bold.ttf");
        Page_Title.setTypeface(myCustomFontta);

        Typeface myCustomFontt2 = Typeface.createFromAsset(getAssets(), "fonts/Nunito-Regular.ttf");
        TXT_New_Password.setTypeface(myCustomFontt2);

        Typeface myCustomFontt3 = Typeface.createFromAsset(getAssets(), "fonts/Nunito-Regular.ttf");
        TXT_New_Confirm_Password.setTypeface(myCustomFontt3);

        Typeface myCustomFontt4 = Typeface.createFromAsset(getAssets(), "fonts/Nunito-Bold.ttf");
        Set_Password_Btn.setTypeface(myCustomFontt4);
///////////////////////////////////////////////////////////////////////////////////////////////////////////

        Back_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Reset_Password_ForgetActivity.this,OTP_Forget_Activity.class);
                startActivity(intent);
            }
        });

        Set_Password_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                password = TXT_New_Password.getText().toString().trim();
                c_password = TXT_New_Confirm_Password.getText().toString().trim();

                if (validateInputs()) {
                    change_password();
                }
            }
        });
    }

    private void setStatusBarGadiant(Reset_Password_ForgetActivity loginActivity) {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            Window window =loginActivity.getWindow();
            Drawable background = loginActivity.getResources().getDrawable(R.drawable.background_color);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(loginActivity.getResources().getColor(android.R.color.transparent));
            window.setBackgroundDrawable(background);
        }
    }

    private void change_password(){
        String new_password_url = getString(R.string.Link)+"set_new_password";
        JSONObject request = new JSONObject();
        try {
            Fid = UserDataCache.GetInstance().id;
            request.put(KEY_USER_ID,Fid);
            request.put(KEY_FGT_PASSWORD,password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.POST, new_password_url, request, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getInt(KEY_STATUS) == 1) {

                        Intent intent = new Intent(Reset_Password_ForgetActivity.this,LoginActivity.class);
                        startActivity(intent);

                        Toast.makeText(getApplicationContext(), response.getString(KEY_MESSAGE), Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(),response.getString(KEY_MESSAGE), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        MySingleton.getInstance(this).addToRequestQueue(jsArrayRequest);
    }

    private boolean validateInputs() {

        if (KEY_EMPTY.equals(password)) {
            TXT_New_Password.setError("Password cannot be empty");
            TXT_New_Password.requestFocus();
            return false;
        }
        if (KEY_EMPTY.equals(c_password)) {
            TXT_New_Confirm_Password.setError("Confirm Password cannot be empty");
            TXT_New_Confirm_Password.requestFocus();
            return false;
        }
        if (!password.equals(c_password)) {
            TXT_New_Confirm_Password.setError("Password and Confirm Password does not match");
            TXT_New_Confirm_Password.requestFocus();
            return false;
        }
        return true;
    }
}