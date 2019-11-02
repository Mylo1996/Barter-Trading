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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

        category = getIntent().getStringExtra("CATEGORY");
        userID = getIntent().getStringExtra("USER_ID");

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressCircle = findViewById(R.id.progressCircle);

        itemList = new ArrayList<>();

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
                    if(category != null && category.equals( uploadedItem.itemCategory)){
                        itemList.add(uploadedItem);
                    }

                    if(userID != null && userID.equals( uploadedItem.itemUserId)){
                        itemList.add(uploadedItem);
                    }

                }

                itemAdapter.notifyDataSetChanged();

                progressCircle.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_LONG).show();
                progressCircle.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        UploadedItem selectedItem = itemList.get(position);
        final String selectedKey = selectedItem.getItemKey();


        Intent intent = new Intent(getBaseContext(), SelectedItemActivity.class);
        intent.putExtra("SELECTED_ITEM_KEY", selectedKey);
        startActivity(intent);

    }
}
