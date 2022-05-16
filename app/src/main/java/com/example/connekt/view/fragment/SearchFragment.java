package com.example.connekt.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.connekt.adapter.PhotoAdapter;
import com.example.connekt.constant.Constant;
import com.example.connekt.databinding.FragmentSearchBinding;
import com.example.connekt.model.Post;
import com.example.connekt.view.activity.SearchPeopleActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class SearchFragment extends Fragment {

    private PhotoAdapter photoAdapter;
    private List<Post> photoList;

    private FragmentSearchBinding binding;
    FirebaseUser firebaseUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        binding.rvListPhoto.setHasFixedSize(true);
        binding.rvListPhoto.setLayoutManager(new GridLayoutManager(getContext(), 2));
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        photoList = new ArrayList<>();
        photoAdapter = new PhotoAdapter(getContext(), photoList);
        binding.rvListPhoto.setAdapter(photoAdapter);
        binding.searchUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), SearchPeopleActivity.class));
            }
        });
        myPhotos();
        return view;
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
}