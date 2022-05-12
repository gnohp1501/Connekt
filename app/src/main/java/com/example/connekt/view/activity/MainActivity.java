package com.example.connekt.view.activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.connekt.R;
import com.example.connekt.constant.Constant;
import com.example.connekt.view.fragment.AddFragment;
import com.example.connekt.view.fragment.FavoriteFragment;
import com.example.connekt.view.fragment.HomeFragment;
import com.example.connekt.view.fragment.PersonFragment;
import com.example.connekt.view.fragment.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private Fragment selectorFragment;
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference(Constant.USERS).child(firebaseUser.getUid());
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.nav_home:
                        selectorFragment = new HomeFragment();
                        break;

                    case R.id.nav_search:
                        selectorFragment = new SearchFragment();
                        break;

                    case R.id.nav_add:
                        selectorFragment = new AddFragment();
                        break;

                    case R.id.nav_heart:
                        selectorFragment = new FavoriteFragment();
                        break;

                    case R.id.nav_profile:
                        selectorFragment = new PersonFragment();
                        break;
                }
                if (selectorFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectorFragment).commit();
                }

                return true;

            }
        });
        Bundle intent = getIntent().getExtras();
        if (intent != null) {
            String profileId = intent.getString(Constant.PUBLISHER_ID);
            getSharedPreferences(Constant.PROFILE, MODE_PRIVATE).edit().putString(Constant.PROFILE_ID, profileId).apply();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PersonFragment()).commit();
            bottomNavigationView.setSelectedItemId(R.id.nav_profile);
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        }
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
        status("offline");
        lastSeen(System.currentTimeMillis() + "");
    }
}