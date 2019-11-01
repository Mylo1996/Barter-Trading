package com.barter_trading_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonCategories;
    private Button buttonUserProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        buttonCategories = findViewById(R.id.buttonCategories);
        buttonUserProfile = findViewById(R.id.buttonUserProfile);

        buttonUserProfile.setOnClickListener(this);
        buttonCategories.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v == buttonCategories){
            //Start CategoriesActivity
            startActivity(new Intent(getApplicationContext(),CategoriesActivity.class));
        }else if(v == buttonUserProfile){
            startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
        }
    }
}
