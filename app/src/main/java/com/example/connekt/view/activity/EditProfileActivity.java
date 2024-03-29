package com.example.connekt.view.activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.connekt.constant.Constant;
import com.example.connekt.databinding.ActivityEditProfileBinding;
import com.example.connekt.model.User;
import com.example.connekt.utils.DateUtils;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class EditProfileActivity extends AppCompatActivity {
    private final int REQUEST_CODE = 1;
    private final int REQUEST_OKE = -1;
    FirebaseStorage storage;
    StorageReference storageReference;
    private FirebaseUser fUser;
    private ActivityEditProfileBinding binding;
    private Uri filePathUri;
    ProgressDialog pd;
    final Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        init();
        changePhoto();
        save();
        close();
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, day);
                updateLabel();
            }
        };
        binding.etDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(EditProfileActivity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void updateLabel() {
        String myFormat = "dd/MM/YYYY";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
        binding.etDob.setText(dateFormat.format(myCalendar.getTime()));
    }

    private void init() {
        pd = new ProgressDialog(this);
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        Bundle intent = getIntent().getExtras();
        if (intent.getBoolean(Constant.CREATED)) {
            userInfo();
        } else {
            binding.tvSave.setText("Create");
            binding.ivClose.setVisibility(View.GONE);
            binding.tvTitle.setText("Create New Profile");
            binding.tvChangePhoto.setText("Upload your avatar");
        }
    }

    private void close() {
        binding.ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void changePhoto() {
        binding.tvChangePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });
    }

    private void save() {
        binding.tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(binding.etUserName.getText().toString()) || TextUtils.isEmpty(binding.etFullName.getText().toString())) {
                    Toast.makeText(EditProfileActivity.this, "Full name, User name can not empty", Toast.LENGTH_SHORT).show();
                } else {
                    updateProfile();
                }
            }
        });
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_CODE);
    }

    private void updateProfile() {
        HashMap<String, Object> map = new HashMap<>();
        map.put(Constant.FULL_NAME, binding.etFullName.getText().toString());
        map.put(Constant.USER_NAME, binding.etUserName.getText().toString());
        map.put(Constant.BOD, myCalendar.getTimeInMillis() + "");
        map.put(Constant.BIO, binding.etBio.getText().toString());
        FirebaseDatabase.getInstance().getReference().child(Constant.USERS)
                .child(fUser.getUid()).updateChildren(map);
        uploadImage();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == REQUEST_OKE && data != null && data.getData() != null) {
            filePathUri = data.getData();
            binding.ivAva.setImageURI(filePathUri);
        }
    }

    private void uploadImage() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Uploading");
        pd.show();
        if (filePathUri != null) {
            final StorageReference filePath = FirebaseStorage.getInstance().getReference(Constant.POSTS).child(System.currentTimeMillis() + "." + getFileExtension(filePathUri));
            StorageTask uploadTask = filePath.putFile(filePathUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String url = downloadUri.toString();
                        FirebaseDatabase.getInstance().getReference().child(Constant.USERS).child(fUser.getUid()).child("image_url").setValue(url);
                        if (getIntent().getExtras().getBoolean(Constant.CREATED)) {
                            finish();
                        } else {
                            startActivity(new Intent(EditProfileActivity.this, MainActivity.class));
                        }
                        pd.dismiss();
                    } else {
                        Toast.makeText(EditProfileActivity.this, "Upload failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            finish();
            pd.dismiss();
        }
    }

    private void userInfo() {
        FirebaseDatabase.getInstance().getReference().child(Constant.USERS)
                .child(fUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Picasso.get().load(user.getImage_url()).into(binding.ivAva);
                binding.etUserName.setText(user.getUser_name());
                binding.etFullName.setText(user.getFull_name());
                binding.etBio.setText(user.getBio());
                binding.etDob.setText(DateUtils.longToDate(Long.parseLong(user.getBOD())));
                binding.etPhone.setText(user.getPhone_number());
                Log.d("123", user.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private String getFileExtension(Uri uri) {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(getContentResolver().getType(uri));
    }
}