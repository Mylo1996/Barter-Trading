package com.barter_trading_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectedProfileActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView imageViewSelectedProfile;

    private FirebaseAuth firebaseAuth;

    private TextView textViewSelectedName;
    private TextView textViewSelectedRating;
    private RatingBar ratingBarSelected;

    private TextView textViewSelectedCurrentRating;
    private RatingBar ratingBarCurrentSelected;

    private DatabaseReference userDatabaseReference;

    private Button buttonFlag;

    private String selectedUserId;
    private List<String> flagList;


    public Map<String,Integer> rating;
    private float avg = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_profile);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this,LoginActivity.class));
        }
        rating = new HashMap<String,Integer>();

        userDatabaseReference = FirebaseDatabase.getInstance().getReference("userdata");

        imageViewSelectedProfile = findViewById(R.id.imageViewSelectedProfile);

        textViewSelectedName = findViewById(R.id.textViewSelectedName);
        textViewSelectedRating = findViewById(R.id.textViewSelectedRating);
        ratingBarSelected = findViewById(R.id.ratingBarSelected);
        textViewSelectedCurrentRating = findViewById(R.id.textViewSelectedCurrentRating);
        ratingBarCurrentSelected = findViewById(R.id.ratingBarCurrentSelected);

        buttonFlag = findViewById(R.id.buttonFlag);
        buttonFlag.setOnClickListener(this);



        selectedUserId = getIntent().getStringExtra("USER_ID");

        //For setting up the rating by user

        ratingBarSelected.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                rating.put(firebaseAuth.getCurrentUser().getUid(),(int) ratingBar.getRating());
                userDatabaseReference.child(selectedUserId).child("rating").setValue(rating);
                textViewSelectedRating.setText(rating.get(firebaseAuth.getCurrentUser().getUid())+".0/5.0");
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        ValueEventListener userDataListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get UserData object and use the values to update the UI
                FirebaseUser user = firebaseAuth.getCurrentUser();
                UserData userData = dataSnapshot.child(selectedUserId).getValue(UserData.class);
                textViewSelectedName.setText(userData.firstName+" "+userData.sureName+"\n" +userData.flagList.size()+" Flags");

                if(userData.rating !=null) {
                    rating = userData.rating;

                }
                if(userData.rating.containsKey(user.getUid())) {
                    ratingBarSelected.setRating(userData.rating.get(user.getUid()));
                }
                avg = 0;
                float sum = 0;
                float count = 0;
                for (Map.Entry<String, Integer> entry : userData.rating.entrySet()) {
                    System.err.println("record: " + entry.getValue());
                    sum += entry.getValue();
                    count++;
                    avg = sum / count;
                }
                System.err.println("size: " + count);
                System.err.println("average: " + avg);
                System.err.println("average: " + user.getUid());

                ratingBarCurrentSelected.setRating(avg);
                ratingBarCurrentSelected.setIsIndicator(true);
                textViewSelectedCurrentRating.setText((Math.round(avg * 10) / 10.0) + "/5.0");

                Picasso.with(getApplicationContext()).load(userData.profileImageUrl).into(imageViewSelectedProfile);
                flagList = userData.flagList;
                if(flagList.contains(user.getUid())){
                    buttonFlag.setEnabled(false);
                }else{
                    flagList.add(user.getUid());
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting UserData failed
            }
        };
        userDatabaseReference.addValueEventListener(userDataListener);

    }

    @Override
    public void onClick(View v) {
        if(v == buttonFlag && buttonFlag.isEnabled()){
            userDatabaseReference.child(selectedUserId).child("flagList").setValue(flagList);
        }
    }
}
