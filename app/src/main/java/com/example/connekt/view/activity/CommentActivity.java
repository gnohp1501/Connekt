package com.example.connekt.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.connekt.R;
import com.example.connekt.adapter.CommentAdapter;
import com.example.connekt.constant.Constant;
import com.example.connekt.databinding.ActivityCommentBinding;
import com.example.connekt.model.Comment;
import com.example.connekt.model.Post;
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
    FirebaseUser fUser;
    private ActivityCommentBinding binding;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;
    private String postId;
    private String authorId;
    private String image_Url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCommentBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        init();
        getPostDetail();
        getUserImage();
        inputComment();
        getComment();
        close();
    }

    private void init() {
        Intent intent = getIntent();
        postId = intent.getStringExtra(Constant.POST_ID);
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(this, commentList, postId);
        binding.recyclerView.setAdapter(commentAdapter);
        fUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    private void getPostDetail() {
        FirebaseDatabase.getInstance().getReference().child(Constant.POSTS).child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);
                authorId = post.getPublisher();
                image_Url = post.getImage_url();
                Picasso.get().load(image_Url).into(binding.ivImage);
                FirebaseDatabase.getInstance().getReference().child(Constant.USERS).child(authorId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        binding.tvAuthor.setText("Post of " + user.getUser_name());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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

    private void inputComment() {
        binding.post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(binding.addComment.getText().toString())) {
                    Toast.makeText(CommentActivity.this, "Comment is empty", Toast.LENGTH_SHORT).show();
                } else {
                    putComment();
                }
            }
        });
    }

    private void close() {
        binding.ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void putComment() {
        HashMap<String, Object> map = new HashMap<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(Constant.COMMENTS).child(postId);
        String id = ref.push().getKey();
        map.put(Constant.ID, id);
        map.put(Constant.COMMENT, binding.addComment.getText().toString());
        map.put(Constant.PUBLISHER, fUser.getUid());
        map.put(Constant.TIME_CREATED, System.currentTimeMillis() + "");

        binding.addComment.setText("");
        ref.child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    if (!postId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        addNotification(postId, authorId);
                    }
                } else {
                    Toast.makeText(CommentActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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
                } else {
                    Picasso.get().load(user.getImage_url()).into(binding.imageProfile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addNotification(String postId, String publisherId) {
        HashMap<String, Object> map = new HashMap<>();
        map.put(Constant.USER_ID, fUser.getUid());
        map.put(Constant.TITLE, getString(R.string.commentYourPostLabel));
        map.put(Constant.POST_ID, postId);
        map.put(Constant.IS_POST, true);
        map.put(Constant.TIME_CREATED, System.currentTimeMillis() + "");
        FirebaseDatabase.getInstance().getReference().child(Constant.NOTIFICATIONS).child(publisherId).push().setValue(map);
    }
}