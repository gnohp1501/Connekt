package com.example.connekt.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.connekt.R;
import com.example.connekt.model.Notification;
import com.example.connekt.model.Post;
import com.example.connekt.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {
    private final Context mContext;
    private final List<Notification> mNotifications;

    public NotificationsAdapter(Context mContext, List<Notification> mNotifications) {
        this.mContext = mContext;
        this.mNotifications = mNotifications;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.notification_item, parent, false);

        return new NotificationsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Notification notification = mNotifications.get(position);

        getUser(holder.iv_ava, holder.tv_user_name, notification.getUser_id());
        holder.tv_title.setText(notification.getTitle());
        if (notification.isPost()) {
            holder.iv_image.setVisibility(View.VISIBLE);
            getPostImage(holder.iv_image, notification.getPost_id());
        } else {
            holder.iv_image.setVisibility(View.GONE);
        }

//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (notification.isPost()) {
//                    mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
//                            .edit().putString(Constant.POST_ID, notification.getPost_id()).apply();
//                    ((FragmentActivity)mContext).getSupportFragmentManager()
//                            .beginTransaction().replace(R.id.fragment_container, new PostDetailFragment()).commit();
//                } else {
//                    mContext.getSharedPreferences("PROFILE", Context.MODE_PRIVATE)
//                            .edit().putString("profileId", notification.getUserid()).apply();
//                    ((FragmentActivity)mContext).getSupportFragmentManager()
//                            .beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
//                }
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return mNotifications.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView iv_ava;
        public ImageView iv_image;
        public TextView tv_user_name;
        public TextView tv_title;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_ava = itemView.findViewById(R.id.iv_ava);
            iv_image = itemView.findViewById(R.id.iv_image);
            tv_user_name = itemView.findViewById(R.id.tv_user_name);
            tv_title = itemView.findViewById(R.id.tv_title);
        }
    }

    private void getPostImage(final ImageView imageView, String postId) {
        FirebaseDatabase.getInstance().getReference().child("posts").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);
                Picasso.get().load(post.getImage_url()).placeholder(R.mipmap.ic_launcher).into(imageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getUser(final ImageView imageView, final TextView textView, String userId) {
        FirebaseDatabase.getInstance().getReference().child("users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user.getImage_url().equals("default")) {
                    imageView.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Picasso.get().load(user.getImage_url()).into(imageView);
                }
                textView.setText(user.getUser_name());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}