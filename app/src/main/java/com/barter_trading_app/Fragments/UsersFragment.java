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
import com.barter_trading_app.ItemAdapter;
import com.barter_trading_app.MessageActivity;
import com.barter_trading_app.R;
import com.barter_trading_app.SelectedItemActivity;
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


public class UsersFragment extends Fragment implements UserAdapter.OnUserClickListener {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<UserData> users;
    private List<String> userIdList;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference userDatabaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_users, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewChatUsers);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));



        users = new ArrayList<>();
        userIdList = new ArrayList<>();
        userAdapter = new UserAdapter(getContext(),users);
        userAdapter.setOnUserClickListener(UsersFragment.this);
        recyclerView.setAdapter(userAdapter);
        readUsers();

        return view;
    }

    private void readUsers() {
        firebaseAuth = FirebaseAuth.getInstance();
        userDatabaseReference = FirebaseDatabase.getInstance().getReference("userdata");

        userDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users.clear();
                userIdList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    UserData userData =  snapshot.getValue(UserData.class);

                    userIdList.add(snapshot.getKey());
                    assert userData != null;
                    assert firebaseAuth != null;

                    if(!snapshot.getKey().equals(firebaseAuth.getUid())){
                        users.add(userData);
                    }

                }
                userAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onItemClick(int position) {
        final String selectedKey = userIdList.get(position);
        Intent intent = new Intent(getContext(), MessageActivity.class);
        intent.putExtra("USER_ID", selectedKey);
        startActivity(intent);
    }
}
