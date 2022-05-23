package com.example.connekt.view.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.connekt.adapter.UserAdapter;
import com.example.connekt.constant.Constant;
import com.example.connekt.databinding.ActivitySearchPeopleBinding;
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

public class SearchPeopleActivity extends AppCompatActivity {
    private ActivitySearchPeopleBinding binding;
    private UserAdapter userAdapter;
    private List<User> mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchPeopleBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        mUser = new ArrayList<>();
        binding.rvListUser.setHasFixedSize(true);
        binding.rvListUser.setLayoutManager(new LinearLayoutManager(this));
        searchUser();
        binding.ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
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
                    userAdapter = new UserAdapter(SearchPeopleActivity.this, mUser, false);
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