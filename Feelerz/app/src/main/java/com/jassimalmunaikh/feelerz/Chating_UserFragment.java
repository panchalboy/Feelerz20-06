package com.jassimalmunaikh.feelerz;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.fragment.app.FragmentManager;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class Chating_UserFragment extends Fragment implements TopLevelFrag , ServerCallObserver {

    ChatListAdapter mAdapter;
    ArrayList<ChatData> mDataList;
    RequestHandler requestHandler = null;
    CustomLoader loader;
    Button Back_btn;
    String chatMateId = "";
    View mainView;
    EditText messageEditText;
    View feelingShape;
    TextView feelingText;
    Boolean AlreadyOpen = true;
    CircleImageView profilePic;
    Boolean lastConversationLoaded = false;
    String name;
    public Chating_UserFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chating__user, container, false);
        this.mainView = view;

        GlobalFragmentStack.getInstance().Register(this);

        this.requestHandler = new RequestHandler(getContext(),this);
        this.loader = new CustomLoader(getActivity(),(ViewGroup)view);

        Bundle args = getArguments();
        this.chatMateId = args.getString("userId");

        SetFriendProfile(args,view);
        Back_btn = view.findViewById(R.id.Back_btn);
        Back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnManualClose();
            }
        });

        ListView listView = view.findViewById(R.id.chat_list);
        mDataList = new ArrayList<ChatData>();
        mAdapter = new ChatListAdapter(getContext(),mDataList,getFragmentManager());
        mAdapter.CreateLoader(this,view);
        listView.setAdapter(mAdapter);
        profilePic = view.findViewById(R.id.ChatUser_profilePic);

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle data = new Bundle();
                data.putString("userId",chatMateId);
                OtherProfileFragment fragment = new OtherProfileFragment();
                fragment.setArguments(data);
                getFragmentManager().beginTransaction().add(R.id.Main_Layout, fragment).addToBackStack(null).commit();
            }
        });
        this.feelingShape = view.findViewById(R.id.feeling_shape);
        this.feelingText = view.findViewById(R.id.feeling_text);
        this.feelingShape.setVisibility(View.INVISIBLE);
        this.feelingText.setVisibility(View.INVISIBLE);

        this.messageEditText = view.findViewById(R.id.chat_box_field);

        ImageButton msgSendButton = view.findViewById(R.id.msg_send_btn);
        msgSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!lastConversationLoaded) return;

                String message = messageEditText.getText().toString();
                message = Html.fromHtml(StringEscapeUtils.escapeJava(message)).toString();

                if(message.isEmpty())
                    return;

                messageEditText.setText("");

                SendMessage(message);

                ChatData newData = new ChatData();
                JSONObject jsonData = new JSONObject();
                try {
                    jsonData.put("message",message);
                    jsonData.put("send_by",UserDataCache.GetInstance().id);
                    jsonData.put("send_to",chatMateId);
                    jsonData.put("date_time",Utilities.getInstance().GetFromattedDate());
                    jsonData.put("status","1");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                newData.SetInfo(jsonData,false);
                mDataList.add(newData);
                mAdapter.notifyDataSetChanged();
            }
        });

        FillChatbox();

        return view;
    }

    private void SendMessage(String message)
    {
        /*
        $user_id =@$data->send_by;
         $send_by=@$data->send_by;
         $send_to=@$data->send_to;
             $chat_message=@$data->message;

         */
        Map<String,String> request = new HashMap<String,String>();
        request.put("send_by",UserDataCache.GetInstance().id);
        request.put("send_to",chatMateId);
        request.put("message",message);
        this.requestHandler.MakeRequest(request,"chat_message");
    }

    void FillChatbox()
    {
        Map<String,String> request = new HashMap<String,String>();
        request.put("user_id",UserDataCache.GetInstance().id);
        request.put("friend_id",chatMateId);
        request.put("page_no","0");
        this.requestHandler.MakeRequest(request,"get_chat_message");
    }

    void SetFriendProfile(Bundle args, View view)
    {
        TextView userName = view.findViewById(R.id.userName);
        name = args.getString("name","null");
        userName.setText(StringEscapeUtils.unescapeJava(name));

        String imageId = args.getString("imageId","");
        if(!imageId.isEmpty())
        {
            profilePic = view.findViewById(R.id.ChatUser_profilePic);
            String url = getResources().getString(R.string.ImageURL) + imageId;
            Picasso.get().load(url).into(profilePic);
        }
    }

    @Override
    public void OnManualClose() {
        GlobalFragmentStack.getInstance().Unregister(Chating_UserFragment.this);
        Close();
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
    public void Close() {
        getFragmentManager().beginTransaction().remove(Chating_UserFragment.this).commit();
    }

    @Override
    public void OnSuccess(JSONObject response, String apiName) {
        if(apiName.equals("get_chat_message"))
        {
            mDataList.clear();
            try {
                JSONArray chats = response.getJSONArray("data");
                for(int i = 0; i < chats.length() ; i++)
                {
                    ChatData newData = new ChatData();
                    JSONObject chat = chats.getJSONObject(i);
                    newData.SetInfo(chat,true);
                    mDataList.add(newData);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            mAdapter.notifyDataSetChanged();
            SendDeliveryConfirmation();
            lastConversationLoaded = true;

            try {
                String friend_last_feeling = response.getString("friend_last_feeling");
                String color = response.getString("feeling_color");
                if (color.equals("null")){
                    this.feelingShape.getBackground().setTint(Color.parseColor("#ffffff"));
                }else{
                    this.feelingShape.getBackground().setTint(Color.parseColor(color));
                }
//                Html.fromHtml(StringEscapeUtils.unescapeJava(currentUser2.getName1()))
                this.feelingText.setText("   " + Html.fromHtml(StringEscapeUtils.unescapeJava(friend_last_feeling)) + "   ");
                this.feelingText.setVisibility(View.VISIBLE);
                this.feelingShape.setVisibility(View.VISIBLE);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        if(apiName.equals("chat_message"))
        {
            JSONObject data = null;
            try {
                data = response.getJSONObject("data");
                String newId = data.getString("id");
                int recentMessageIndex = mDataList.size() - 1;
                mDataList.get(recentMessageIndex).SetId(newId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
          mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void OnFailure(String errorMessage, String apiName) {

    }

    @Override
    public void OnNetworkError() {
        noInternetPopUp();
        loader.hide();
    }

    void SendDeliveryConfirmation()
    {
        Map<String,String> request = new HashMap<String,String>();
        request.put("user_id",UserDataCache.GetInstance().id);
        request.put("status","2");
        this.requestHandler.MakeRequest(request,"message_status_delivred");
    }
}

