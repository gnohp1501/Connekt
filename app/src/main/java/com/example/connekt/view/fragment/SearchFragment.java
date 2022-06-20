package com.example.connekt.view.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.connekt.adapter.PhotoAdapter;
import com.example.connekt.adapter.UserAdapter;
import com.example.connekt.constant.Constant;
import com.example.connekt.databinding.FragmentSearchBinding;
import com.example.connekt.model.Post;
import com.example.connekt.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class SearchFragment extends Fragment {

    private PhotoAdapter photoAdapter;
    private List<Post> photoList;

    private UserAdapter userAdapter;
    private List<User> mUser;

    private FragmentSearchBinding binding;
    FirebaseUser firebaseUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        searchUserTab();
        searchImageTab();
        init();
        myPhotos();
        searchUser();
        return view;
    }

    private void init() {
        binding.rvListPhoto.setHasFixedSize(true);
        binding.rvListPhoto.setLayoutManager(new GridLayoutManager(getContext(), 3));
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        photoList = new ArrayList<>();
        photoAdapter = new PhotoAdapter(getContext(), photoList);
        binding.rvListPhoto.setAdapter(photoAdapter);
        //
        mUser = new ArrayList<>();
        binding.rvListUser.setHasFixedSize(true);
        binding.rvListUser.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvListPhoto.setVisibility(View.VISIBLE);
        binding.rvListUser.setVisibility(View.GONE);
        binding.searchUser.setVisibility(View.VISIBLE);
    }

    private void searchUserTab() {
        binding.searchUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.etSearchUser.setVisibility(View.VISIBLE);
                binding.rvListPhoto.setVisibility(View.GONE);
                binding.rvListUser.setVisibility(View.VISIBLE);

                binding.searchImage.setVisibility(View.VISIBLE);
                binding.searchUser.setVisibility(View.GONE);
            }
        });
    }

    private void searchImageTab() {
        binding.searchImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.etSearchUser.setVisibility(View.GONE);
                binding.rvListPhoto.setVisibility(View.VISIBLE);
                binding.rvListUser.setVisibility(View.GONE);

                binding.searchUser.setVisibility(View.VISIBLE);
                binding.searchImage.setVisibility(View.GONE);
            }
        });
    }

    private void myPhotos() {
        FirebaseDatabase.getInstance().getReference().child(Constant.POSTS)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        photoList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Post post = snapshot.getValue(Post.class);
                            photoList.add(post);
                        }
                        Collections.reverse(photoList);
                        photoAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError dataError) {

                    }
                });
    }

    private void searchUser() {
        binding.etSearchUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                search(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void search(String user) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Query query = FirebaseDatabase.getInstance().getReference(Constant.USERS)
                .orderByChild(Constant.USER_NAME)
                .startAt(user)
                .endAt(user + "\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!binding.etSearchUser.getText().toString().equals("")) {
                    mUser.clear();
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        User user = snap.getValue(User.class);
                        assert user != null;
                        assert firebaseUser != null;
                        if (!user.getId().equals(firebaseUser.getUid())) {
                            user.getUser_name();
                            mUser.add(user);
                        }
                    }
                    userAdapter = new UserAdapter(getContext(), mUser, false);
                    binding.rvListUser.setAdapter(userAdapter);
                } else {
                    readUsers();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readUsers() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(Constant.USERS);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUser.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    mUser.add(user);
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}