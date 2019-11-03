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
    private List<String> userList;
    private FirebaseUser fuser;
    private DatabaseReference reference;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        recyclerView = view.findViewById(R.id.recycler_View);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        userList = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
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

            }
        });

        return view;
    }

    private void readChats() {
        users = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("userdata");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    UserData user = snapshot.getValue(UserData.class);
                    String userId = snapshot.getKey();
                    for(String userid : userList){
                        if(userId.equals(userid)){
                            if(users.size() != 0){
                                List<UserData> tempUsers = new ArrayList<>();
                                tempUsers.addAll(users);
                                for(UserData user1 : tempUsers){
                                    String user1Id = "";
                                    for(DataSnapshot snapshot1 : dataSnapshot.getChildren()){
                                        if(snapshot1.child("profileImageUrl").getValue().equals(user1.profileImageUrl)){
                                            user1Id = snapshot1.getKey();
                                        }
                                    }
                                    if(!userId.equals(user1Id)){
                                        users.add(user1);
                                    }
                                }
                            }else{
                                users.add(user);
                            }
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
        final String selectedKey = userList.get(position);
        Intent intent = new Intent(getContext(), MessageActivity.class);
        intent.putExtra("USER_ID", selectedKey);
        startActivity(intent);
    }
}
