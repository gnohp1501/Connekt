package com.example.connekt.view.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.connekt.R;
import com.example.connekt.adapter.UserAdapter;
import com.example.connekt.databinding.FragmentHomeBinding;
import com.example.connekt.databinding.FragmentSearchBinding;
import com.example.connekt.model.User;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        binding.recycleView.setHasFixedSize(true);
        binding.recycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        mUsers = new ArrayList<>();
        userAdapter = new UserAdapter(getContext() , mUsers , true);
        binding.recycleView.setAdapter(userAdapter);

        readUsers();
        return view;
    }
    private void readUsers() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (TextUtils.isEmpty(binding.searchView.getQuery().toString())){
                    mUsers.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        User user = snapshot.getValue(User.class);
                        mUsers.add(user);
                    }

                    userAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void searchUser(String s) {
        Query query = FirebaseDatabase.getInstance().getReference().child("Users")
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