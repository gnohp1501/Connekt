package com.example.connekt.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.connekt.R;
import com.example.connekt.constant.Constant;
import com.example.connekt.view.fragment.FavoriteFragment;
import com.example.connekt.view.fragment.HomeFragment;
import com.example.connekt.view.fragment.PersonFragment;
import com.example.connekt.view.fragment.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;
    private BottomNavigationView bottomNavigationView;
    private Fragment selectorFragment;

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
                        selectorFragment = null;
                        Intent intent = new Intent(MainActivity.this, AddActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putBoolean(Constant.EDIT, false);
                        intent.putExtras(bundle);
                        startActivity(intent);
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
}