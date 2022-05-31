package com.example.connekt.view.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.connekt.constant.Constant;
import com.example.connekt.databinding.ActivityAddBinding;
import com.example.connekt.model.Post;
import com.example.connekt.model.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class AddActivity extends AppCompatActivity {
    private ActivityAddBinding binding;
    private final int PICK_IMAGE_REQUEST = 22;
    private final int REQUEST_CODE = 1;
    private final int REQUEST_OKE = -1;
    FirebaseStorage storage;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    private Uri filePathUri;
    private String imageUrl;
    private String post_id;
    private Boolean isEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        init();
        selectImage();
        publishPost();
        userInfo();
    }

    private void init() {
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        Bundle intent = getIntent().getExtras();
        post_id = intent.getString(Constant.POST_ID);
        isEdit = intent.getBoolean(Constant.EDIT);
        if (isEdit) {
            binding.tvTitle.setText("Edit Post");
            postInfo();
        } else {
            binding.tvTitle.setText("Create Post");
        }
    }

    private void selectImage() {
        binding.butImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });
    }

    private void publishPost() {
        binding.butPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEdit) {
                    editPost();
                } else {
                    uploadImage();
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

    private void userInfo() {
        FirebaseDatabase.getInstance().getReference().child(Constant.USERS)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                binding.tvUserName.setText(user.getUser_name());
                Picasso.get().load(user.getImage_url()).into(binding.ivAva);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void postInfo() {
        FirebaseDatabase.getInstance().getReference().child(Constant.POSTS)
                .child(post_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);
                binding.etStatus.setText(post.getDescription());
                Picasso.get().load(post.getImage_url()).into(binding.ivImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == REQUEST_OKE && data != null && data.getData() != null) {
            filePathUri = data.getData();
            binding.ivImage.setImageURI(filePathUri);

        }
    }

    private void uploadImage() {
        if (filePathUri != null) {
            final ProgressDialog pd = new ProgressDialog(AddActivity.this);
            pd.setMessage("Uploading");
            pd.show();
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
                    Uri downloadUri = task.getResult();
                    imageUrl = downloadUri.toString();
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Constant.POSTS);
                    String postId = ref.push().getKey();
                    HashMap<String, Object> map = new HashMap<>();
                    map.put(Constant.POST_ID, postId);
                    map.put(Constant.IMAGE_URL, imageUrl);
                    map.put(Constant.DESCRIPTION, binding.etStatus.getText().toString());
                    map.put(Constant.PUBLISHER, FirebaseAuth.getInstance().getCurrentUser().getUid());
                    map.put(Constant.TIME_CREATED, System.currentTimeMillis() + "");
                    ref.child(postId).setValue(map);
                    pd.dismiss();
                    startActivity(new Intent(AddActivity.this, MainActivity.class));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(AddActivity.this, "No image was selected!", Toast.LENGTH_SHORT).show();
        }
    }

    private void editPost() {
        if (filePathUri != null) {
            final ProgressDialog pd = new ProgressDialog(AddActivity.this);
            pd.setMessage("Uploading");
            pd.show();
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
                    Uri downloadUri = task.getResult();
                    imageUrl = downloadUri.toString();
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Constant.POSTS).child(post_id);
                    HashMap<String, Object> map = new HashMap<>();
                    map.put(Constant.DESCRIPTION, binding.etStatus.getText().toString());
                    map.put(Constant.IMAGE_URL, imageUrl);
                    ref.updateChildren(map);
                    pd.dismiss();
                    startActivity(new Intent(AddActivity.this, MainActivity.class));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            databaseReference = FirebaseDatabase.getInstance().getReference(Constant.POSTS).child(post_id);
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put(Constant.DESCRIPTION, binding.etStatus.getText().toString());
            databaseReference.updateChildren(hashMap);
            startActivity(new Intent(AddActivity.this, MainActivity.class));
        }
    }

    private String getFileExtension(Uri uri) {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(AddActivity.this.getContentResolver().getType(uri));
    }
}