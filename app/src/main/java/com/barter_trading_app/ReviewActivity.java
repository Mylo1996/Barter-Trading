package com.barter_trading_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReviewActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView recyclerView;
    private ReviewAdapter reviewAdapter;
    private EditText editTextNewReview;
    private Button buttonAddNewReview;

    private FirebaseAuth firebaseAuth;

    private DatabaseReference userDatabaseReference;
    private List<Review> reviewList;

    private String currentUserName;
    private String selectedUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        selectedUserId = getIntent().getStringExtra("USER_ID");

        System.err.println(selectedUserId);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this,LoginActivity.class));
        }

        editTextNewReview = findViewById(R.id.editTextNewReview);
        buttonAddNewReview = findViewById(R.id.buttonAddNewReview);

        recyclerView = findViewById(R.id.recyclerViewReview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        reviewList = new ArrayList<>();
        reviewAdapter = new ReviewAdapter(ReviewActivity.this,reviewList);
        recyclerView.setAdapter(reviewAdapter);

        userDatabaseReference = FirebaseDatabase.getInstance().getReference("userdata");

        buttonAddNewReview.setOnClickListener(this);


        userDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                FirebaseUser user = firebaseAuth.getCurrentUser();
                UserData currentUserData = dataSnapshot.child(user.getUid()).getValue(UserData.class);
                if(currentUserData != null) {
                    currentUserName = currentUserData.firstName;
                }

                reviewList.clear();
                UserData userData = dataSnapshot.child(selectedUserId).getValue(UserData.class);

                reviewList.addAll(userData.reviewList);
                Collections.reverse(reviewList);
                reviewAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == buttonAddNewReview){
            Review newReview = new Review(currentUserName,editTextNewReview.getText().toString());
            reviewList.add(newReview);
            userDatabaseReference.child(selectedUserId).child("reviewList").setValue(reviewList);
        }
    }

}
