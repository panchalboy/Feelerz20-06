package com.jassimalmunaikh.feelerz;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;

import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import androidx.fragment.app.Fragment;
import androidx.core.content.FileProvider;
import de.hdodenhof.circleimageview.CircleImageView;

import android.text.Html;
import android.util.Base64;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static com.jassimalmunaikh.feelerz.R.layout.spinner_item4;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreatePostFragment extends Fragment implements ServerCallObserver , TopLevelFrag , AdapterView.OnItemSelectedListener{
    private static final String KEY_MESSAGE = "message";
    Intent CropIntent;
    Uri uri;
    private int PICK_IMAGE_REQUEST = 1;
    private int REQUEST_IMAGE_CAPTURE = 2;
    Uri outPutfileUri;
    Typeface myCustomFontt1,myCustomFontt2,myCustomFontt3,myCustomFontt4;
    RequestHandler requestHandler;

    View mainView;
    Spinner spinner,Spinner2;
    CircleImageView UserProfilePic;
    Button Back_Btn;
    String privacy;
    String feelingName = "";
    String feelingParent = "";
    int feelingColor;
    String feelingId = "";
    EditText postText;
    Button postButton;

    Button galleryButton;
    Button cameraButton;
    ViewGroup.LayoutParams params;
    CustomLoader loader;

    Bitmap bitmap;

    private File photoFile = null;
    String currentPhotoPath ;
    String SelectedFeeling;

    public CreatePostFragment() {
        // Required empty public constructor

        this.bitmap = null;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.requestHandler = new RequestHandler(getContext(),this);
        // Inflate the layout for this fragment.
        View view = inflater.inflate(R.layout.fragment_feel, container, false);
        spinner = (Spinner)view.findViewById(R.id.spinner);
        SettextSytle();
        // Spinner click listener
        spinner.setOnItemSelectedListener(this);

        GlobalCache.GetInstance();
        // Spinner Drop down elements
        List categories = new ArrayList();
        categories.add("Friend");
        categories.add("Public");
        categories.add("Anonymous");

        // Creating adapter for spinner
        ArrayAdapter dataAdapter = new ArrayAdapter(getContext(), R.layout.spinner_item22, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(R.layout.spinner_item2);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);

        //some spinner initialisation stuff->
        spinner.getBackground().setColorFilter(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        Spinner2 = (Spinner)view.findViewById(R.id.feeling_text);

        ArrayList<String> FeelingString = GlobalCache.GetInstance().getFeelingString();
//        SelectedFeeling = this.feelingName;
        this.feelingName = SelectedFeeling;
//        Spinner2.setSelection(FeelingString.indexOf(SelectedFeeling));
//        spinner.setSelection(SelectedFeeling);
//        Spinner2.setSelection(((ArrayAdapter<String>)Spinner2.getAdapter()).getPosition(SelectedFeeling));
        Spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // On selecting a spinner item

                String item = parent.getItemAtPosition(position).toString();
//                txtView.setText("    " + StringEscapeUtils.unescapeJava(this.feelingName) + "   ");

                // Showing selected spinner item
//                Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();

                /*TextView spinner_text=(TextView) view;
                if (SelectedFeeling != item){
//                Toast.makeText(this,"your grade is " + spinner_text.getText(),Toast.LENGTH_SHORT).show();
                    spinner_text.setText(SelectedFeeling);//this line should do its work but didnt goes well
                }else {
                    spinner_text.setText(item);
                }*/

            }



            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

//            Spinner2.setSelection(getIndex(mySpinner, myValue));
            //private method of your class
//            private int getIndex(Spinner spinner, String myString){
//                for (int i=0;i<spinner.getCount();i++){
//                    if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
//                        return i;
//                    }
//                }
//                return 0;
//            }
        });


        ArrayAdapter dataAdapter2 = new ArrayAdapter(getContext(), R.layout.spinner_item3, FeelingString);

        // Drop down layout style - list view with radio button
        dataAdapter2.setDropDownViewResource(spinner_item4);

        Spinner2.setEnabled(true);
        Spinner2.setClickable(false);
        // attaching data adapter to spinner
        Spinner2.setAdapter(dataAdapter2);

//        Spinner2.setSelection(dataAdapter2.getPosition(String.format(Locale.getDefault(), "%c", SelectedFeeling)));
        /*int spinnerPosition = 0;
        String strpos1 = FeelingString.getString(SelectedFeeling, "");
        if (strpos1 != null || !strpos1.equals(null) || !strpos1.equals("")) {
            strpos1 = prfs.getString("SPINNER1_VALUE", "");
            spinnerPosition = dataAdapter2.getPosition(strpos1);
            Spinner2.setSelection(spinnerPosition);
            spinnerPosition = 0;
        }*/


        //some spinner initialisation stuff->
        Spinner2.getBackground().setColorFilter(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        this.loader = new CustomLoader(getActivity(),(ViewGroup)view);
        this.mainView = view;

        GlobalFragmentStack.getInstance().Register(this);


        String imageId = UserDataCache.GetInstance().imageId;
        if(!imageId.isEmpty()) {
            try {
                UserProfilePic = mainView.findViewById(R.id.profile_pic);
                String url = getResources().getString(R.string.ImageURL) + imageId;
                Picasso.get().load(url).into(UserProfilePic);
            }
            catch (Exception e){}
        }

        TextView TitlePage = view.findViewById(R.id.CreatePostTitle_page);
        TitlePage.setTypeface(myCustomFontt4);

        TextView name = view.findViewById(R.id.Post_User_Name);
        name.setText(Html.fromHtml(StringEscapeUtils.unescapeJava(UserDataCache.GetInstance().name)));
     //   name.setText(UserDataCache.GetInstance().name);
        name.setTypeface(myCustomFontt2);

        Back_Btn = view.findViewById(R.id.Back_Btn);
        Back_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnManualClose();
            }
        });

        this.feelingColor = getArguments().getInt("feelingColor");
        this.feelingName = getArguments().getString("feelingName");
        if(this.feelingName!=null) {
            int spinnerPosition = dataAdapter2.getPosition(StringEscapeUtils.unescapeJava(feelingName));
            Spinner2.setSelection(spinnerPosition);
        }
        this.feelingParent = getArguments().getString("feelingParent");
        this.feelingId = getArguments().getString("feelingId");

        SetupFeeling(view);

        this.postText = (EditText)view.findViewById(R.id.post_text);
        postText.setTypeface(myCustomFontt3);

        this.postButton = (Button)view.findViewById(R.id.post_button);
        this.postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loader.show();
                mainView.invalidate();
                Post();
                removePhoneKeypad();
            }
        });

        galleryButton = (Button)view.findViewById(R.id.gallery);
        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });

        cameraButton = (Button)view.findViewById(R.id.camera);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CaptureImage();
            }
        });
        return view;
    }

    public void removePhoneKeypad() {
        InputMethodManager inputManager = (InputMethodManager) mainView
                .getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        IBinder binder = mainView.getWindowToken();
        inputManager.hideSoftInputFromWindow(binder,
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void SettextSytle(){

        myCustomFontt1 = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Klavika-Regular.ttf");
        myCustomFontt2 = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Nunito-Bold.ttf");
        myCustomFontt3 = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Nunito-Regular.ttf");
        myCustomFontt4 = Typeface.createFromAsset(getActivity().getAssets(),"fonts/nunito-semibold.ttf");
    }

    public void OpenGallery()
    {

//        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
//        startActivityForResult(i, PICK_IMAGE_REQUEST);
        CropImage.activity().start(getContext(), this);
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
    void CaptureImage()
    {

        CropImage.activity().start(getContext(), this);

        /*Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {

                Uri photoURI = null;
                try {
                    photoURI = FileProvider.getUriForFile(getActivity(),
                            "com.jassimalmunaikh.feelerz.fileprovider",
                            photoFile);
                }
                catch (Exception e)
                {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }*/
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ImageView imageView = mainView.findViewById(R.id.post_image);


        /*if (requestCode == PICK_IMAGE_REQUEST &&
        resultCode == RESULT_OK && data != null && data.getData() != null) {
            try {
                Uri uri = data.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                this.bitmap = bitmap;
                AdjustImageView(imageView, this.bitmap);



            } catch (IOException e) {
                e.printStackTrace();
            }*/

            /*if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
                ImageCropFunction();
            } else if (requestCode == 2) {
                if (data != null) {
                    uri = data.getData();
                    uri = data.getData();
                    ImageCropFunction();
                }
            } else if (requestCode == 1) {
                if (data.getData() != null) {
                    Bundle bundle = data.getExtras();
                    bitmap =  bundle.getParcelable("data");;
                    this.bitmap = bitmap;
                    AdjustImageView(imageView,this.bitmap);
                    */
        /*profilePic.setImageBitmap(this.bitmap);*//*
                }
            }*/

        /*}*/
        /*try {
            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                Bitmap imageBitmap =  BitmapFactory.decodeFile(currentPhotoPath);
                imageBitmap = CorrectBitmap(imageBitmap);
                this.bitmap = imageBitmap;
                AdjustImageView(imageView, imageBitmap);
            }
        }
        catch (Exception e)
        {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }*/

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                uri = result.getUri();
                imageView.setImageURI(result.getUri());
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

    /*public void ImageCropFunction() {

        // Image Crop Code
        try {
            CropIntent = new Intent("com.android.camera.action.CROP");

            CropIntent.setDataAndType(uri, "image/*");

            CropIntent.putExtra("crop", "true");
            CropIntent.putExtra("outputX", 360);
            CropIntent.putExtra("outputY", 310);
            CropIntent.putExtra("aspectX", 4);
            CropIntent.putExtra("aspectY", 4);
            CropIntent.putExtra("scaleUpIfNeeded", true);
            CropIntent.putExtra("return-data", true);

            startActivityForResult(CropIntent, 1);

        } catch (ActivityNotFoundException e) {

        }
    }*/

    public Bitmap CorrectBitmap(Bitmap bitmap)
    {
        Bitmap rotatedBitmap = null;

        try {
            ExifInterface ei = new ExifInterface(currentPhotoPath);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);


            switch (orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitmap = rotateImage(bitmap, 90);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitmap = rotateImage(bitmap, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitmap = rotateImage(bitmap, 270);
                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    rotatedBitmap = bitmap;
            }
        }
        catch(Exception e){}

        return rotatedBitmap;
    }

    public Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    public void AdjustImageView(ImageView iv,Bitmap bitmap)
    {
        Display d = getActivity().getWindowManager().getDefaultDisplay();
        Point p = new Point();
        d.getSize(p);

        iv.setImageBitmap(bitmap);
        iv.setAdjustViewBounds(true);

        if(bitmap.getWidth() >= bitmap.getHeight())
        {
            if(bitmap.getWidth() > p.x + 60)
                iv.getLayoutParams().width = p.x - 60;
        }
        if(bitmap.getHeight() > bitmap.getWidth())
        {
            if(bitmap.getWidth() > p.y / 2)
                iv.getLayoutParams().height = (p.y / 2) - 60;
        }
    }
    public void SetupFeeling(View v)
    {
        View feelingShape = v.findViewById(R.id.feeling_shape);
        feelingShape.getBackground().setTint(this.feelingColor);


//        TextView txtView = v.findViewById(spinner_item4);
//        txtView.setText("    " + StringEscapeUtils.unescapeJava(this.feelingName) + "   ");

        /*Spinner Spinner2 = (Spinner)v.findViewById(R.id.feeling_text);

        List categories2 = new ArrayList();
        categories2.add("Love");
        categories2.add("Satisfied");
        categories2.add("sjdh");
        categories2.add("x54cd");
        categories2.add("5szd45");
        categories2.add("jsud456");

        Spinner2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // On selecting a spinner item
                String item = parent.getItemAtPosition(position).toString();

                // Showing selected spinner item
//                Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
            }
        });

        ArrayAdapter dataAdapter = new ArrayAdapter(getContext(),R.layout.spinner_item3, categories2);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(R.layout.spinner_item4);

        Spinner2.setEnabled(true);
        Spinner2.setClickable(false);
        // attaching data adapter to spinner
        Spinner2.setAdapter(dataAdapter);


        //some spinner initialisation stuff->
        Spinner2.getBackground().setColorFilter(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);*/

    }

    /*
          $user_id=@$data->user_id;
          $feeling=@$data->feeling;
          $feeling_id=@$data->feeling_id;
          $feeling_color=@$data->feeling_color;
          $post_text=@$data->post_text;
          $privacy=@$data->privacy;
          $image=@$data->image;
          $image_type=@$data->image_type;
          $post_textO=@$data->post_textO;
     */

    public void Post()
    {
        Map<String,String> request = new HashMap<String,String>();
        request.put("user_id",UserDataCache.GetInstance().id);
        request.put("feeling_id",this.feelingId);
        String hexColor = String.format("#%06X",(0xFFFFFF & this.feelingColor));
        request.put("feeling_color",hexColor);

        String feelingEncoded = "";
        String utfEncoded = "";

        feelingEncoded = encodeToUTF8(this.feelingName);
        utfEncoded = encodeToUTF8(this.postText.getText().toString());

        request.put("feeling",feelingEncoded);
        request.put("post_text",utfEncoded);
        request.put("privacy",this.privacy);

        String type = "1";
        String encodedBitmap = "";
        if(this.bitmap != null) {
            type = "2";
            encodedBitmap = getBase64ForBitmap(this.bitmap);
        }
        request.put("image_type",type);
        request.put("image",encodedBitmap);
        request.put("tag_friend","");
        request.put("post_textO",utfEncoded);
        this.requestHandler.MakeRequest(request,"post_comment");
    }

    String encodeToUTF8(String mainString)
    {
        return StringEscapeUtils.escapeJava(mainString);
    }

    String getBase64ForBitmap(Bitmap bitmap)
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,50,stream);
        byte[] result = stream.toByteArray();
        return Base64.encodeToString(result,Base64.DEFAULT);
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
        this.loader.hide();
//        try {
//            Toast.makeText(getContext(), response.getString(KEY_MESSAGE), Toast.LENGTH_SHORT).show();
        ((DashboardActivity)getActivity()).OpenTabFragment(ETab.Home);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        ((DashboardActivity)getActivity()).OpenTabFragment(ETab.Home);

    }

    @Override
    public void OnFailure(String errorMessage, String apiName) {
        this.loader.hide();
//        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
//        ((DashboardActivity)getActivity()).OpenTabFragment(ETab.Home);

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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        privacy = parent.getItemAtPosition(position).toString();

        // Showing selected spinner item
//        Toast.makeText(parent.getContext(), "Selected: " + privacy, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
