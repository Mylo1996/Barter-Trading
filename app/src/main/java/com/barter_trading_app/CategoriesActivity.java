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

    // Open up the ListItemsActivity with the chosen category filter
    // Put the category to the starter intent as an Extra to let the next Activity know the filter
    @Override
    public void onClick(View v) {
        if(v == buttonGadgets){
            Intent intent = new Intent(getBaseContext(), ListItemsActivity.class);
            intent.putExtra("CATEGORY", "Gadgets");
            startActivity(intent);
        }else if(v == buttonClothes){
            Intent intent = new Intent(getBaseContext(), ListItemsActivity.class);
            intent.putExtra("CATEGORY", "Clothes");
            startActivity(intent);
        }else if(v == buttonTools){
            Intent intent = new Intent(getBaseContext(), ListItemsActivity.class);
            intent.putExtra("CATEGORY", "Tools");
            startActivity(intent);
        }else if(v == buttonBicycles){
            Intent intent = new Intent(getBaseContext(), ListItemsActivity.class);
            intent.putExtra("CATEGORY", "Bicycles");
            startActivity(intent);
        }
    }
}
