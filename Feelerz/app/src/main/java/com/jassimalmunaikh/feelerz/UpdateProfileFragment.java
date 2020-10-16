package com.jassimalmunaikh.feelerz;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;

import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.Html;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static android.widget.Toast.makeText;

/**
 * A simple {@link Fragment} subclass.
 */
public class UpdateProfileFragment extends Fragment implements ServerCallObserver, TopLevelFrag{

    Intent CropIntent;
    Uri uri;
    Typeface myCustomFontt1,myCustomFontt2,myCustomFontt3,myCustomFontt4;

    UpdateProfileFragment context = this;
    String country_name,country_code,country_shortname,country_id,country_status;

    private static final String KEY_STATUS = "status";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_PROFILE_IMAGE = "profileimage";
    private static final String KEY_FULL_NAME = "name";
    private static final String KEY_GENDER = "gender";
    private static final String KEY_COUNTRY = "country";
    private static final String KEY_CONTACT = "contact";
    private static final String KEY_DATE_OF_BIRTH = "dob";
    private static final String KEY_BIO = "bio";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_USER_ID = "user_id";
    ArrayList<String> CountryString;
    private RequestQueue Queue;
    String selecPicker,selecPickergender;
    NumberPicker SelectNP;
    CustomLoader loader;
    RequestHandler requestHandler;
    SessionHandler handler;
    TextView UpdateProfile,Update_Profile_Title,UserCountry,UserDateOfBirth,UserGender;
    EditText UserFullName,UserContact,UserBio,UserName;
    String ProfilePicID,userFullName,userGender,userCountry,userContact,userDateOfBirth,userBio,userName;
    Button Back_btn;
    View view1;

    CircleImageView profilePic;
    Bitmap bitmap = null;

    DatePicker DatePicker;


    public UpdateProfileFragment() {
        // Required empty public constructor

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_update__profile, container, false);
        this.view1 = view;
        removePhoneKeypad();
        Queue = Volley.newRequestQueue(getContext());
        SettextSytle();

        SessionHandler.GetInstance().InitForCurrentContext(getContext());
        GlobalFragmentStack.getInstance().Register(this);

        this.requestHandler = new RequestHandler(getContext(),this);
        this.loader = new CustomLoader(getActivity(),(ViewGroup)view);
        CountryString = new ArrayList<>();
//        RequestQueue Queue = Volley.newRequestQueue(this);
//        Queue = Volley.newRequestQueue(this);

        profilePic = view.findViewById(R.id.Profile);
      //  profilePic.setAdjustViewBounds(true);

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });

        Back_btn =  (Button)view.findViewById(R.id.Back_Btn);
        UpdateProfile = view.findViewById(R.id.UpdateProfile_btn);

        Update_Profile_Title = view.findViewById(R.id.Update_Profile_Title);
        UserFullName = view.findViewById(R.id.Full_Name);
        UserGender = view.findViewById(R.id.Gender);
        UserCountry = view.findViewById(R.id.Country);
        UserContact = view.findViewById(R.id.Mobile);
        UserDateOfBirth = view.findViewById(R.id.Date_Of_Birth);
        UserBio = view.findViewById(R.id.Biography);
        UserName = view.findViewById(R.id.UserName);

        Update_Profile_Title.setTypeface(myCustomFontt4);
        UserFullName.setTypeface(myCustomFontt3);
        UserGender.setTypeface(myCustomFontt3);
        UserCountry.setTypeface(myCustomFontt3);
        UserContact.setTypeface(myCustomFontt3);
        UserDateOfBirth.setTypeface(myCustomFontt3);
        UserBio.setTypeface(myCustomFontt3);
        UserName.setTypeface(myCustomFontt3);
        UpdateProfile.setTypeface(myCustomFontt4);

        ProfilePicID = UserDataCache.GetInstance().imageId;
        String url = getResources().getString(R.string.ImageURL) + ProfilePicID;
        Picasso.get().load(url).error(R.drawable.or_username).placeholder(R.drawable.or_username).into(profilePic);

        userFullName = UserDataCache.GetInstance().name;
        UserFullName.setText(Html.fromHtml(StringEscapeUtils.unescapeJava(userFullName)));

        userGender = UserDataCache.GetInstance().gender;
        UserGender.setText(userGender);

        userCountry = UserDataCache.GetInstance().country;
        UserCountry.setText(userCountry);

        userContact = UserDataCache.GetInstance().contact;
        UserContact.setText("+"+userContact);

        userDateOfBirth = UserDataCache.GetInstance().dob;
        UserDateOfBirth.setText(userDateOfBirth);

        userBio = UserDataCache.GetInstance().bio;
        UserBio.setText(Html.fromHtml(StringEscapeUtils.unescapeJava(userBio)));

        userName = UserDataCache.GetInstance().userName;
        UserName.setText(userName);

        UserCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show();
            }
        });

        UserDateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                datepopup();

                        final Calendar c = Calendar.getInstance();
                        int mYear = c.get(Calendar.YEAR);
                        int mMonth = c.get(Calendar.MONTH);
                        int mDay = c.get(Calendar.DAY_OF_MONTH);

                        final DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                                new DatePickerDialog.OnDateSetListener() {
                                    String fmonth, fDate;
                                    int month;

                                    @Override
                                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                                        try {
                                            if (monthOfYear < 10 && dayOfMonth < 10) {

                                                fmonth = "0" + monthOfYear;
                                                month = Integer.parseInt(fmonth) + 1;
                                                fDate = "0" + dayOfMonth;
                                                String paddedMonth = String.format("%02d", month);
                                                UserDateOfBirth.setText(fDate + "/" + paddedMonth + "/" + year);

                                            } else {

                                                fmonth = "0" + monthOfYear;
                                                month = Integer.parseInt(fmonth) + 1;
                                                String paddedMonth = String.format("%02d", month);
                                                UserDateOfBirth.setText(dayOfMonth + "/" + paddedMonth + "/" + year);
                                            }

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                    }

                                }, mYear, mMonth, mDay);
                        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                        datePickerDialog.show();

                    }
                });

        UserGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                genderpopup();
            }
        });


        Back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnManualClose();
            }
        });

        UpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userFullName = UserFullName.getText().toString().trim();
                userGender = UserGender.getText().toString().trim();
                userCountry = UserCountry.getText().toString().trim();
                userContact = UserContact.getText().toString().trim();
                userDateOfBirth = UserDateOfBirth.getText().toString().trim();
                userBio = Html.fromHtml(StringEscapeUtils.escapeJava(UserBio.getText().toString().trim())).toString();
                userName = UserName.getText().toString().trim();

                uploadprofile();
            }
        });
        GetCountryCodes();
        return view;
    }

    public void removePhoneKeypad() {
        InputMethodManager inputManager = (InputMethodManager) view1
                .getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        IBinder binder = view1.getWindowToken();
        inputManager.hideSoftInputFromWindow(binder,
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void GetCountryCodes(){
        loader.show();

        Map<String,String> request = new HashMap<String,String>();
        requestHandler.MakeRequest(request,"all_cuntry");

    }

    public void show(){

        final Dialog dialog = new Dialog(getContext());
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
        SelectNP.setMaxValue(data.length - 1); //to array last value

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

                StringTokenizer tokens = new StringTokenizer(selecPicker, " ");
                String first1 = tokens.nextToken();
                String second1 = tokens.nextToken();
                UserContact.setText(first1);
                UserCountry.setText(second1);
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

    public void SettextSytle(){

        myCustomFontt1 = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Klavika-Regular.ttf");
        myCustomFontt2 = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Nunito-Bold.ttf");
        myCustomFontt3 = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Nunito-Regular.ttf");
        myCustomFontt4 = Typeface.createFromAsset(getActivity().getAssets(),"fonts/nunito-semibold.ttf");
    }

    private void genderpopup(){
        // custom dialog
        final Dialog dialog = new Dialog(getContext());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.gender_selecter_popup);
        dialog.setTitle("Select Country");

        TextView dialog_Cancel_btn = (TextView) dialog.findViewById(R.id.cancel_btn);
        TextView dialog_OK_btn = (TextView) dialog.findViewById(R.id.OK_btn);

        final NumberPicker np = (NumberPicker) dialog.findViewById(R.id.Select_Country);

        //Initializing a new string array with elements
        final String[] values= {"Male","Female", "Other"};

        //Populate NumberPicker values from String array values
        //Set the minimum value of NumberPicker
        np.setMinValue(0); //from array first value
        //Specify the maximum value/number of NumberPicker
        np.setMaxValue(values.length-1); //to array last value

        //Specify the NumberPicker data source as array elements
        np.setDisplayedValues(values);

        //Gets whether the selector wheel wraps when reaching the min/max value.
        np.setWrapSelectorWheel(true);

        //Set a value change listener for NumberPicker
        np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                //Display the newly selected value from picker
                int pos = np.getValue();
                //get string from number picker and position
                selecPickergender = values[pos];
                //test toast to get selected text string
            }
        });

        dialog_OK_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                UserGender.setText(selecPickergender);

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

    private void datepopup(){
        // custom dialog
        final Dialog dialog = new Dialog(getContext());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.date_selecter_popup);
        dialog.setTitle("Select Date");

        TextView dialog_Cancel_btn = (TextView) dialog.findViewById(R.id.cancel_btn);
        TextView dialog_OK_btn = (TextView) dialog.findViewById(R.id.OK_btn);
        DatePicker = (DatePicker)dialog.findViewById(R.id.datePicker);

        dialog_OK_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                UserDateOfBirth.setText(getCurrentDate());

               /* SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                String currentDate = simpleDateFormat.format(new Date());
                UserDateOfBirth.setText(currentDate);

                Calendar mcurrentdate = Calendar.getInstance();
                int mYear = mcurrentdate.get(Calendar.YEAR);
                int mMonth = mcurrentdate.get(Calendar.MONTH);
                int mDay = mcurrentdate.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dpdialog  =new DatePickerDialog(Update_ProfileFragment.this, new DatePickerDialog.OnDateSetListener(){
                    @Override
                    public void onDateSet(DatePicker view, int selectedyear, int selectedmonth, int selectedday) {
                        UserDateOfBirth.setText(new StringBuilder()
                                .append(selectedday).append("-").append(selectedmonth + 1).append("-")
                                .append(selectedyear).append(" "));

                    }
                }, mYear, mMonth, mDay);
                dpdialog.setTitle("Select Date");
                dpdialog.show();*/

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

    private String getCurrentDate(){

        StringBuilder builder =new StringBuilder();
        builder.append(DatePicker.getDayOfMonth()+"/");
        builder.append((DatePicker.getMonth() + 1)+"/");
        builder.append(DatePicker.getYear());
        return builder.toString();
    }

    String getBase64ForBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,50,stream);
        byte[] result = stream.toByteArray();
        return Base64.encodeToString(result,Base64.DEFAULT);
    }


    public void OpenGallery() {

        CropImage.activity().start(getContext(), this);

//        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
//        startActivityForResult(i,2 );

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /*if (requestCode == 0 && resultCode == RESULT_OK && requestCode == 2) {
            ImageCropFunction();
        } else if (requestCode == 2) {
            if (data != null) {
                uri = data.getData();
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                this.bitmap = bitmap;
                profilePic.setImageBitmap(this.bitmap);
//                ImageCropFunction();
            }
        } else if (requestCode == 1) {
            if (data != null) {
                Bundle bundle = data.getExtras();
                bitmap =  bundle.getParcelable("data");;
                this.bitmap = bitmap;
                profilePic.setImageBitmap(this.bitmap);
            }
        }*/

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                uri = result.getUri();
                profilePic.setImageURI(result.getUri());
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                this.bitmap = bitmap;
//                Toast.makeText(
//                        getActivity(), "Cropping successful, Sample: " + result.getSampleSize(), Toast.LENGTH_LONG)
//                        .show();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(getActivity(), "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
            }
        }
    }

//    public void ImageCropFunction() {
//
//        // Image Crop Code
//        try {
//            CropIntent = new Intent("com.android.camera.action.CROP");
//
//            CropIntent.setDataAndType(uri, "image/*");
//
//            CropIntent.putExtra("crop", "true");
//            CropIntent.putExtra("outputX", 800);
//            CropIntent.putExtra("outputY", 800);
//            CropIntent.putExtra("aspectX", 4);
//            CropIntent.putExtra("aspectY", 4);
//            CropIntent.putExtra("scaleUpIfNeeded", false);
//            CropIntent.putExtra("return-data", true);
//
//            startActivityForResult(CropIntent, 1);
//
//        } catch (ActivityNotFoundException e) {
//
//        }
//    }

    private void uploadprofile(){

        this.loader.show();

        Map<String,String> request = new HashMap<String,String>();

        request.put("user_id",UserDataCache.GetInstance().id);
        request.put("name",userFullName);
        request.put("nameO",userFullName);

        StringTokenizer tokens11 = new StringTokenizer(userContact, "+");
        String t11 = tokens11.nextToken();

        request.put("contact",t11);
        request.put("gender",userGender);
        request.put("dob",userDateOfBirth);
        request.put("country",userCountry);
        request.put("bio",userBio);
        request.put("username",userName);

        if(bitmap != null)
        {
            String encoded = getBase64ForBitmap(this.bitmap);
            request.put("profileimage",encoded);
        }

        this.requestHandler.MakeRequest(request,"update_profile");

    }

    private void SuccesssfullUpdatePopUp(){

        SettextSytle();
        // custom dialog
        final Dialog dialog = new Dialog(getContext());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.updateprofile_successfully_popup);
        dialog.setTitle("SuccessFully Update");

        TextView dialog_SuccessTitle = dialog.findViewById(R.id.Success_txt);
        TextView dialog_SuccessMsg = dialog.findViewById(R.id.Success_msg_txt);
        TextView dialog_OK_btn = dialog.findViewById(R.id.OK_BTN);

        dialog_SuccessTitle.setTypeface(myCustomFontt4);
        dialog_SuccessMsg.setTypeface(myCustomFontt2);
        dialog_OK_btn.setTypeface(myCustomFontt3);

        dialog_OK_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnManualClose();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public void OnSuccess(JSONObject response, String apiName) {
        if(apiName =="update_profile") {
            try {
                JSONObject updatedData = response.getJSONObject("user_profile");
                UserDataCache.GetInstance().SetLoginData(updatedData);
                SessionHandler.GetInstance().onSuccessfullLogin(updatedData);
//                Toast.makeText(getContext(), response.getString(KEY_MESSAGE), Toast.LENGTH_SHORT).show();
                SuccesssfullUpdatePopUp();
            }
            catch (JSONException e){}

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
//       this.loader.SetErrorColor();
        this.loader.hide();
        makeText(getActivity(), "Please Retry !!", Toast.LENGTH_SHORT).show();

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