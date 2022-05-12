package com.example.connekt.view.activity;

import static com.example.connekt.utils.DateUtils.getTime;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.connekt.R;
import com.example.connekt.adapter.MessageAdapter;
import com.example.connekt.constant.Constant;
import com.example.connekt.model.Chat;
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
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessActivity extends AppCompatActivity {

    CircleImageView image_user;
    TextView username_user;
    TextView last_seen;

    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    Intent intent;

    ImageView button_send;
    EditText text_send;
    RecyclerView recyclerview_mess;
    String userid;

    MessageAdapter messageAdapter;
    List<Chat> mChat;

    ValueEventListener seenListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mess);
        init();
        setFirebaseUser();
        setButton_send();
    }

    public void init() {
        image_user = findViewById(R.id.iv_ava);
        username_user = findViewById(R.id.tv_user_name);
        last_seen = findViewById(R.id.tv_last_seen);
        button_send = findViewById(R.id.button_send);
        text_send = findViewById(R.id.text_send);
        recyclerview_mess = findViewById(R.id.recyclerview_mess);
        recyclerview_mess.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerview_mess.setLayoutManager(linearLayoutManager);
    }

    public void setFirebaseUser() {
        intent = getIntent();
        userid = intent.getStringExtra(Constant.USER_ID);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference(Constant.USERS).child(userid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                username_user.setText(user.getUser_name());
                if (user.getImage_url().equals(Constant.DEFAULT)) {
                    image_user.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Picasso.get().load(user.getImage_url()).into(image_user);
                }
                if (user.getStatus().equals("online")) {
                    last_seen.setText("Active");
                } else {
                    last_seen.setText("Last seen in " + getTime(Long.parseLong(user.getLast_seen())));
                }
                readMess(firebaseUser.getUid(), userid, user.getImage_url());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        seenMess(userid);
    }

    private void sendMess(String sender, String receiver, String mess) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Constant.SENDER, sender);
        hashMap.put(Constant.RECEIVER, receiver);
        hashMap.put(Constant.MESS, mess);
        hashMap.put(Constant.DATE, System.currentTimeMillis() + "");
        hashMap.put(Constant.TIME, System.currentTimeMillis() + "");
        hashMap.put(Constant.IS_SEEN, false);
        reference.child(Constant.CHATS).push().setValue(hashMap);

        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference(Constant.CHAT_LIST)
                .child(firebaseUser.getUid())
                .child(userid);
        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    chatRef.child(Constant.ID).setValue(userid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void seenMess(String userid) {
        databaseReference = FirebaseDatabase.getInstance().getReference(Constant.CHATS);
        seenListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Chat chat = snap.getValue(Chat.class);
                    if (chat.getReceiver().equals(firebaseUser.getUid()) &&
                            chat.getSender().equals(userid)) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put(Constant.IS_SEEN, true);
                        snap.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void setButton_send() {
        button_send.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                String msg = text_send.getText().toString();
                if (!msg.equals("")) {
                    sendMess(firebaseUser.getUid(), userid, msg);
                } else {
                    Toast.makeText(MessActivity.this, "Empty", Toast.LENGTH_SHORT).show();
                }
                text_send.setText("");
            }
        });
    }

    private void readMess(final String myID, final String userid, final String imageUrl) {
        mChat = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference(Constant.CHATS);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mChat.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Chat chat = snap.getValue(Chat.class);
                    if (chat.getReceiver().equals(myID) && chat.getSender().equals(userid) ||
                            chat.getReceiver().equals(userid) && chat.getSender().equals(myID)
                    ) {
                        mChat.add(chat);
                    }
                    messageAdapter = new MessageAdapter(MessActivity.this, mChat, imageUrl);
                    recyclerview_mess.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void status(String status) {
        databaseReference = FirebaseDatabase.getInstance().getReference(Constant.USERS).child(firebaseUser.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Constant.STATUS, status);
        databaseReference.updateChildren(hashMap);
    }

    private void lastSeen(String last_seen) {
        databaseReference = FirebaseDatabase.getInstance().getReference(Constant.USERS).child(firebaseUser.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Constant.LAST_SEEN, last_seen);
        databaseReference.updateChildren(hashMap);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        status("online");
        lastSeen("0");
    }

    @Override
    protected void onPause() {
        super.onPause();
        databaseReference.removeEventListener(seenListener);
        status("offline");
        lastSeen(System.currentTimeMillis() + "");
    }
}