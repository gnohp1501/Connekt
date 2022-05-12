package com.example.connekt.adapter;

import static com.example.connekt.utils.DateUtils.getTimeAgo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.connekt.R;
import com.example.connekt.constant.Constant;
import com.example.connekt.model.Chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    private final Context mContext;
    private final List<Chat> mChats;
    private final String imageUrl;
    FirebaseUser fuser;
    private Boolean isSeen = false;
    private int viewTypeSeen;


    public MessageAdapter(Context mContext, List<Chat> mChats, String imageUrl) {
        this.mContext = mContext;
        this.mChats = mChats;
        this.imageUrl = imageUrl;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            viewTypeSeen = MSG_TYPE_RIGHT;
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
            return new ViewHolder(view);
        } else {
            viewTypeSeen = MSG_TYPE_LEFT;
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
            return new ViewHolder(view);
        }

    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chat chat = mChats.get(position);
        holder.text_send.setText(chat.getMess());
        if (imageUrl.equals(Constant.DEFAULT)) {
            holder.imageView.setImageResource(R.mipmap.ic_launcher);
        } else {
            Picasso.get().load(imageUrl).into(holder.imageView);
        }
        holder.text_time.setText(getTimeAgo(Long.parseLong(chat.getTime())));
        if (position == mChats.size() - 1) {
            if (chat.isSeen()) {
                holder.isSeen.setText(mContext.getString(R.string.seenLabel));
            } else {
                holder.isSeen.setText(mContext.getString(R.string.deliveriedLabel));
            }
        } else {
            holder.isSeen.setVisibility(View.GONE);
        }
        holder.text_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isSeen = !isSeen;
                if (isSeen) {
                    holder.isSeen.setVisibility(View.VISIBLE);
                    holder.text_time.setVisibility(View.VISIBLE);
                } else {
                    holder.isSeen.setVisibility(View.GONE);
                    holder.text_time.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mChats.size();
    }

    @Override
    public int getItemViewType(int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if (mChats.get(position).getSender().equals(fuser.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView text_send;
        public ImageView imageView;
        public TextView text_time;
        public TextView isSeen;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_ava);
            text_send = itemView.findViewById(R.id.text_send);
            text_time = itemView.findViewById(R.id.text_time);
            isSeen = itemView.findViewById(R.id.text_seen);
        }
    }
}
