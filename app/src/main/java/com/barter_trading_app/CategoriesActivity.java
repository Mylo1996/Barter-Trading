package com.barter_trading_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class CategoriesActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonGadgets;
    private Button buttonClothes;
    private Button buttonTools;
    private Button buttonBicycles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);


        buttonGadgets = findViewById(R.id.buttonGadgets);
        buttonClothes = findViewById(R.id.buttonClothes);
        buttonTools = findViewById(R.id.buttonTools);
        buttonBicycles = findViewById(R.id.buttonBicycles);

        buttonGadgets.setOnClickListener(this);
        buttonClothes.setOnClickListener(this);
        buttonTools.setOnClickListener(this);
        buttonBicycles.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v == buttonGadgets){
            startActivity(new Intent(getApplicationContext(),ListItemsActivity.class));
        }else if(v == buttonClothes){

        }else if(v == buttonTools){

        }else if(v == buttonBicycles){

        }
    }
}
