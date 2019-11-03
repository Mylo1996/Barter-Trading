package com.barter_trading_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.barter_trading_app.Fragments.ChatFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class SelectedItemActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;

    private ImageView imageViewSelectedItem;
    private TextView textViewSelectedItemName;
    private RatingBar ratingBarSelectedItem;
    private TextView textViewRatingSelectedItem;
    private TextView textViewSelectedItemUserName;
    private TextView textViewSelectedItemDescription;
    private TextView textViewSelectedItemGetUserItems;
    private Button buttonViewReviews;
    private Button buttonMessage;
    private Button buttonVideoCheck;

    private String selectedItemKey;

    private DatabaseReference itemDatabaseReference;
    private DatabaseReference userDatabaseReference;

    private UploadedItem itemData;

    public Map<String,Integer> rating;
    private float avg = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_item);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this,LoginActivity.class));
        }

        rating = new HashMap<String,Integer>();

        itemDatabaseReference = FirebaseDatabase.getInstance().getReference("uploadedItem");
        userDatabaseReference = FirebaseDatabase.getInstance().getReference("userdata");

        selectedItemKey = getIntent().getStringExtra("SELECTED_ITEM_KEY");

        imageViewSelectedItem = findViewById(R.id.imageViewSelectedItem);
        textViewSelectedItemName = findViewById(R.id.textViewSelectedItemName);
        ratingBarSelectedItem = findViewById(R.id.ratingBarSelectedItem);
        textViewRatingSelectedItem = findViewById(R.id.textViewRatingSelectedItem);
        textViewSelectedItemUserName = findViewById(R.id.textViewSelectedItemUserName);
        textViewSelectedItemDescription = findViewById(R.id.textViewSelectedItemDescription);
        textViewSelectedItemGetUserItems = findViewById(R.id.textViewSelectedItemGetUserItems);
        buttonViewReviews = findViewById(R.id.buttonViewReviews);
        buttonMessage = findViewById(R.id.buttonMessage);
        buttonVideoCheck = findViewById(R.id.buttonVideoCheck);

        textViewSelectedItemName.setText(selectedItemKey);

        buttonVideoCheck.setOnClickListener(this);
        textViewSelectedItemUserName.setOnClickListener(this);
        textViewSelectedItemGetUserItems.setOnClickListener(this);
        buttonViewReviews.setOnClickListener(this);
        buttonMessage.setOnClickListener(this);
    }


    @Override
    protected void onStart() {
        super.onStart();
        ValueEventListener itemDataListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get UserData object and use the values to update the UI
                itemData = dataSnapshot.child(selectedItemKey).getValue(UploadedItem.class);
                if(itemData.itemVideoUrl == null){
                    buttonVideoCheck.setEnabled(false);
                }

                textViewSelectedItemName.setText(itemData.itemName);



                ratingBarSelectedItem.setIsIndicator(true);
                textViewRatingSelectedItem.setText((Math.round(avg * 10) / 10.0) + "/5.0");

                textViewSelectedItemDescription.setText(itemData.itemDescription);

                Picasso.with(getApplicationContext()).load(itemData.itemImageUrl).placeholder(R.mipmap.ic_launcher).into(imageViewSelectedItem);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting UserData failed
                Toast.makeText(getApplicationContext(), "Data Connection failed...", Toast.LENGTH_LONG).show();
            }

        };

        ValueEventListener userDataEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(itemData !=null) {
                    UserData userData = dataSnapshot.child(itemData.itemUserId).getValue(UserData.class);
                    if (userData.rating != null) {
                        rating = userData.rating;

                    }
                    if (userData.rating.containsKey(user.getUid())) {
                        ratingBarSelectedItem.setRating(userData.rating.get(user.getUid()));
                    }
                    avg = averageMap(userData.rating);

                    textViewSelectedItemUserName.setText("User: " + userData.firstName + " " + userData.sureName);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Getting UserData failed
                Toast.makeText(getApplicationContext(), "Data Connection failed...", Toast.LENGTH_LONG).show();

            }
        };
        itemDatabaseReference.addValueEventListener(itemDataListener);
        userDatabaseReference.addValueEventListener(userDataEventListener);

    }

    private float averageMap(Map<String, Integer> rating) {
        float avg=0;
        float sum=0;
        float count = 0;
        for (Map.Entry<String, Integer> entry : rating.entrySet()) {
            sum+=entry.getValue();
            count++;
            avg = sum/count;
        }
        return avg;
    }


    @Override
    public void onClick(View v) {
        if(v == textViewSelectedItemGetUserItems){
            Intent intent = new Intent(getBaseContext(), ListItemsActivity.class);
            intent.putExtra("USER_ID", itemData.itemUserId);
            startActivity(intent);
        }else if(v == buttonViewReviews){
            Intent intent = new Intent(getBaseContext(), ReviewActivity.class);
            intent.putExtra("USER_ID", itemData.itemUserId);
            startActivity(intent);
        }else if(v == buttonMessage){
            Intent intent = new Intent(getBaseContext(), MessageActivity.class);
            intent.putExtra("USER_ID", itemData.itemUserId);
            startActivity(intent);
        }else if(v == textViewSelectedItemUserName){
            Intent intent = new Intent(getBaseContext(), SelectedProfileActivity.class);
            intent.putExtra("USER_ID", itemData.itemUserId);
            startActivity(intent);
        }else if(v == buttonVideoCheck){
            Intent intent = new Intent(getBaseContext(), VideoPlayerActivity.class);
            System.err.println(itemData.itemVideoUrl);
            intent.putExtra("VIDEO_URL", itemData.itemVideoUrl);
            startActivity(intent);
        }
    }
}
