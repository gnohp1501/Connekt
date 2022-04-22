package com.example.connekt.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.connekt.R;
import com.example.connekt.constant.Constant;
import com.example.connekt.model.User;
import com.example.connekt.view.activity.MainActivity;
import com.example.connekt.view.fragment.PersonFragment;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private final Context mContext;
    private final List<User> mUsers;
    private final boolean isFragment;

    private FirebaseUser firebaseUser;


    public UserAdapter(Context mContext, List<User> mUsers, boolean isFargment) {
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.isFragment = isFargment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final User user = mUsers.get(position);
        holder.btn_Follow.setVisibility(View.VISIBLE);

        holder.tv_user_name.setText(user.getUser_name());
        holder.tv_full_name.setText(user.getFull_name());

        Picasso.get().load(user.getImage_url()).placeholder(R.mipmap.ic_launcher).into(holder.iv_ava);

        isFollowed(user.getId(), holder.btn_Follow);

        if (user.getId().equals(firebaseUser.getUid())) {
            holder.btn_Follow.setVisibility(View.GONE);
        }

        holder.btn_Follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.btn_Follow.getText().toString().equals(("follow"))) {
                    FirebaseDatabase.getInstance().getReference().child("follow").
                            child((firebaseUser.getUid())).child("following").child(user.getId()).setValue(true);

                    FirebaseDatabase.getInstance().getReference().child("follow").
                            child(user.getId()).child("followers").child(firebaseUser.getUid()).setValue(true);

                    addNotification(user.getId());
                } else {
                    FirebaseDatabase.getInstance().getReference().child("follow").
                            child((firebaseUser.getUid())).child("following").child(user.getId()).removeValue();

                    FirebaseDatabase.getInstance().getReference().child("follow").
                            child(user.getId()).child("followers").child(firebaseUser.getUid()).removeValue();
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFragment) {
                    mContext.getSharedPreferences("PROFILE", Context.MODE_PRIVATE).edit().putString("profileId", user.getId()).apply();
                    ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PersonFragment()).commit();
                } else {
                    Intent intent = new Intent(mContext, MainActivity.class);
                    intent.putExtra("publisherId", user.getId());
                    mContext.startActivity(intent);
                }
            }
        });

    }

    private void isFollowed(final String id, final Button btnFollow) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("follow").child(firebaseUser.getUid())
                .child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(id).exists())
                    btnFollow.setText("following");
                else
                    btnFollow.setText("follow");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView iv_ava;
        public TextView tv_user_name;
        public TextView tv_full_name;
        public Button btn_Follow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            iv_ava = itemView.findViewById(R.id.iv_ava);
            tv_user_name = itemView.findViewById(R.id.tv_user_name);
            tv_full_name = itemView.findViewById(R.id.tv_full_name);
            btn_Follow = itemView.findViewById(R.id.but_follow);
        }
    }

    private void addNotification(String userId) {
        HashMap<String, Object> map = new HashMap<>();

        map.put(Constant.USER_ID, firebaseUser.getUid());
        map.put(Constant.TITLE,Constant.FOLLOW_YOU);
        map.put(Constant.POST_ID, "");
        map.put(Constant.IS_POST, false);

        FirebaseDatabase.getInstance().getReference().child("notifications").child(userId).push().setValue(map);
    }

}
