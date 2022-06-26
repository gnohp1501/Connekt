package com.example.connekt.adapter;

import static com.example.connekt.utils.DateUtils.formatNumber;
import static com.example.connekt.utils.DateUtils.getTimeAgo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.connekt.R;
import com.example.connekt.constant.Constant;
import com.example.connekt.model.Comment;
import com.example.connekt.model.User;
import com.example.connekt.view.activity.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private final Context mContext;
    private final List<Comment> mComments;
    String postId;
    private FirebaseUser fUser;

    public CommentAdapter(Context mContext, List<Comment> mComments, String postId) {
        this.mContext = mContext;
        this.mComments = mComments;
        this.postId = postId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.comment_item, parent, false);
        return new CommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        fUser = FirebaseAuth.getInstance().getCurrentUser();
        Comment comment = mComments.get(position);
        holder.comment.setText(comment.getComment());
        holder.time_created.setText(getTimeAgo(Long.parseLong(comment.getTime_created())));

        FirebaseDatabase.getInstance().getReference().child(Constant.USERS)
                .child(comment.getPublisher()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                holder.username.setText(user.getUser_name());
                if (user.getImage_url().equals(Constant.DEFAULT)) {
                    holder.imageProfile.setImageResource(R.mipmap.ic_launcher);

                } else {
                    Picasso.get().load(user.getImage_url()).into(holder.imageProfile);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MainActivity.class);
                intent.putExtra(Constant.PUBLISHER_ID, comment.getPublisher());
                mContext.startActivity(intent);
            }
        });
        holder.imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MainActivity.class);
                intent.putExtra(Constant.PUBLISHER_ID, comment.getPublisher());
                mContext.startActivity(intent);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (comment.getPublisher().endsWith(fUser.getUid())) {
                    AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                    alertDialog.setTitle(R.string.deleteLabel);
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, mContext.getString(R.string.noLabel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, mContext.getString(R.string.yesLabel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            FirebaseDatabase.getInstance().getReference().child(Constant.COMMENTS)
                                    .child(postId).child(comment.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(mContext, mContext.getString(R.string.deleteCommentSuccessLabel), Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }
                                }
                            });

                        }
                    });
                    alertDialog.show();
                }
                return true;
            }
        });

        holder.flag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.flag.getTag().equals(Constant.FLAG)) {
                    FirebaseDatabase.getInstance().getReference().child(Constant.FLAG)
                            .child(comment.getId()).child(fUser.getUid()).setValue(true);
                } else {
                    FirebaseDatabase.getInstance().getReference().child(Constant.FLAG)
                            .child(comment.getId()).child(fUser.getUid()).removeValue();
                }
            }
        });
        isFlag(comment.getId(), holder.flag);
        getFlag(comment.getId(), holder.tv_flag);
    }

    private void isFlag(String postId, final ImageView imageView) {
        FirebaseDatabase.getInstance().getReference().child(Constant.FLAG).child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(fUser.getUid()).exists()) {
                    imageView.setImageResource(R.drawable.ic_baseline_flag_24_red);
                    imageView.setTag(Constant.FLAGED);
                } else {
                    imageView.setImageResource(R.drawable.ic_baseline_flag_24);
                    imageView.setTag(Constant.FLAG);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getFlag(String postId, final TextView text) {
        FirebaseDatabase.getInstance().getReference().child(Constant.FLAG).child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                text.setText(formatNumber(dataSnapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mComments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView imageProfile;
        public TextView username;
        public TextView comment;
        public TextView time_created;
        public TextView tv_flag;
        public ImageView flag;


        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            imageProfile = itemView.findViewById(R.id.image_profile);
            username = itemView.findViewById(R.id.username);
            comment = itemView.findViewById(R.id.comment);
            time_created = itemView.findViewById(R.id.tv_time_created);
            tv_flag = itemView.findViewById(R.id.tv_flag);
            flag = itemView.findViewById(R.id.flag);
        }
    }
}

