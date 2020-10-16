package com.jassimalmunaikh.feelerz;


import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReportFragment extends Fragment implements ServerCallObserver,TopLevelFrag {

    Typeface myCustomFontt1,myCustomFontt2,myCustomFontt3,myCustomFontt4;
    RequestHandler requestHandler;
    CustomLoader loader;

    Button CancelBTN;
    TextView Title,SubTitle,SubmitBTN;
    EditText Message;
    String message;

    public ReportFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_report, container, false);

        SettextSytle();

        this.requestHandler = new RequestHandler(getContext(),this);
        this.loader = new CustomLoader(getActivity(),(ViewGroup)view);
        GlobalFragmentStack.getInstance().Register(this);

        Title = view.findViewById(R.id.title_txt);
        SubTitle = view.findViewById(R.id.subTitle_txt);
        Message = view.findViewById(R.id.report_txt);
        SubmitBTN = view.findViewById(R.id.Submit_report);
        CancelBTN = view.findViewById(R.id.CloseBTN);

        Title.setTypeface(myCustomFontt3);
        SubTitle.setTypeface(myCustomFontt3);
        Message.setTypeface(myCustomFontt3);
        SubmitBTN.setTypeface(myCustomFontt2);

        SubmitBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message = Message.getText().toString().trim();
                SendReport();
            }
        });

        CancelBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnManualClose();
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

    private void SendReport(){
        this.loader.show();
        String PostID = getArguments().getString("postid");
        Map<String,String> request = new HashMap<>();
        request.put("message",message);
        request.put("post_id",PostID);
        request.put("user_id",UserDataCache.GetInstance().id);
        this.requestHandler.MakeRequest(request,"user_manage_report");

    }

    private void showpopup(){
            // custom dialog
            final Dialog dialog = new Dialog(getContext());
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setContentView(R.layout.report_success_popup);
            dialog.setTitle("Forget Password...");

            TextView title = (TextView) dialog.findViewById(R.id.Success_txt);
            TextView subTitle = (TextView) dialog.findViewById(R.id.message_txt);
            TextView ok_btn = (TextView) dialog.findViewById(R.id.OK_BTN);
            title.setTypeface(myCustomFontt2);
            subTitle.setTypeface(myCustomFontt3);
            ok_btn.setTypeface(myCustomFontt3);

            ok_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OnManualClose();
                    dialog.dismiss();
                }
            });
            dialog.show();
        }


    @Override
    public void OnSuccess(JSONObject response, String apiName) {
        if(apiName =="user_manage_report")
        {
           showpopup();
        }
        this.loader.hide();
    }

    @Override
    public void OnFailure(String errorMessage, String apiName) {
        this.loader.SetErrorColor();
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
