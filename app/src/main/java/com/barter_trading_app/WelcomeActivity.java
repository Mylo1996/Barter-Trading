package com.barter_trading_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonCategories;
    private Button buttonUserProfile;
    private Button buttonWelcomeMessages;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this,LoginActivity.class));
        }

        buttonCategories = findViewById(R.id.buttonCategories);
        buttonUserProfile = findViewById(R.id.buttonUserProfile);
        buttonWelcomeMessages = findViewById(R.id.buttonWelcomeMessages);

        buttonUserProfile.setOnClickListener(this);
        buttonCategories.setOnClickListener(this);
        buttonWelcomeMessages.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v == buttonCategories){
            //Start CategoriesActivity
            startActivity(new Intent(getApplicationContext(),CategoriesActivity.class));
        }else if(v == buttonUserProfile){
            startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
        }else if(v == buttonWelcomeMessages){
            startActivity(new Intent(getApplicationContext(),ChatActivity.class));
        }
    }
}
