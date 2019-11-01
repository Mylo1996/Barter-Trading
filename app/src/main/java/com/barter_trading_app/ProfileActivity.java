package com.barter_trading_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;

    private TextView textViewName;
    private TextView textViewRating;
    private Button buttonLogout;
    private RatingBar ratingBar;

    private DatabaseReference userDatabaseReference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this,LoginActivity.class));
        }

        userDatabaseReference = FirebaseDatabase.getInstance().getReference("userdata");

        textViewName = findViewById(R.id.textViewName);
        textViewRating = findViewById(R.id.textViewRating);
        ratingBar = findViewById(R.id.ratingBar);

        buttonLogout = findViewById(R.id.buttonLogout);

        buttonLogout.setOnClickListener(this);



        //For setting up the rating by user
        /*
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                textViewRating.setText(String.valueOf((int) ratingBar.getRating())+"/5");
            }
        });
        */
    }




    @Override
    protected void onStart() {
        super.onStart();
        ValueEventListener userDataListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get UserData object and use the values to update the UI
                FirebaseUser user = firebaseAuth.getCurrentUser();
                UserData userData = dataSnapshot.child(user.getUid()).getValue(UserData.class);
                textViewName.setText(userData.firstName+" "+userData.sureName+"\n"+user.getEmail());
                ratingBar.setRating(userData.rating);
                ratingBar.setIsIndicator(true);
                textViewRating.setText(String.valueOf(userData.rating)+"/5");
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
        if(v == buttonLogout){
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(this,LoginActivity.class));
        }

    }
}
