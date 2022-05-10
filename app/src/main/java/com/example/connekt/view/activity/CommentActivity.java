package com.example.connekt.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.example.connekt.R;
import com.example.connekt.adapter.CommentAdapter;
import com.example.connekt.adapter.UserAdapter;
import com.example.connekt.constant.Constant;
import com.example.connekt.databinding.ActivityCommentBinding;
import com.example.connekt.databinding.ActivityListUserBinding;
import com.example.connekt.model.Comment;
import com.example.connekt.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

public class CommentActivity extends AppCompatActivity {
    private ActivityCommentBinding binding;

    private CommentAdapter commentAdapter;
    private List<Comment> commentList;
    private String postId;
    FirebaseUser fUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCommentBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(Constant.COMMENTS);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Intent intent = getIntent();
        postId = intent.getStringExtra(Constant.POST_ID);

        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentList = new ArrayList<>();
        commentAdapter= new CommentAdapter(this,commentList,postId);
        //
        binding.recyclerView.setAdapter(commentAdapter);

        //

        fUser = FirebaseAuth.getInstance().getCurrentUser();

        getUserImage();
        binding.post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(binding.addComment.getText().toString()))
                {
                    Toast.makeText(CommentActivity.this,"No comment added",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    putComment();
                }
            }
        });
        getComment();
    }

    private void getComment() {

        FirebaseDatabase.getInstance().getReference().child(Constant.COMMENTS).child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Comment comment = snapshot.getValue(Comment.class);
                    commentList.add(comment);
                }

                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void putComment() {
        HashMap<String,Object> map = new HashMap<>();
        DatabaseReference ref =  FirebaseDatabase.getInstance().getReference().child(Constant.COMMENTS).child(postId);
        String id = ref.push().getKey();
        map.put(Constant.ID,id);
        map.put(Constant.COMMENT,binding.addComment.getText().toString());
        map.put(Constant.PUBLISHER,fUser.getUid());

        binding.addComment.setText("");
        ref.child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(CommentActivity.this,"Comment added",Toast.LENGTH_SHORT).show();

                }else
                {
                    Toast.makeText(CommentActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void getUserImage() {
        FirebaseDatabase.getInstance().getReference().child(Constant.USERS)
                .child(fUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user.getImage_url().equals(Constant.DEFAULT)) {
                    binding.imageProfile.setImageResource(R.mipmap.ic_launcher);
                } else
                {
                    Picasso.get().load(user.getImage_url()).into(binding.imageProfile);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}