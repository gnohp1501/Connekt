package com.example.connekt.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.connekt.R;
import com.example.connekt.constant.Constant;
import com.example.connekt.databinding.ActivityCreateAccountBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class CreateAccountActivity extends AppCompatActivity {
    private ActivityCreateAccountBinding binding;
    private DatabaseReference mRootRef;
    private FirebaseAuth mAuth;
    public String EMAIL;
    public String PASSWORD;
    public String ID;
    public String FULL_NAME;
    public String USER_NAME;
    public String BIO;
    public String IMAGE_URL;
    public String PHONE;
    public String DOB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateAccountBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        binding.butCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.createUserWithEmailAndPassword(EMAIL, PASSWORD).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        EMAIL = getIntent().getExtras().getString(Constant.EMAIL);
                        PASSWORD = getIntent().getExtras().getString(Constant.PASSWORD);
                        ID = mAuth.getCurrentUser().getUid();
                        FULL_NAME = binding.etFullName.getText().toString();
                        USER_NAME = binding.etUserName.getText().toString();
                        BIO = binding.etBio.getText().toString();
                        //pIMAGE_URL = binding.etFullName.getText().toString();
                        PHONE = binding.etPhone.getText().toString();
                        //DOB = binding.et.getText().toString();
                        HashMap<String, Object> map = new HashMap<>();
                        map.put(Constant.ID, ID);
                        map.put(Constant.FULL_NAME, FULL_NAME);
                        map.put(Constant.EMAIL, EMAIL);
                        map.put(Constant.USER_NAME, USER_NAME);
                        map.put(Constant.BIO, BIO);
                        map.put(Constant.IMAGE_URL, "default");
                        map.put(Constant.PHONE, PHONE);
                        map.put(Constant.BOD, "01/01/2000");
                        mRootRef.child(Constant.USERS).child(mAuth.getCurrentUser().getUid()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(CreateAccountActivity.this, R.string.uploadProfileBetterLabel, Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(CreateAccountActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(CreateAccountActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }
        });
    }
}