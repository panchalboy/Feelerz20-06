package com.jassimalmunaikh.feelerz;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 */
public class ImagePreview extends Fragment implements TopLevelFrag{

    Button cancel_btn;
    Button Share_BTN;
    Bitmap downloadedImage;
    String imageURL;
    Boolean forProfilePicture;
    public ImagePreview() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_image_preview2, container, false);

        GlobalFragmentStack.getInstance().Register(this);

        final SubsamplingScaleImageView scalableView = (SubsamplingScaleImageView)view.findViewById(R.id.imageView);

        forProfilePicture = getArguments().getBoolean("forProfilePicture",false);
        final String prefix = getArguments().getString("postImageId");
        String url = getResources().getString(R.string.PostURL);
        if(forProfilePicture)
            url = getResources().getString(R.string.ImageURL);

        this.imageURL = url + prefix;

        Share_BTN = view.findViewById(R.id.Share_BTN);
        if(forProfilePicture)
            Share_BTN.setVisibility(View.INVISIBLE);
        cancel_btn = view.findViewById(R.id.cancel_btn);

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnManualClose();
            }
        });


        Picasso.get().load(this.imageURL).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                scalableView.setImage(ImageSource.bitmap(bitmap));
                downloadedImage = bitmap;
                if(forProfilePicture)
                    return;
                Share_BTN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bitmap icon = ImagePreview.this.downloadedImage;
                        Intent share = new Intent(Intent.ACTION_SEND);
                        share.setType("image/jpeg");
                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                        icon.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                        File f = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)+ File.separator + "temporary_file.jpg");
                        try {
                            f.createNewFile();
                            FileOutputStream fo = new FileOutputStream(f);
                            fo.write(bytes.toByteArray());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        share.putExtra(Intent.EXTRA_STREAM, Uri.parse(f.getAbsolutePath()));
                        startActivity(Intent.createChooser(share, "Share Image"));
                    }
                });
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });

        return view;
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
