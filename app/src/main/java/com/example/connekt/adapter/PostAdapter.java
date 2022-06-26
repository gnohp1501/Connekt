package com.example.connekt.adapter;

import static com.example.connekt.utils.DateUtils.formatNumber;
import static com.example.connekt.utils.DateUtils.getTimeAgo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
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
import com.example.connekt.view.activity.AddActivity;
import com.example.connekt.view.activity.CommentActivity;
import com.example.connekt.view.activity.ListUserActivity;
import com.example.connekt.view.fragment.PersonFragment;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private final Context mContext;
    private final List<Post> mPosts;
    private final FirebaseUser firebaseUser;
    BottomSheetDialog bottomSheetDialog;

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
        holder.iv_image.requestLayout();
        checkAuthor(post.getPublisher(), holder.iv_more);
        holder.iv_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog = new BottomSheetDialog(mContext, R.style.BottomSheetTheme);

                View bsView = LayoutInflater.from(mContext).inflate(R.layout.bottom_sheet_post,
                        view.findViewById(R.id.bottom_sheet));
                bsView.findViewById(R.id.bs_edit).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mContext, AddActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString(Constant.POST_ID, post.getPost_id());
                        bundle.putBoolean(Constant.EDIT, true);
                        intent.putExtras(bundle);
                        mContext.startActivity(intent);
                    }
                });
                bsView.findViewById(R.id.bs_delete).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomSheetDialog.hide();
                        LayoutInflater li = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
                        View mView = li.inflate(R.layout.dialog_post, null);
                        mBuilder.setView(mView);
                        AlertDialog dialog = mBuilder.create();
                        TextView mLeft = mView.findViewById(R.id.tv_left);
                        TextView mRight = mView.findViewById(R.id.tv_right);
                        ImageView image = mView.findViewById(R.id.iv_image);
                        Picasso.get().load(post.getImage_url()).into(image);
                        mLeft.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
//                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
//                                Query query = ref.child(Constant.POSTS).child(post.getPost_id());

                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Constant.POSTS).child(post.getPost_id());
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put(Constant.STATUS, Constant.DELETED);
                                databaseReference.updateChildren(hashMap);
//                                query.addListenerForSingleValueEvent(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(DataSnapshot dataSnapshot) {
//                                        for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
//                                            appleSnapshot.getRef().child(Constant.STATUS).setValue("deleted");
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onCancelled(DatabaseError databaseError) {
//                                        Log.e("1212", "onCancelled", databaseError.toException());
//                                    }
//                                });
                                dialog.hide();
                            }
                        });
                        mRight.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.hide();
                            }
                        });
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.show();
                    }
                });
                bottomSheetDialog.setContentView(bsView);
                bottomSheetDialog.show();
            }
        });

        FirebaseDatabase.getInstance().getReference().child(Constant.USERS).child(post.getPublisher()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                if (user.getImage_url().equals(Constant.DEFAULT)) {
                    holder.iv_ava.setImageResource(R.drawable.personicon);
                } else {
                    Picasso.get().load(user.getImage_url()).placeholder(R.mipmap.ic_launcher).into(holder.iv_ava);
                }
                holder.tv_user_name.setText(user.getUser_name());
                holder.tv_user_name2.setText(user.getUser_name());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        holder.iv_heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.iv_heart.getTag().equals(Constant.LIKE)) {
                    FirebaseDatabase.getInstance().getReference().child(Constant.LIKES)
                            .child(post.getPost_id()).child(firebaseUser.getUid()).setValue(true);
                    if (!post.getPublisher().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        addNotification(post.getPost_id(), post.getPublisher());
                    }
                } else {
                    FirebaseDatabase.getInstance().getReference().child(Constant.LIKES)
                            .child(post.getPost_id()).child(firebaseUser.getUid()).removeValue();
                }
            }
        });

        holder.iv_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CommentActivity.class);
                intent.putExtra(Constant.POST_ID, post.getPost_id());
                intent.putExtra(Constant.AUTHOR_ID, post.getPublisher());
                intent.putExtra(Constant.IMAGE_URL, post.getImage_url());
                mContext.startActivity(intent);
            }
        });

        holder.tv_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CommentActivity.class);
                intent.putExtra(Constant.POST_ID, post.getPost_id());
                intent.putExtra(Constant.AUTHOR_ID, post.getPublisher());
                mContext.startActivity(intent);
            }
        });

        holder.iv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.iv_save.getTag().equals(Constant.SAVE)) {
                    FirebaseDatabase.getInstance().getReference().child(Constant.SAVES)
                            .child(firebaseUser.getUid()).child(post.getPost_id()).setValue(true);
                } else {
                    FirebaseDatabase.getInstance().getReference().child(Constant.SAVES)
                            .child(firebaseUser.getUid()).child(post.getPost_id()).removeValue();
                }
            }
        });

        holder.iv_ava.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.getSharedPreferences(Constant.PROFILE, Context.MODE_PRIVATE)
                        .edit().putString(Constant.PROFILE_ID, post.getPublisher()).apply();

                ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new PersonFragment()).commit();
            }
        });

        holder.tv_user_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.getSharedPreferences(Constant.PROFILE, Context.MODE_PRIVATE)
                        .edit().putString(Constant.PROFILE_ID, post.getPublisher()).apply();

                ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new PersonFragment()).commit();
            }
        });

        isLiked(post.getPost_id(), holder.iv_heart);
        getLikes(post.getPost_id(), holder.tv_heart);
        getComments(post.getPost_id(), holder.tv_comment);
        isSaved(post.getPost_id(), holder.iv_save);

        holder.tv_heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ListUserActivity.class);
                intent.putExtra(Constant.ID, post.getPublisher());
                intent.putExtra(Constant.TITLE, Constant.LIKES);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    private void isSaved(final String postId, final ImageView image) {
        FirebaseDatabase.getInstance().getReference().child(Constant.SAVES).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(postId).exists()) {
                    image.setImageResource(R.drawable.ic_savefill);
                    image.setTag(Constant.SAVED);
                } else {
                    image.setImageResource(R.drawable.ic_save);
                    image.setTag(Constant.SAVE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void isLiked(String postId, final ImageView imageView) {
        FirebaseDatabase.getInstance().getReference().child(Constant.LIKES).child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(firebaseUser.getUid()).exists()) {
                    imageView.setImageResource(R.drawable.ic_likefill);
                    imageView.setTag(Constant.LIKED);
                } else {
                    imageView.setImageResource(R.drawable.ic_like);
                    imageView.setTag(Constant.LIKE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getLikes(String postId, final TextView text) {
        FirebaseDatabase.getInstance().getReference().child(Constant.LIKES).child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                text.setText(formatNumber(dataSnapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getComments(String postId, final TextView text) {
        FirebaseDatabase.getInstance().getReference().child(Constant.COMMENTS).child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                text.setText(formatNumber(dataSnapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkAuthor(String userId, final ImageView iv_more) {
        if (userId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            iv_more.setVisibility(View.VISIBLE);
        } else {
            iv_more.setVisibility(View.GONE);
        }
        Log.d("123", String.valueOf(userId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())));
    }

    private void addNotification(String postId, String publisherId) {
        HashMap<String, Object> map = new HashMap<>();
        map.put(Constant.USER_ID, firebaseUser.getUid());
        map.put(Constant.TITLE, mContext.getString(R.string.likedYourPostLabel));
        map.put(Constant.POST_ID, postId);
        map.put(Constant.IS_POST, true);
        map.put(Constant.TIME_CREATED, System.currentTimeMillis() + "");
        FirebaseDatabase.getInstance().getReference().child(Constant.NOTIFICATIONS).child(publisherId).push().setValue(map);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final ImageView iv_ava;
        public final ImageView iv_image;
        public final ImageView iv_heart;
        public final ImageView iv_comment;
        public final ImageView iv_save;
        public final ImageView iv_more;

        public final TextView tv_user_name;
        public final TextView tv_user_name2;
        public final TextView tv_time_created;
        //public final TextView tv_privacy;
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
            iv_more = itemView.findViewById(R.id.iv_more);

            tv_user_name = itemView.findViewById(R.id.tv_user_name);
            tv_user_name2 = itemView.findViewById(R.id.tv_user_name2);
            tv_time_created = itemView.findViewById(R.id.tv_time_created);
            //tv_privacy = itemView.findViewById(R.id.tv_privacy);
            tv_description = itemView.findViewById(R.id.tv_description);
            tv_heart = itemView.findViewById(R.id.tv_heart);
            tv_comment = itemView.findViewById(R.id.tv_comment);
        }
    }
}
