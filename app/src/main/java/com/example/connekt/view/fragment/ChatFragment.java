package com.example.connekt.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.connekt.R;
import com.example.connekt.adapter.UserAdapter;
import com.example.connekt.adapter.UserChatAdapter;
import com.example.connekt.model.Chatlist;
import com.example.connekt.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ChatFragment extends Fragment {

    private RecyclerView recyclerView;
    private UserChatAdapter userAdapter;

    FirebaseUser firebaseUser;
    DatabaseReference reference;
    private List<Chatlist> listChat;

    private List<User> mUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        recyclerView = view.findViewById(R.id.recyclerview_mess);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //usersList = new ArrayList<>();
        listChat = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("chatlist")
                .child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listChat.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Chatlist chatlist = snap.getValue(Chatlist.class);
                    listChat.add(chatlist);
                }
                chatList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//         reference = FirebaseDatabase.getInstance().getReference("chats");
//         reference.addValueEventListener(new ValueEventListener() {
//             @Override
//             public void onDataChange(@NonNull DataSnapshot snapshot) {
//                 usersList.clear();
//                 for(DataSnapshot snap : snapshot.getChildren())
//                 {
//                     Chat chat= snap.getValue(Chat.class);
//                     if(chat.getSender().equals(firebaseUser.getUid()))
//                     {
//                         usersList.add(chat.getReceiver());
//                     }
//                     if (chat.getReceiver().equals(firebaseUser.getUid()))
//                     {
//                         usersList.add(chat.getSender());
//                     }
//                 }
//                 //readChat();
//             }
//
//             @Override
//             public void onCancelled(@NonNull DatabaseError error) {
//
//             }
//         });
        return view;
    }

    private void chatList() {
        mUser = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUser.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    User user = snap.getValue(User.class);
                    for (Chatlist list : listChat) {
                        if (user.getId().equals(list.getId())) {
                            mUser.add(user);
                        }
                    }
                }
                userAdapter = new UserChatAdapter(getContext(), mUser, true);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}