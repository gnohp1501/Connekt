package com.example.connekt.view.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.connekt.R;
import com.example.connekt.constant.Constant;
import com.example.connekt.databinding.ActivitySignUpBinding;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {
    private ActivitySignUpBinding binding;
    private FirebaseAuth mAuth;
    ProgressDialog pd;
    public String EMAIL;
    public String PASSWORD;
    public String RE_PASSWORD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        mAuth = FirebaseAuth.getInstance();
        pd = new ProgressDialog(this);
        goSignIn();
        createAccount();
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
        Intent intent = new Intent(SignUpActivity.this, CreateAccountActivity.class);
        Bundle bundle = new Bundle();
        bundle.getString(Constant.EMAIL, binding.etEmail.getText().toString());
        bundle.getString(Constant.PASSWORD, binding.etPassword.getText().toString());
        startActivity(intent);
    }
}