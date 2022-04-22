package com.example.connekt.adapter;

import static com.example.connekt.utils.DateUtils.getTimeAgo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.connekt.R;
import com.example.connekt.constant.Constant;
import com.example.connekt.model.Post;
import com.example.connekt.model.User;
import com.example.connekt.view.fragment.PersonFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private Context mContext;
    private List<Post> mPosts;

    private FirebaseUser firebaseUser;

    public PostAdapter(Context mContext, List<Post> mPosts) {
        this.mContext = mContext;
        this.mPosts = mPosts;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_item, parent, false);
        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = mPosts.get(position);
        Picasso.get().load(post.getImage_url()).into(holder.iv_image);
        holder.tv_description.setText(post.getDescription());
        holder.tv_time_created.setText(post.getTime_created());
        holder.tv_time_created.setText(getTimeAgo(Long.parseLong(post.getTime_created())));

        FirebaseDatabase.getInstance().getReference().child("users").child(post.getPublisher()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                if (user.getImage_url().equals("default")) {
                    holder.iv_ava.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Picasso.get().load(user.getImage_url()).placeholder(R.mipmap.ic_launcher).into(holder.iv_ava);
                }
                holder.tv_user_name.setText(user.getUser_name());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        holder.iv_heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.iv_heart.getTag().equals("like")) {
                    FirebaseDatabase.getInstance().getReference().child("likes")
                            .child(post.getPost_id()).child(firebaseUser.getUid()).setValue(true);
                    addNotification(post.getPost_id(), post.getPublisher());
                } else {
                    FirebaseDatabase.getInstance().getReference().child("likes")
                            .child(post.getPost_id()).child(firebaseUser.getUid()).removeValue();
                }
            }
        });

//        holder.tv_comment.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(mContext, CommentActivity.class);
//                intent.putExtra("postId", post.getPost_id());
//                intent.putExtra("authorId", post.getPublisher());
//                mContext.startActivity(intent);
//            }
//        });

//        holder.noOfComments.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(mContext, CommentActivity.class);
//                intent.putExtra("postId", post.getPostid());
//                intent.putExtra("authorId", post.getPublisher());
//                mContext.startActivity(intent);
//            }
//        });

        holder.iv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.iv_save.getTag().equals("save")) {
                    FirebaseDatabase.getInstance().getReference().child("saves")
                            .child(firebaseUser.getUid()).child(post.getPost_id()).setValue(true);
                } else {
                    FirebaseDatabase.getInstance().getReference().child("saves")
                            .child(firebaseUser.getUid()).child(post.getPost_id()).removeValue();
                }
            }
        });

        holder.iv_ava.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.getSharedPreferences("PROFILE", Context.MODE_PRIVATE)
                        .edit().putString("profileId", post.getPublisher()).apply();

                ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new PersonFragment()).commit();
            }
        });

        holder.tv_user_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.getSharedPreferences("PROFILE", Context.MODE_PRIVATE)
                        .edit().putString("profileId", post.getPublisher()).apply();

                ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new PersonFragment()).commit();
            }
        });

        isLiked(post.getPost_id(), holder.iv_heart);
        noOfLikes(post.getPost_id(), holder.tv_heart);
        getComments(post.getPost_id(), holder.tv_comment);
        isSaved(post.getPost_id(), holder.iv_save);

//        holder.postImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit().putString("postid", post.getPostid()).apply();
//
//                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.fragment_container, new PostDetailFragment()).commit();
//            }
//        });

//        holder.tv_heart.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(mContext, FollowersActivity.class);
//                intent.putExtra("id", post.getPublisher());
//                intent.putExtra("title", "likes");
//                mContext.startActivity(intent);
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final ImageView iv_ava;
        public final ImageView iv_image;
        public final ImageView iv_heart;
        public final ImageView iv_comment;
        public final ImageView iv_save;

        public final TextView tv_user_name;
        public final TextView tv_time_created;
        public final TextView tv_privacy;
        public final TextView tv_description;
        public final TextView tv_heart;
        public final TextView tv_comment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            iv_ava = itemView.findViewById(R.id.iv_ava);
            iv_image = itemView.findViewById(R.id.iv_image);
            iv_heart = itemView.findViewById(R.id.iv_heart);
            iv_comment = itemView.findViewById(R.id.iv_comment);
            iv_save = itemView.findViewById(R.id.iv_save);

            tv_user_name = itemView.findViewById(R.id.tv_user_name);
            tv_time_created = itemView.findViewById(R.id.tv_time_created);
            tv_privacy = itemView.findViewById(R.id.tv_privacy);
            tv_description = itemView.findViewById(R.id.tv_description);
            tv_heart = itemView.findViewById(R.id.tv_heart);
            tv_comment = itemView.findViewById(R.id.tv_comment);
        }
    }

    private void isSaved (final String postId, final ImageView image) {
        FirebaseDatabase.getInstance().getReference().child("saves").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(postId).exists()) {
                    image.setImageResource(R.drawable.ic_icons8_delete_bookmark);
                    image.setTag("saved");
                } else {
                    image.setImageResource(R.drawable.ic_icons8_add_bookmark);
                    image.setTag("save");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void isLiked(String postId, final ImageView imageView) {
        FirebaseDatabase.getInstance().getReference().child("likes").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(firebaseUser.getUid()).exists()) {
                    imageView.setImageResource(R.drawable.icon_heart_ed);
                    imageView.setTag("liked");
                } else {
                    imageView.setImageResource(R.drawable.icon_heart);
                    imageView.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void noOfLikes (String postId, final TextView text) {
        FirebaseDatabase.getInstance().getReference().child("likes").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                text.setText(dataSnapshot.getChildrenCount()+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getComments (String postId, final TextView text) {
        FirebaseDatabase.getInstance().getReference().child("comments").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                text.setText(dataSnapshot.getChildrenCount()+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addNotification(String postId,String publisherId) {
        HashMap<String, Object> map = new HashMap<>();

        map.put(Constant.USER_ID, firebaseUser.getUid());
        map.put(Constant.TITLE, "liked your post.");
        map.put(Constant.POST_ID, postId);
        map.put(Constant.IS_POST, true);

        FirebaseDatabase.getInstance().getReference().child("notifications").child(publisherId).push().setValue(map);
    }
}
