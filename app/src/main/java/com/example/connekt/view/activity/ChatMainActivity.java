package com.example.connekt.view.activity;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.connekt.R;
import com.example.connekt.constant.Constant;
import com.example.connekt.databinding.ActivityChatMainBinding;
import com.example.connekt.model.User;
import com.example.connekt.view.fragment.ChatFragment;
import com.example.connekt.view.fragment.UserChatFragment;
import com.google.android.material.tabs.TabLayout;
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

public class ChatMainActivity extends AppCompatActivity {
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;
    private ActivityChatMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        init();
        setFirebaseUser();
    }

    public void init() {
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager viewPager = findViewById(R.id.view_pager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragments(new ChatFragment(), "CHAT");
        viewPagerAdapter.addFragments(new UserChatFragment(), "USER");

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    public void setFirebaseUser() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference(Constant.USERS).child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                binding.tvUserName.setText(user.getUser_name());
                if (user.getImage_url().equals(Constant.DEFAULT)) {
                    binding.ivAva.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Picasso.get().load(user.getImage_url()).into(binding.ivAva);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void status(String status) {
        databaseReference = FirebaseDatabase.getInstance().getReference(Constant.USERS).child(firebaseUser.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Constant.STATUS, status);
        databaseReference.updateChildren(hashMap);
    }

    private void lastSeen(String last_seen) {
        databaseReference = FirebaseDatabase.getInstance().getReference(Constant.USERS).child(firebaseUser.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Constant.LAST_SEEN, last_seen);
        databaseReference.updateChildren(hashMap);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        status(Constant.ONLINE);
        lastSeen("0");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
        lastSeen(System.currentTimeMillis() + "");
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final ArrayList<Fragment> fragments;
        private final ArrayList<String> titles;


        public ViewPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFragments(Fragment fragment, String title) {
            fragments.add(fragment);
            titles.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }
}