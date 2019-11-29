package com.barter_trading_app.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.barter_trading_app.Adapter.UserAdapter;
import com.barter_trading_app.Chat;
import com.barter_trading_app.MessageActivity;
import com.barter_trading_app.R;
import com.barter_trading_app.UserData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class ChatFragment extends Fragment implements UserAdapter.OnUserClickListener{

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;

    private List<UserData> users;
    private List<String> usersID;
    private List<String> userList;
    private FirebaseUser fuser;
    private DatabaseReference referenceChat;
    private DatabaseReference referenceUser;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        recyclerView = view.findViewById(R.id.recycler_View);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        userList = new ArrayList<>();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        referenceChat = FirebaseDatabase.getInstance().getReference("Chats");

        referenceChat.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if(chat.sender.equals(fuser.getUid())){
                        userList.add(chat.receiver);
                    }
                    if(chat.receiver.equals(fuser.getUid())){
                        userList.add(chat.sender);
                    }
                }
                readChats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                readChats();
            }
        });

    }

    private void readChats() {
        users = new ArrayList<>();
        usersID = new ArrayList<>();
        referenceUser = FirebaseDatabase.getInstance().getReference("userdata");

        referenceUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    UserData user = snapshot.getValue(UserData.class);
                    String userId = snapshot.getKey();
                    for(String userid : userList){
                        if(userId.equals(userid)){
                            users.add(user);
                            usersID.add(userId);
                        }
                    }
                }



                userAdapter = new UserAdapter(getContext(),removeDuplicates(users));
                userAdapter.setOnUserClickListener(ChatFragment.this);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    // Function to remove duplicates from an ArrayList
    public static <T> ArrayList<T> removeDuplicates(List<T> list)
    {

        // Create a new ArrayList
        ArrayList<T> newList = new ArrayList<T>();

        // Traverse through the first list
        for (T element : list) {

            // If this element is not present in newList
            // then add it
            if (!newList.contains(element)) {

                newList.add(element);
            }
        }

        // return the new list
        return newList;
    }

    @Override
    public void onItemClick(int position) {
        System.err.println(position);
        final String selectedKey = removeDuplicates(usersID).get(position);
        Intent intent = new Intent(getContext(), MessageActivity.class);
        intent.putExtra("USER_ID", selectedKey);
        startActivity(intent);
    }
}
