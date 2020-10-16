package com.jassimalmunaikh.feelerz;

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

interface DeleteConfirmListener
{
    public void OnListItemDeletion(int position);
    public void OnDeletionInProgress();
    public void OnDeletionFailed();
}

public class DeleteConfirmDialog extends BottomSheetDialogFragment implements ServerCallObserver {

    public final static int TYPE_DELETE_COMMENT = 0;
    public final static int TYPE_DELETE_POST = 1;
    public final static int TYPE_DELETE_NOTIFICATION = 2;
    public final static int TYPE_DELETE_MESSAGE = 3;

    DeleteConfirmListener mListener = null;

    String endPoint = "";
    Map<String,String> request = new HashMap<String,String>();
    RequestHandler requestHandler;
    TextView headerMsg;
    TextView msg;
    int mPosition;
    String contentId;
    Button accept,decline;

    public void SetListener(DeleteConfirmListener listener)
    {
        this.mListener = listener;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View parent = (View)getView().getParent();
        parent.setBackgroundColor(Color.TRANSPARENT);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.confirm_action_bs_dialog_layout, container, false);


        this.requestHandler = new RequestHandler(getActivity(), this);
        this.mPosition = getArguments().getInt("position",-1);
        contentId = getArguments().getString("contentId","");
        if(mPosition == -1 || contentId.isEmpty()) {
            Toast.makeText(getContext(),"please provide data position to delete",Toast.LENGTH_SHORT).show();
            dismiss();
            return v;
        }

        accept = (Button) v.findViewById(R.id.button_accept);
        decline = (Button) v.findViewById(R.id.Cancel_BTN);

        this.headerMsg = v.findViewById(R.id.header);

        this.msg = v.findViewById(R.id.message);
        String comment = getArguments().getString("comment","");
        this.msg.setText(comment);

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if(mListener != null)
                    mListener.OnDeletionInProgress();
                requestHandler.MakeRequest(request,endPoint);
            }
        });

        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        int type = getArguments().getInt("type", -1);
        SetData(type);

        return v;
    }

    private void SetData(int type)
    {
        switch (type)
        {
            case TYPE_DELETE_COMMENT:
                headerMsg.setText("Do you want to delete this comment ?");
                accept.setText("Delete Comment");
                endPoint = "delete_comment";
                request.put("id",contentId);
                break;
            case TYPE_DELETE_POST:
                headerMsg.setText("Do you want to delete this post ?");
                accept.setText("Delete Post");
                endPoint = "post_delete";
                request.put("user_id",UserDataCache.GetInstance().id);
                request.put("post_id",contentId);
                break;
            case TYPE_DELETE_NOTIFICATION:
                headerMsg.setText("Do you want to delete this Notification ?");
                accept.setText("Delete Notification");
                endPoint = "delete_nofication";
                request.put("id",contentId);
                break;
            case TYPE_DELETE_MESSAGE:
                headerMsg.setText("Do you want to delete this message?");
                endPoint = "delete_chat";
                request.put("id",contentId);
                request.put("send_by",UserDataCache.GetInstance().id);
                break;
        }
    }

    public Window getWindow() {
        return null;
    }

    @Override
    public void OnSuccess(JSONObject response, String apiName) {
        dismiss();
        if(mListener != null)
        {
            mListener.OnListItemDeletion(mPosition);
        }
    }

    @Override
    public void OnFailure(String errorMessage, String apiName) {
        Toast.makeText(getContext(),"Failed to delete..",Toast.LENGTH_LONG).show();
        if(mListener != null)
            mListener.OnDeletionFailed();
        dismiss();
    }

    @Override
    public void OnNetworkError() {

    }

}