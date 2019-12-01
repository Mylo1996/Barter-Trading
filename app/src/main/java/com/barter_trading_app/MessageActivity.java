package com.barter_trading_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.barter_trading_app.Adapter.MessageAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MessageActivity extends AppCompatActivity {

    private CircleImageView profileImage;
    private TextView userName;
    private Toolbar toolbar;

    private ImageButton btn_send;
    private EditText text_send;

    private DatabaseReference userDatabaseReference;
    private DatabaseReference chatDatabaseReference;

    MessageAdapter messageAdapter;
    List<Chat> chats;

    RecyclerView recyclerView;

    private FirebaseUser fuser;
    private Intent intent;

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        profileImage = findViewById(R.id.usersProfileCircle);
        userName = findViewById(R.id.username);
        btn_send = findViewById(R.id.btn_send);
        text_send = findViewById(R.id.text_send);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = text_send.getText().toString();
                if(!msg.equals("")){
                    sendMessage(FirebaseAuth.getInstance().getCurrentUser().getUid(),userId,msg);
                }else{
                    Toast.makeText(getApplicationContext(),"You can't send empty message...",Toast.LENGTH_LONG).show();
                }
                text_send.setText("");
            }
        });

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        intent = getIntent();
        userId = intent.getStringExtra("USER_ID");
        userDatabaseReference = FirebaseDatabase.getInstance().getReference("userdata").child(userId);


        userDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserData user = dataSnapshot.getValue(UserData.class);
                userName.setText(user.firstName+ " "+user.sureName);
                Picasso.with(getApplicationContext()).load(user.profileImageUrl).placeholder(R.mipmap.ic_launcher).into(profileImage);
                readMessage(fuser.getUid(),userId,user.profileImageUrl);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Data Connection failed...", Toast.LENGTH_LONG).show();
            }
        });

    }

    // Create a new record in the "Chats" data table
    private void sendMessage(String sender,String receiver, String message){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        HashMap<String,Object> hashMap = new HashMap<>();

        hashMap.put("sender",sender);
        hashMap.put("receiver",receiver);
        hashMap.put("message",message);
        String uploadId = chatDatabaseReference.push().getKey();
        databaseReference.child("Chats").child(uploadId).setValue(hashMap);

    }

    // Get all the messages from the "Chats" data table from the selected user
    private void readMessage(final String myid, final String userid, final String imageurl){
        chats = new ArrayList<>();
        chatDatabaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        chatDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chats.clear();
                for( DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if(chat.receiver.equals(myid) && chat.sender.equals(userid) || chat.receiver.equals(userid) && chat.sender.equals(myid)){
                        chats.add(chat);
                    }
                    messageAdapter = new MessageAdapter(MessageActivity.this,chats,imageurl);
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Data Connection failed...", Toast.LENGTH_LONG).show();
            }
        });
    }

}
