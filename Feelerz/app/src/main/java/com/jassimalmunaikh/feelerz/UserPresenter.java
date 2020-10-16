package com.jassimalmunaikh.feelerz;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.otaliastudios.autocomplete.RecyclerViewPresenter;
import com.squareup.picasso.Picasso;


import org.apache.commons.text.StringEscapeUtils;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class UserPresenter extends RecyclerViewPresenter<Users> {

    @SuppressWarnings("WeakerAccess")
    protected Adapter adapter;

    @SuppressWarnings("WeakerAccess")
    public UserPresenter(@NonNull Context context) {
        super(context);
    }

    @NonNull
    @Override
    protected PopupDimensions getPopupDimensions() {
        PopupDimensions dims = new PopupDimensions();
        dims.width = 600;
        dims.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        return dims;
    }

    @NonNull
    @Override
    protected RecyclerView.Adapter instantiateAdapter() {
        adapter = new Adapter();
        return adapter;
    }

    @Override
    protected void onQuery(@Nullable CharSequence query) {

        List<Users> all = CommentSection.friendList;
        if (TextUtils.isEmpty(query)) {
            adapter.setData(all);
        } else {
            query = query.toString().toLowerCase();
            List<Users> list = new ArrayList<>();
            for (Users u : all) {
                if (u.getFullname().toLowerCase().contains(query) ||
                        u.getFullname().toLowerCase().contains(query)) {
                    list.add(u);
                }
            }
            adapter.setData(list);
            Log.e("UserPresenter", "found "+list.size()+" users for query "+query);
        }
        adapter.notifyDataSetChanged();
    }

    protected class Adapter extends RecyclerView.Adapter<Adapter.Holder> {

        private List<Users> data;

        @SuppressWarnings("WeakerAccess")
        protected class Holder extends RecyclerView.ViewHolder {
            private View root;
            private TextView fullname;
            private TextView username;
            private CircleImageView imgUser;
            Holder(View itemView) {
                super(itemView);
                root = itemView;
                fullname = itemView.findViewById(R.id.Mention_UserName);
//                username = itemView.findViewById(R.id.username);
                imgUser = itemView.findViewById(R.id.Mention_Userprofile);
            }
        }

        @SuppressWarnings("WeakerAccess")
        protected void setData(@Nullable List<Users> data) {
            this.data = data;
        }

        @Override
        public int getItemCount() {
            return (isEmpty()) ? 1 : data.size();
        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new Holder(LayoutInflater.from(getContext()).inflate(R.layout.mention_list_item, parent, false));
        }

        private boolean isEmpty() {
            return data == null || data.isEmpty();
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            if (isEmpty()) {
                holder.fullname.setText("No user here!");
//                holder.username.setText("Sorry!");
                holder.root.setOnClickListener(null);
                return;
            }
            final Users user = data.get(position);
//            holder.fullname.setText(user.getFullname());
//            String userName = data.get(position).getUsername();
            final String userName = data.get(position).getUsername();
            holder.fullname.setText(Html.fromHtml(StringEscapeUtils.unescapeJava(user.getFullname())));
//            holder.username.setText("@" + Html.fromHtml(StringEscapeUtils.unescapeJava(user.getFullname())));
            String url = getContext().getResources().getString(R.string.ImageURL) + user.getImageId();
            Picasso.get().load(url).into(holder.imgUser);
            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dispatchClick(user);

                }
            });
        }
    }
}
