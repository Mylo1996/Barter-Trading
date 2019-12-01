package com.barter_trading_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class ListItemsActivity extends AppCompatActivity implements ItemAdapter.OnItemClickListener {
    private RecyclerView recyclerView;
    private ItemAdapter itemAdapter;

    private ProgressBar progressCircle;

    private DatabaseReference itemDatabaseReference;
    private List<UploadedItem> itemList;


    private String category;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_items);

        // We can start this activity with two kind of filters:
        //  Category, that filters by categories
        //  User ID, that filters by the user who uploaded the item
        category = getIntent().getStringExtra("CATEGORY");
        userID = getIntent().getStringExtra("USER_ID");

        // The RecyclerView makes the items to be shown in a scrollable list
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressCircle = findViewById(R.id.progressCircle);

        itemList = new ArrayList<>();

        // The RecyclerView needs an adapter that converts the data to items to show
        itemAdapter = new ItemAdapter(ListItemsActivity.this,itemList);

        recyclerView.setAdapter(itemAdapter);

        itemAdapter.setOnItemClickListener(ListItemsActivity.this);


        itemDatabaseReference = FirebaseDatabase.getInstance().getReference("uploadedItem");

        itemDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                itemList.clear();

                for(DataSnapshot postSnapShot : dataSnapshot.getChildren()){
                    UploadedItem uploadedItem = postSnapShot.getValue(UploadedItem.class);

                    uploadedItem.setItemKey(postSnapShot.getKey());
                    System.err.println(category);

                    //check which filter is valid
                    if(category != null && category.equals( uploadedItem.itemCategory ) && !uploadedItem.agreed){
                        itemList.add(uploadedItem);
                    }

                    if(userID != null && userID.equals( uploadedItem.itemUserId) && !uploadedItem.agreed){
                        itemList.add(uploadedItem);
                    }

                }

                itemAdapter.notifyDataSetChanged();

                progressCircle.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Data Connection failed...", Toast.LENGTH_LONG).show();
                progressCircle.setVisibility(View.INVISIBLE);
            }
        });
    }


    // If we touch an item from the list, it will open up the SelectedItemActivity that shows the item details
    // We put the item's generated ID to the started intent as an Extra to let the next Activity know which item is chosen
    @Override
    public void onItemClick(int position) {
        UploadedItem selectedItem = itemList.get(position);
        final String selectedKey = selectedItem.getItemKey();

        Intent intent = new Intent(getBaseContext(), SelectedItemActivity.class);
        intent.putExtra("SELECTED_ITEM_KEY", selectedKey);
        startActivity(intent);

    }

    // If we touch and hold on an item from the list, it will show up a dropdown menu  with the "Agree" button
    // The users only able to agree their own items that removes the item from the list which means that it has been traded
    @Override
    public void onAgreedItemClick(int position) {

        UploadedItem selectedItem = itemList.get(position);
        final String selectedKey = selectedItem.getItemKey();
        itemDatabaseReference.child(selectedKey).child("agreed").setValue(true);

    }
}
