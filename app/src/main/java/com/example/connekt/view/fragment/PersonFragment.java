package com.example.connekt.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.connekt.adapter.PhotoAdapter;
import com.example.connekt.databinding.FragmentPersonBinding;
import com.example.connekt.model.Post;
import com.example.connekt.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class PersonFragment extends Fragment {

    private FragmentPersonBinding binding;
    private FirebaseUser fUser;
    String profileId;

    private PhotoAdapter postAdapterSaves;
    private List<Post> mySavedPosts;

    private PhotoAdapter photoAdapter;
    private List<Post> myPhotoList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPersonBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        fUser = FirebaseAuth.getInstance().getCurrentUser();

        String data = getContext().getSharedPreferences("PROFILE", Context.MODE_PRIVATE).getString("profileId", "none");
        if (data.equals("none")) {
            profileId = fUser.getUid();
        } else {
            profileId = data;
        }
        userInfo();
        getFollowersAndFollowingCount();
        getPostCount();
        myPhotos();
        getSavedPosts();

        binding.rvPictures.setHasFixedSize(true);
        binding.rvPictures.setLayoutManager(new GridLayoutManager(getContext(), 3));
        myPhotoList = new ArrayList<>();
        photoAdapter = new PhotoAdapter(getContext(), myPhotoList);
        binding.rvPictures.setAdapter(photoAdapter);
        //

        binding.rvSaved.setLayoutManager(new GridLayoutManager(getContext(), 3));
        mySavedPosts = new ArrayList<>();
        postAdapterSaves = new PhotoAdapter(getContext(), mySavedPosts);
        binding.rvSaved.setAdapter(postAdapterSaves);
        binding.imbMyPictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.rvPictures.setVisibility(View.VISIBLE);
                binding.rvSaved.setVisibility(View.GONE);
            }
        });
        binding.imbSavedPictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.rvPictures.setVisibility(View.GONE);
                binding.rvSaved.setVisibility(View.VISIBLE);
            }
        });
        return view;
    }

    private void getSavedPosts() {
        List<String> savedIds = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference().child("saves")
                .child(fUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    savedIds.add(snapshot.getKey());

                }
                FirebaseDatabase.getInstance().getReference().child("posts")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                                mySavedPosts.clear();
                                for (DataSnapshot snapshot1 : dataSnapshot1.getChildren()) {
                                    Post post = snapshot1.getValue(Post.class);
                                    for (String id : savedIds) {
                                        if (post.getPost_id().equals(id)) {
                                            mySavedPosts.add(post);

                                        }

                                    }
                                }
                                postAdapterSaves.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void myPhotos() {
        FirebaseDatabase.getInstance().getReference().child("posts")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        myPhotoList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Post post = snapshot.getValue(Post.class);
                            if (post.getPublisher().equals(profileId)) {
                                myPhotoList.add(post);

                            }
                        }
                        Collections.reverse(myPhotoList);
                        photoAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError dataError) {

                    }
                });
    }

    private void getFollowersAndFollowingCount() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child("follow").child(profileId);
        ref.child("followers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                binding.tvFollowers.setText("" + dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ref.child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                binding.tvFollowing.setText("" + dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void userInfo() {
        FirebaseDatabase.getInstance().getReference().child("users")
                .child(profileId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Picasso.get().load(user.getImage_url()).into(binding.ivAva);
                binding.tvUserName.setText(user.getUser_name());
                binding.tvFullName.setText(user.getFull_name());
                binding.tvBio.setText(user.getBio());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getPostCount() {
        FirebaseDatabase.getInstance().getReference().child("posts")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        int counter = 0;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Post post = snapshot.getValue(Post.class);
                            if (post.getPublisher().equals(profileId)) counter++;


                        }
                        binding.tvPosts.setText(String.valueOf(counter));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}