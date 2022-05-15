package com.example.connekt.adapter;

import static com.example.connekt.utils.DateUtils.getTimeMess;

import android.content.Context;
import android.content.Intent;
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
import com.example.connekt.model.User;
import com.example.connekt.view.activity.MessActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserChatAdapter extends RecyclerView.Adapter<UserChatAdapter.ViewHolder> {
    private final Context mContext;
    private final List<User> mUsers;
    private final boolean isChat;

    String theLastMess;
    String timeTheLastMess;
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    public UserChatAdapter(Context mContext, List<User> mUsers, boolean isChat) {
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.isChat = isChat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item_chat, parent, false);

        return new UserChatAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = mUsers.get(position);
        holder.username.setText(user.getUser_name());
        if (user.getImage_url().equals(Constant.DEFAULT)) {
            holder.imageView.setImageResource(R.mipmap.ic_launcher);
        } else {
            Picasso.get().load(user.getImage_url()).into(holder.imageView);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, MessActivity.class);
                intent.putExtra(Constant.USER_ID, user.getId());
                mContext.startActivity(intent);
            }
        });
        if (isChat) {
            if (user.getStatus().equals(Constant.ONLINE)) {
                holder.status_on.setVisibility(View.VISIBLE);
                holder.status_off.setVisibility(View.GONE);
            } else {
                holder.status_on.setVisibility(View.GONE);
                holder.status_off.setVisibility(View.VISIBLE);
            }
        } else {
            holder.status_on.setVisibility(View.GONE);
            holder.status_off.setVisibility(View.GONE);
        }
        if (isChat) {
            lastMess(user.getId(), holder.last_mess, holder.time);
        } else {
            holder.last_mess.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public void lastMess(String userid, TextView last_mess, TextView time) {
        theLastMess = "default";
        timeTheLastMess = "0";
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.CHATS);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Chat chat = snap.getValue(Chat.class);
                    if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid)
                            || chat.getReceiver().equals(userid) && chat.getSender().equals(firebaseUser.getUid())) {
                        theLastMess = chat.getMess();
                        timeTheLastMess = chat.getTime();
                    }
                }
                switch (theLastMess) {
                    case "default":
                        last_mess.setText("No message !");
                        time.setText("");
                        break;
                    default:
                        last_mess.setText(theLastMess);
                        time.setText(getTimeMess(Long.parseLong(timeTheLastMess)));
                        break;
                }
                theLastMess = "default";
                timeTheLastMess = "0";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView username;
        public ImageView imageView;
        public CircleImageView status_on;
        public CircleImageView status_off;
        public TextView last_mess;
        public TextView time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.tv_user_name);
            imageView = itemView.findViewById(R.id.iv_ava);
            status_on = itemView.findViewById(R.id.status_on);
            status_off = itemView.findViewById(R.id.status_off);
            last_mess = itemView.findViewById(R.id.tv_last_mess);
            time = itemView.findViewById(R.id.tv_time_last);
        }
    }
}
