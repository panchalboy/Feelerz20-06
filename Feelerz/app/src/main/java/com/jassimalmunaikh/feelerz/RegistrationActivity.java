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

import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class RegistrationActivity extends AppCompatActivity implements  ServerCallObserver{


    private static final String KEY_STATUS = "status";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_DATA = "data";
    private static final String KEY_EMPTY = "";

    private static final String KEY_DEVICE_TOKEN = "deviceToken";
    private static final String KEY_FULL_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_COUNTRY = "country";
    private static final String KEY_MOBILE = "mobile";
    private static final String KEY_PASSWORD = "password";

    private String deviceToken = "1234";

    RequestHandler requestHandler;
    CustomLoader loader;


    ArrayList<String> CountryString;

    private RequestQueue Queue;

    EditText First_Name_EDT,Last_Name_EDT,Email_EDT,UserName_EDT,Password_EDT,Confirm_Password_EDT;
    TextView Page_Title,First_Name_TXT,Last_Name_TXT,Email_TXT,UserName_TXT,Country_TXT,Country_TXT2,Password_TXT,Confirm_Password_TXT;

    private String name,firstName,lastName,email,username,country,mobile,password,confirmPassword;
    String country_name,country_code,country_shortname,country_id,country_status;
    final Context context = this;

    String text;
    String t11;
    NumberPicker SelectNP;
    String selecPicker,second1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        setStatusBarGadiant(this);

        this.requestHandler = new RequestHandler(getApplicationContext(),this);
        this.loader = new CustomLoader(this,(ViewGroup) getWindow().getDecorView().getRootView());

        CountryString = new ArrayList<>();
        Queue = Volley.newRequestQueue(this);

        Page_Title = findViewById(R.id.Heading_Title);

        First_Name_TXT = findViewById(R.id.First_name);
        Last_Name_TXT = findViewById(R.id.last_name);
        Email_TXT = findViewById(R.id.Email_id);
        UserName_TXT = findViewById(R.id.User_name);
        Country_TXT = findViewById(R.id.Enter_country);
        Password_TXT = findViewById(R.id.password);
        Confirm_Password_TXT = findViewById(R.id.confirm_password);

        First_Name_EDT = findViewById(R.id.type_first_name);
        Last_Name_EDT = findViewById(R.id.type_last_name);
        Email_EDT = findViewById(R.id.type_Email_id);
        UserName_EDT = findViewById(R.id.type_user_name);
        Country_TXT2 = findViewById(R.id.type_spinner);
        Password_EDT = findViewById(R.id.type_password);
        Confirm_Password_EDT = findViewById(R.id.type_confirm_password);

        Button Registration_Btn = findViewById(R.id.Registration_Button);
        Button Back_Btn = findViewById(R.id.Back_Btn);


/////////////////////////////////      FONT     ///////////////////////////////////////////////////////
        Typeface myCustomFontt1 = Typeface.createFromAsset(getAssets(), "fonts/Nunito-Bold.ttf");
        Page_Title.setTypeface(myCustomFontt1);

        Typeface myCustomFontt2 = Typeface.createFromAsset(getAssets(), "fonts/Nunito-Regular.ttf");
        First_Name_TXT.setTypeface(myCustomFontt2);

        Typeface myCustomFontt3 = Typeface.createFromAsset(getAssets(), "fonts/Nunito-Regular.ttf");
        Last_Name_TXT.setTypeface(myCustomFontt3);

        Typeface myCustomFontt4 = Typeface.createFromAsset(getAssets(), "fonts/Nunito-Regular.ttf");
        Email_TXT.setTypeface(myCustomFontt4);

        Typeface myCustomFontt5 = Typeface.createFromAsset(getAssets(), "fonts/Nunito-Regular.ttf");
        UserName_TXT.setTypeface(myCustomFontt5);

        Typeface myCustomFontt6 = Typeface.createFromAsset(getAssets(), "fonts/Nunito-Regular.ttf");
        Country_TXT.setTypeface(myCustomFontt6);

        Typeface myCustomFontt8 = Typeface.createFromAsset(getAssets(), "fonts/Nunito-Regular.ttf");
        Password_TXT.setTypeface(myCustomFontt8);

        Typeface myCustomFontt9 = Typeface.createFromAsset(getAssets(), "fonts/Nunito-Regular.ttf");
        Confirm_Password_TXT.setTypeface(myCustomFontt9);

        Typeface myCustomFontt10 = Typeface.createFromAsset(getAssets(), "fonts/Nunito-Regular.ttf");
        First_Name_EDT.setTypeface(myCustomFontt10);

        Typeface myCustomFontt11 = Typeface.createFromAsset(getAssets(), "fonts/Nunito-Regular.ttf");
        Last_Name_EDT.setTypeface(myCustomFontt11);

        Typeface myCustomFontt12 = Typeface.createFromAsset(getAssets(), "fonts/Nunito-Regular.ttf");
        Email_EDT.setTypeface(myCustomFontt12);

        Typeface myCustomFontt13 = Typeface.createFromAsset(getAssets(), "fonts/Nunito-Regular.ttf");
        UserName_EDT.setTypeface(myCustomFontt13);

        Typeface myCustomFontt14 = Typeface.createFromAsset(getAssets(), "fonts/Nunito-Regular.ttf");
        Country_TXT2.setTypeface(myCustomFontt14);

        Typeface myCustomFontt16 = Typeface.createFromAsset(getAssets(), "fonts/Nunito-Regular.ttf");
        Password_EDT.setTypeface(myCustomFontt16);

        Typeface myCustomFontt17 = Typeface.createFromAsset(getAssets(), "fonts/Nunito-Regular.ttf");
        Confirm_Password_EDT.setTypeface(myCustomFontt17);

        Typeface myCustomFontt18 = Typeface.createFromAsset(getAssets(),"fonts/Nunito-Regular.ttf");
        Registration_Btn.setTypeface(myCustomFontt18);
//////////////////////////////////////////////////////////////////////////////////////////////////////////

        Back_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistrationActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });

        Country_TXT2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show();
            }
        });


//        jsonparse2();
        GetCountryCodes();

        Registration_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                firstName = First_Name_EDT.getText().toString().trim();
                lastName = Last_Name_EDT.getText().toString().trim();
                name = firstName +" "+ lastName;
                if (validationEmailAddress(email)){
                    email = Email_EDT.getText().toString().toLowerCase().trim();
                }
                username = UserName_EDT.getText().toString().trim();
                password = Password_EDT.getText().toString().trim();
                confirmPassword = Confirm_Password_EDT.getText().toString().trim();

                if (validateInputs()) {
                    registerUser();
                }
            }
        });
    }

    private boolean validationEmailAddress(String email) {
        String emailInput = Email_EDT.getText().toString();

        if(!emailInput.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()){
//            Toast.makeText(this,"Email Validated Successfully",Toast.LENGTH_LONG).show();
            return true;
        }else{
            Toast.makeText(this,"Invalid Email Address !",Toast.LENGTH_LONG).show();
            return false;
        }
    }

    public void Registration_SuccessFully_popup(){

        final Dialog dialog = new Dialog(context);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.registration_successfully_popup);
        dialog.setTitle("Select Country");

        TextView dialog_Heading = (TextView)dialog.findViewById(R.id.Success_txt);
        TextView dialog_SubTitle = (TextView)dialog.findViewById(R.id.Success_msg_txt);
        TextView dialog_OK_btn = (TextView) dialog.findViewById(R.id.OK_BTN);
        Typeface myCustomFontt5 = Typeface.createFromAsset(getAssets(), "fonts/nunito-semibold.ttf");
        dialog_Heading.setTypeface(myCustomFontt5);
        Typeface myCustomFontt = Typeface.createFromAsset(getAssets(), "fonts/Nunito-Regular.ttf");
        dialog_SubTitle.setTypeface(myCustomFontt);
        dialog_OK_btn.setTypeface(myCustomFontt5);

        dialog_SubTitle.setText("We have sent an OTP to your email id "+email+", Please enter the OTP back to complete your registration");

        dialog_OK_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegistrationActivity.this, OTP_RegistrationActivity.class);
                startActivity(intent);
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    public void show(){

        final Dialog dialog = new Dialog(context);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.select_country_with_code);
        dialog.setTitle("Select Country");

        TextView dialog_Cancel_btn = (TextView) dialog.findViewById(R.id.cancel_btn);
        TextView dialog_OK_btn = (TextView) dialog.findViewById(R.id.OK_btn);

        SelectNP = (NumberPicker) dialog.findViewById(R.id.Select_Country);

        // Convert string arraylist to string array.
        String[] data = new String[CountryString.size()];
        data = CountryString.toArray(data);


        //Populate NumberPicker values from String array values
        //Set the minimum value of NumberPicker
        SelectNP.setMinValue(0); //from array first value
        //Specify the maximum value/number of NumberPicker
        SelectNP.setMaxValue(data.length-1); //to array last value

        //Specify the NumberPicker data source as array elements
        SelectNP.setDisplayedValues(data);

        //Gets whether the selector wheel wraps when reaching the min/max value.
        SelectNP.setWrapSelectorWheel(true);

        //Set a value change listener for NumberPicker
        String[] finalData1 = data;
        SelectNP.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                //Display the newly selected value from picker

//                String Data = SelectCountry.setText(finalData1[newVal]);
                int pos = SelectNP.getValue();
                //get string from number picker and position
                selecPicker = finalData1[pos];
                //test toast to get selected text string
//                Toast.makeText(context, selecPicker , Toast.LENGTH_SHORT).show();
            }
        });

        dialog_OK_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Country_TXT2.setText(selecPicker);
                StringTokenizer tokens = new StringTokenizer(selecPicker, " ");
                String first1 = tokens.nextToken();
                second1 = tokens.nextToken();
                StringTokenizer tokens11 = new StringTokenizer(first1, "+");
                t11 = tokens11.nextToken();
                dialog.dismiss();
            }
        });

        dialog_Cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    public void jsonparse2(){

        String url = getString(R.string.Link)+"all_cuntry";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getInt(KEY_STATUS) == 1) {

                        JSONArray jsonArray = response.getJSONArray("data");

                        for(int i = 0; i < jsonArray.length();i++) {
                            JSONObject json = jsonArray.getJSONObject(i);

//                            country_id = json.getString("id");
//                            country_shortname = json.getString("sortname");
                            country_code = json.getString("country_code");
                            country_name = json.getString("name");
//                            country_status = json.getString("status");

//                            CountryName.add(country_id);
//                            CountryName.add(country_shortname);

                            CountryString.add("+"+country_code +" "+country_name);
//                            CountryName.add(country_name);
//                            CountryName.add(country_status);
                        }
                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                error.printStackTrace();
            }
        });
        Queue.add(request);
    }
    public void GetCountryCodes(){
        loader.show();
        Map<String,String> request = new HashMap<String,String>();
        requestHandler.MakeRequest(request,"all_cuntry");

    }


    private void registerUser(){

        Map<String,String> request = new HashMap<String,String>();

        request.put(KEY_DEVICE_TOKEN, deviceToken);
        request.put(KEY_FULL_NAME, name);
        request.put(KEY_EMAIL, email);
        request.put(KEY_USERNAME, username);
        request.put(KEY_COUNTRY, second1);
        request.put(KEY_MOBILE,t11);
        request.put(KEY_PASSWORD, password);
        UserDataCache.GetInstance().email = email;

        this.loader.show();
        this.requestHandler.MakeRequest(request,"self_register");

    }

    private void send_opt() {

        Map<String,String> request = new HashMap<String,String>();

        request.put(KEY_EMAIL, email);

        this.loader.show();
        this.requestHandler.MakeRequest(request,"send_otp");
    }

    private void setStatusBarGadiant(RegistrationActivity registrationActivity) {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            Window window =registrationActivity.getWindow();
            Drawable background = registrationActivity.getResources().getDrawable(R.drawable.background_color);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(registrationActivity.getResources().getColor(android.R.color.transparent));
            window.setBackgroundDrawable(background);
        }
    }

    private boolean validateInputs() {
        if (KEY_EMPTY.equals(firstName)) {
//            First_Name_EDT.setError("First Name cannot be empty");
//            First_Name_EDT.requestFocus();
            Toast.makeText(this,"First Name cannot be empty",Toast.LENGTH_LONG).show();
            return false;
        }
        if (KEY_EMPTY.equals(lastName)) {
//            Last_Name_EDT.setError("Last Name cannot be empty");
//            Last_Name_EDT.requestFocus();
            Toast.makeText(this,"Last Name cannot be empty",Toast.LENGTH_LONG).show();
            return false;
        }
        if (KEY_EMPTY.equals(email)) {
//            Email_EDT.setError("Email cannot be empty");
//            Email_EDT.requestFocus();
            Toast.makeText(this,"Email cannot be empty",Toast.LENGTH_LONG).show();
            return false;
        }
        if (KEY_EMPTY.equals(username)) {
//            UserName_EDT.setError("Username cannot be empty");
//            UserName_EDT.requestFocus();
            Toast.makeText(this,"Username cannot be empty",Toast.LENGTH_LONG).show();
            return false;
        }
        if (KEY_EMPTY.equals(country)) {
//            Country_EDT.setError("Country cannot be empty");
//            Country_EDT.requestFocus();
            Toast.makeText(this,"Country cannot be empty",Toast.LENGTH_LONG).show();
            return false;
        }
        if (KEY_EMPTY.equals(password)) {
//            Password_EDT.setError("Password cannot be empty");
//            Password_EDT.requestFocus();
            Toast.makeText(this,"Password cannot be empty",Toast.LENGTH_LONG).show();
            return false;
        }
        if (KEY_EMPTY.equals(confirmPassword)) {
//            Confirm_Password_EDT.setError("Confirm Password cannot be empty");
//            Confirm_Password_EDT.requestFocus();
            Toast.makeText(this,"Confirm Password cannot be empty",Toast.LENGTH_LONG).show();
            return false;
        }
        if (!password.equals(confirmPassword)) {
            Toast.makeText(RegistrationActivity.this,"Password and Confirm password does not match.",Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
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

        if(apiName == "self_register") {

            send_opt();
            try {
                UserDataCache.GetInstance().email = email;
                UserDataCache.GetInstance().setRegistrationdata(response.getJSONObject(KEY_DATA));
            } catch (JSONException e) {
                e.printStackTrace();
            }

                Registration_SuccessFully_popup();


        }
        else if(apiName == "send_otp")
        {
            /*UserDataCache.GetInstance().email = email;*/

        }
        else if(apiName == "all_cuntry")
        {
            JSONArray jsonArray = null;
            try {
                jsonArray = response.getJSONArray("data");
                for(int i = 0; i < jsonArray.length();i++) {
                    JSONObject json = jsonArray.getJSONObject(i);

//                            country_id = json.getString("id");
//                            country_shortname = json.getString("sortname");
                    country_code = json.getString("country_code");
                    country_name = json.getString("name");
//                            country_status = json.getString("status");

//                            CountryName.add(country_id);
//                            CountryName.add(country_shortname);

                    CountryString.add("+"+country_code +" "+country_name);
//                            CountryName.add(country_name);
//                            CountryName.add(country_status);

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    this.loader.hide();
    }

    @Override
    public void OnFailure(String errorMessage, String apiName) {

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.error_email_already_taken_popup);
        dialog.setTitle("Email Already Taken...");

        TextView error_txt = dialog.findViewById(R.id.error_txt);
        TextView email_txt = dialog.findViewById(R.id.error_message);
        TextView OK_BTN = dialog.findViewById(R.id.OK_BTN);

        Typeface myCustomFontt1 = Typeface.createFromAsset(getAssets(), "fonts/Nunito-Bold.ttf");
        error_txt.setTypeface(myCustomFontt1);

        Typeface myCustomFontt2 = Typeface.createFromAsset(getAssets(), "fonts/nunito-semibold.ttf");
        email_txt.setTypeface(myCustomFontt2);
//        email_txt.setText(KEY_MESSAGE);

        Typeface myCustomFontt3 = Typeface.createFromAsset(getAssets(), "fonts/nunito-semibold.ttf");
        OK_BTN.setTypeface(myCustomFontt3);

        OK_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
        this.loader.hide();
    }

    @Override
    public void OnNetworkError() {
        noInternetPopUp();
        loader.hide();
    }
}