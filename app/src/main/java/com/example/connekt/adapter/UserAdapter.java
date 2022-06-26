package com.example.connekt.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.connekt.R;
import com.example.connekt.constant.Constant;
import com.example.connekt.model.User;
import com.example.connekt.view.fragment.PersonFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private final Context mContext;
    private final List<User> mUsers;
    private final boolean isFragment;

    private FirebaseUser firebaseUser;
    private List<String> id1;
    private List<String> id2;
    private int mutual;


    public UserAdapter(Context mContext, List<User> mUsers, boolean isFragment) {
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.isFragment = isFragment;
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
        holder.layout.setVisibility(View.VISIBLE);
        holder.tv_user_name.setText(user.getUser_name());
        holder.tv_full_name.setText(user.getFull_name());
        Picasso.get().load(user.getImage_url()).placeholder(R.color.white).into(holder.iv_ava);
        isFollowed(user.getId(), holder.btn_Follow);

        if (user.getId().equals(firebaseUser.getUid())) {
            holder.btn_Follow.setVisibility(View.GONE);
            holder.layout.setVisibility(View.GONE);
            holder.layout.setLayoutParams(new RelativeLayout.LayoutParams(0, 0));
        }

        holder.btn_Follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.btn_Follow.getText().toString().equals((Constant.FOLLOW))) {
                    FirebaseDatabase.getInstance().getReference().child(Constant.FOLLOW).
                            child((firebaseUser.getUid())).child(Constant.FOLLOWING).child(user.getId()).setValue(true);

                    FirebaseDatabase.getInstance().getReference().child(Constant.FOLLOW).
                            child(user.getId()).child(Constant.FOLLOWERS).child(firebaseUser.getUid()).setValue(true);

                    addNotification(user.getId());
                } else {
                    FirebaseDatabase.getInstance().getReference().child(Constant.FOLLOW).
                            child((firebaseUser.getUid())).child(Constant.FOLLOWING).child(user.getId()).removeValue();

                    FirebaseDatabase.getInstance().getReference().child(Constant.FOLLOW).
                            child(user.getId()).child(Constant.FOLLOWERS).child(firebaseUser.getUid()).removeValue();
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.getSharedPreferences(Constant.PROFILE, Context.MODE_PRIVATE)
                        .edit().putString(Constant.PROFILE_ID, user.getId()).apply();

                ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new PersonFragment()).commit();
            }
        });

        checkFollowingUsers(user.getId(), holder.tv_mutual_friend);

    }

    private void isFollowed(final String id, final Button btnFollow) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(Constant.FOLLOW).child(firebaseUser.getUid())
                .child(Constant.FOLLOWING);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(id).exists()) {
                    btnFollow.setText(Constant.FOLLOWING);
                } else {
                    btnFollow.setText(Constant.FOLLOW);
                }

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

    private void addNotification(String userId) {
        HashMap<String, Object> map = new HashMap<>();
        map.put(Constant.USER_ID, firebaseUser.getUid());
        map.put(Constant.TITLE, mContext.getString(R.string.followyouLabel));
        map.put(Constant.POST_ID, "");
        map.put(Constant.IS_POST, false);
        map.put(Constant.TIME_CREATED, System.currentTimeMillis() + "");
        FirebaseDatabase.getInstance().getReference().child(Constant.NOTIFICATIONS).child(userId).push().setValue(map);
    }

    private void checkFollowingUsers(String user, TextView view) {
        id1 = new ArrayList<>();
        id2 = new ArrayList<>();
        mutual = 0;
        FirebaseDatabase.getInstance().getReference().child(Constant.FOLLOW).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(Constant.FOLLOWING).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                id1.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    id1.add(snapshot.getKey());
                }
                id1.add(FirebaseAuth.getInstance().getCurrentUser().getUid());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        FirebaseDatabase.getInstance().getReference().child(Constant.FOLLOW).child(user).child(Constant.FOLLOWING).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                id2.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    id2.add(snapshot.getKey());
                }
                id2.add(FirebaseAuth.getInstance().getCurrentUser().getUid());
                mutual = 0;
                for (String id1 : id1) {
                    for (String id2: id2){
                        if (id1.equals(id2)) {
                            mutual++;
                        }
                    }
                }
                view.setText(mutual + " mutual friend");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
//        for (int i = 1; i <= id1.size(); i++) {
//            for (int j = 1; j <= id2.size(); j++) {
//                if (id1.get(i) == id2.get(j)) {
//                    mutual++;
//                }
//            }
//        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView iv_ava;
        public TextView tv_user_name;
        public TextView tv_full_name;
        public TextView tv_mutual_friend;
        public Button btn_Follow;
        public RelativeLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            iv_ava = itemView.findViewById(R.id.iv_ava);
            tv_user_name = itemView.findViewById(R.id.tv_user_name);
            tv_full_name = itemView.findViewById(R.id.tv_full_name);
            btn_Follow = itemView.findViewById(R.id.but_follow);
            layout = itemView.findViewById(R.id.layout);
            tv_mutual_friend = itemView.findViewById(R.id.tv_mutual_friend);
        }
    }

}
