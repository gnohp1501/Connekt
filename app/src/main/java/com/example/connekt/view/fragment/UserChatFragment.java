package com.example.connekt.view.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.connekt.R;
import com.example.connekt.adapter.UserChatAdapter;
import com.example.connekt.constant.Constant;
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

public class UserChatFragment extends Fragment {

    private RecyclerView recyclerViewUser;
    private UserChatAdapter userAdapter;
    private List<User> mUsers;
    private EditText search_user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        recyclerViewUser = view.findViewById(R.id.recycleview_user);
        recyclerViewUser.setHasFixedSize(true);
        recyclerViewUser.setLayoutManager(new LinearLayoutManager(getContext()));
        mUsers = new ArrayList<>();
        search_user = view.findViewById(R.id.search_user);
        //
        searchUser();
        readUsers();
        return view;
    }

    private void searchUser() {
        search_user.addTextChangedListener(new TextWatcher() {
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
                if (!search_user.getText().toString().equals("")) {
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
                    userAdapter = new UserChatAdapter(getContext(), mUsers, false);
                    recyclerViewUser.setAdapter(userAdapter);
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
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    User user = snap.getValue(User.class);
                    assert user != null;
                    assert firebaseUser != null;
                    if (!user.getId().equals(firebaseUser.getUid())) {
                        mUsers.add(user);
                    }
                }
                userAdapter = new UserChatAdapter(getContext(), mUsers, true);
                recyclerViewUser.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}