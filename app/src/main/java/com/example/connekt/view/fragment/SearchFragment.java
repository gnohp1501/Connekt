package com.example.connekt.view.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.connekt.adapter.UserAdapter;
import com.example.connekt.constant.Constant;
import com.example.connekt.databinding.FragmentSearchBinding;
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
import java.util.List;


public class SearchFragment extends Fragment {

    private List<User> mUsers;
    private UserAdapter userAdapter;

    private FragmentSearchBinding binding;
    FirebaseUser firebaseUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        binding.rvListUser.setHasFixedSize(true);
        binding.rvListUser.setLayoutManager(new LinearLayoutManager(getContext()));
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mUsers = new ArrayList<>();
        userAdapter = new UserAdapter(getContext(), mUsers, true);
        binding.rvListUser.setAdapter(userAdapter);
        searchUser();
        readUsers();
        return view;
    }

    private void searchUser() {
        binding.searchUser.addTextChangedListener(new TextWatcher() {
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
                if (!binding.searchUser.getText().toString().equals("")) {
                    mUsers.clear();
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        User user = snap.getValue(User.class);

                        assert user != null;
                        assert firebaseUser != null;
                        if (!user.getId().equals(firebaseUser.getUid())) {
                            user.getUser_name();
                            mUsers.add(user);
                        }
                    }
                    userAdapter = new UserAdapter(getContext(), mUsers, false);
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
                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    mUsers.add(user);
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void searchUser(String s) {
        Query query = FirebaseDatabase.getInstance().getReference().child(Constant.USERS)
                .orderByChild("username").startAt(s).endAt(s + "\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    mUsers.add(user);
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}