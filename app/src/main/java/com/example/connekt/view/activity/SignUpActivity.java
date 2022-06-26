package com.example.connekt.view.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.connekt.R;
import com.example.connekt.constant.Constant;
import com.example.connekt.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {
    public String EMAIL;
    public String PASSWORD;
    public String RE_PASSWORD;
    public String ID;
    ProgressDialog pd;
    private ActivitySignUpBinding binding;
    private FirebaseAuth mAuth;
    private DatabaseReference mRootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        init();
        goSignIn();
        createAccount();
    }

    private void init() {
        mAuth = FirebaseAuth.getInstance();
        mRootRef = FirebaseDatabase.getInstance().getReference();
        pd = new ProgressDialog(this);
    }

    private void createAccount() {
        binding.butSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EMAIL = binding.etEmail.getText().toString();
                PASSWORD = binding.etPassword.getText().toString();
                RE_PASSWORD = binding.etRepassword.getText().toString();
                if (TextUtils.isEmpty(EMAIL) || TextUtils.isEmpty(PASSWORD)) {
                    Toast.makeText(SignUpActivity.this, R.string.noEmptyLabel, Toast.LENGTH_SHORT).show();
                } else if (PASSWORD.length() < 6) {
                    Toast.makeText(SignUpActivity.this, R.string.passwordTooShortLabel, Toast.LENGTH_SHORT).show();
                } else if (!PASSWORD.equals(RE_PASSWORD)) {
                    Toast.makeText(SignUpActivity.this, R.string.passwordNotMatchLabel, Toast.LENGTH_SHORT).show();
                } else {
                    registerUser();
                }
            }
        });
    }

    private void goSignIn() {
        binding.tvSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
            }
        });
    }

    private void registerUser() {
        mAuth.createUserWithEmailAndPassword(EMAIL + "@gmail.com", PASSWORD).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                ID = mAuth.getCurrentUser().getUid();
                HashMap<String, Object> map = new HashMap<>();
                map.put(Constant.ID, ID);
                map.put(Constant.FULL_NAME, EMAIL);
                map.put(Constant.EMAIL, EMAIL);
                map.put(Constant.USER_NAME, EMAIL);
                map.put(Constant.BIO, "");
                map.put(Constant.IMAGE_URL, Constant.DEFAULT);
                map.put(Constant.PHONE, " ");
                map.put(Constant.BOD, " ");
                map.put(Constant.STATUS, Constant.ONLINE);
                map.put(Constant.LAST_SEEN, System.currentTimeMillis() + "");
                mRootRef.child(Constant.USERS).child(mAuth.getCurrentUser().getUid()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Intent i = new Intent(SignUpActivity.this, EditProfileActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putBoolean(Constant.CREATED, false);
                            i.putExtras(bundle);
                            startActivity(i);
                            finish();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
}